package com.example.prontojob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EmployerVerify extends AppCompatActivity implements View.OnClickListener {

    //declare
    EditText eBirthday, eAge, ePhoneNumber, eAddress;
    Button eDate, btnVerify, btnComCamera, btnComGallery, btnIDCam, btnIDGallery, eEmpCert;
    Spinner eIDType;
    private int mYear, mMonth, mDay;
    public static String eUserID;
    ImageView selectedCom, selectedID;
    String currentPhotoPath;
    private Uri filepath;
    private final int PICK_PDF_CODE = 2342;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    public static final int GALLERY_REQUEST_CODE = 105;
    static String idType;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_verify);

        //initialize variables
        eBirthday = (EditText) findViewById(R.id.eBirthday);
        eAge = (EditText) findViewById(R.id.eAge);
        ePhoneNumber = (EditText) findViewById(R.id.ephonenumber);
        eAddress = (EditText) findViewById(R.id.eAddress);
        eIDType = (Spinner) findViewById(R.id.eIDType);
        eDate = (Button) findViewById(R.id.eButtonDate);
        btnVerify = (Button) findViewById(R.id.ebtnVerify);
        btnComCamera = (Button) findViewById(R.id.comCameraBtn);
        btnComGallery = (Button) findViewById(R.id.comGalleryBtn);
        btnIDCam = (Button) findViewById(R.id.eIDCameraBtn);
        btnIDGallery = (Button) findViewById(R.id.eIDGalleryBtn);
        selectedCom = (ImageView) findViewById(R.id.displayComID);
        selectedID = (ImageView) findViewById(R.id.displayEID);
        eEmpCert = (Button) findViewById(R.id.eRegEmpCert);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //function to use the buttons
        btnVerify.setOnClickListener(this);
        eDate.setOnClickListener(this);
        btnComCamera.setOnClickListener(this);
        btnIDCam.setOnClickListener(this);
        eEmpCert.setOnClickListener(this);
        
        //function for getting the image from gallery of the user
        btnComGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent comGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(comGallery, GALLERY_REQUEST_CODE);
                idType = "company";
            }
        });
        btnIDGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eIDGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(eIDGallery, GALLERY_REQUEST_CODE);
                idType = "government";
            }
        });

        ArrayAdapter<CharSequence> idAdapter = ArrayAdapter.createFromResource(this,
                R.array.id_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        eIDType.setAdapter(idAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ebtnVerify:
                employerVerify();
                break;
            case R.id.comCameraBtn:
                askCameraPermission();
                idType = "company";
                break;
            case R.id.eIDCameraBtn:
                askCameraPermission();
                idType = "government";
                break;
            case R.id.eRegEmpCert:
                chooseDoc();
                break;
            case R.id.eButtonDate:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                eBirthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    //method for asking camera permission for the company ID
    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            comDispatchTakePictureIntent();
        }
    }

    //Get the permission to use camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                comDispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method for uploading the images to firebase
    private void uploadImageToFirebase(String name, Uri contentUri) {

        StorageReference image;


        if (idType.equals("government")){
            image = storageReference.child("Employer's Government ID").child(eUserID).child("images/" + name);
        } else {
            image = storageReference.child("Employer's Company ID").child(eUserID).child("images/" + name);
        }

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URI is" + uri.toString());
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EmployerVerify.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the file extension
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void comDispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {
                // Error occurred while creating the File
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    //Method for selecting PDF file
    private void chooseDoc() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_PDF_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                filepath = data.getData();
                UploadFile();
            } else
                Toast.makeText(this, "NO FILE CHOSEN", Toast.LENGTH_SHORT).show();

        }

        if (idType.equals("government")){
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    File f = new File(currentPhotoPath);
                    selectedID.setImageURI(Uri.fromFile(f));
                    Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);

                    uploadImageToFirebase(f.getName(), contentUri);
                }

            }
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contentUri = data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                    Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
                    selectedID.setImageURI(contentUri);

                    uploadImageToFirebase(imageFileName, contentUri);

                }

            }
        } else {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    File f = new File(currentPhotoPath);
                    selectedCom.setImageURI(Uri.fromFile(f));
                    Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);

                    uploadImageToFirebase(f.getName(), contentUri);
                }

            }
            if (requestCode == GALLERY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contentUri = data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                    Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
                    selectedCom.setImageURI(contentUri);

                    uploadImageToFirebase(imageFileName, contentUri);

                }

            }

        }

    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private void UploadFile() {
        if (filepath != null) {

            Date dateObject = new Date(System.currentTimeMillis());
            String formattedDate = formatDate(dateObject);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference sref = storageReference.child("Employer's COE").child(eUserID).child("Resume" + formattedDate + ".pdf");

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EmployerVerify.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EmployerVerify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot
                                    .getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    //function for verifying user
    private void employerVerify() {

        String birthday = eBirthday.getText().toString();
        String age = eAge.getText().toString();
        String phoneNumber = ePhoneNumber.getText().toString();
        String address = eAddress.getText().toString();
        String idTy = eIDType.getSelectedItem().toString();

        //verify that edit text fields are not empty
        if (birthday.isEmpty()) {
            eBirthday.setError("Birthday is required!");
            eBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            eAge.setError("Age is required!");
            eAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            ePhoneNumber.setError("Phone Number is required!");
            ePhoneNumber.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            eAddress.setError("Address is required!");
            eAddress.requestFocus();
            return;
        }

        //
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Employers");

        reference.child(eUserID).child("Information").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    reference.child(eUserID).child("Information").child("Birthday").push();
                    reference.child(eUserID).child("Information").child("Age").push();
                    reference.child(eUserID).child("Information").child("Phone Number").push();
                    reference.child(eUserID).child("Information").child("Address").push();
                    reference.child(eUserID).child("Information").child("ID Type").push();

                    reference.child(eUserID).child("Information").child("Birthday").setValue(birthday);
                    reference.child(eUserID).child("Information").child("Age").setValue(age);
                    reference.child(eUserID).child("Information").child("Phone Number").setValue(phoneNumber);
                    reference.child(eUserID).child("Information").child("Address").setValue(address);
                    reference.child(eUserID).child("Information").child("ID Type").setValue(idTy);

                    startActivity(new Intent(EmployerVerify.this, EmployerLogin.class));
                    finish();
                }
            }
        });
    }
}