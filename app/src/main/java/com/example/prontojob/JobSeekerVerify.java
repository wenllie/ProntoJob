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
import android.widget.ImageButton;
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

public class JobSeekerVerify extends AppCompatActivity implements View.OnClickListener {

    //Declare
    EditText jsBirthday, jsAge, jsPhoneNumber, jsAddress;
    Button jsDate, btnVerify, jsResume, cameraBtn, galleryBtn;
    ;
    Spinner jsExperience, jsIDType;
    private int mYear, mMonth, mDay;
    public static String userID;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage;
    String currentPhotoPath;
    private Uri filepath;

    private final int PICK_PDF_CODE = 2342;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_verify);

        //initialize variables
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //user information
        jsBirthday = (EditText) findViewById(R.id.jsBirthday);
        jsAge = (EditText) findViewById(R.id.jsAge);
        jsPhoneNumber = (EditText) findViewById(R.id.jsphonenumber);
        jsAddress = (EditText) findViewById(R.id.jsAddress);
        jsExperience = (Spinner) findViewById(R.id.jsExperience);
        jsIDType = (Spinner) findViewById(R.id.jsIDType);
        jsDate = (Button) findViewById(R.id.jsButtonDate);
        btnVerify = (Button) findViewById(R.id.jsbtnVerify);
        jsResume = (Button) findViewById(R.id.jsResume);
        selectedImage = (ImageView) findViewById(R.id.displayImageView);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        galleryBtn = (Button) findViewById(R.id.galleryBtn);

        //Function to use the buttons
        btnVerify.setOnClickListener(this);
        jsDate.setOnClickListener(this);
        jsResume.setOnClickListener(this);

        ArrayAdapter<CharSequence> experienceAdapter = ArrayAdapter.createFromResource(this,
                R.array.experience, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        jsExperience.setAdapter(experienceAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.id_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        jsIDType.setAdapter(typeAdapter);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermissions();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jsbtnVerify:
                verifyUser();
                break;
            case R.id.jsResume:
                chooseDoc();
                break;
            case R.id.jsButtonDate:
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

                                jsBirthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
        }
    }

    //Method for select image/camera
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    //Get the permission to use camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method for uploading the images to firebase
    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("Job Seeker's Government ID").child(userID).child("images/" + name);
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
                Toast.makeText(JobSeekerVerify.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //get the file extension
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    //create the image file in the database
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

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

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
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
                selectedImage.setImageURI(contentUri);

                uploadImageToFirebase(imageFileName, contentUri);

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
            StorageReference sref = storageReference.child("Job Seeker's Resume").child(userID).child("Resume" + formattedDate + ".pdf");

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(JobSeekerVerify.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(JobSeekerVerify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    //Method for verifying the using through firebase email authentication
    private void verifyUser() {
        String birthday = jsBirthday.getText().toString();
        String age = jsAge.getText().toString();
        String phoneNumber = jsPhoneNumber.getText().toString();
        String address = jsAddress.getText().toString();
        String exp = jsExperience.getSelectedItem().toString();
        String idTy = jsIDType.getSelectedItem().toString();

        //verify that edit text fields are not empty
        if (birthday.isEmpty()) {
            jsBirthday.setError("First name is required!");
            jsBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            jsAge.setError("First name is required!");
            jsAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            jsPhoneNumber.setError("First name is required!");
            jsPhoneNumber.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            jsAddress.setError("First name is required!");
            jsAddress.requestFocus();
            return;
        }

        //
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Job Seekers");

        reference.child(userID).child("Information").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    reference.child(userID).child("Information").child("Birthday").push();
                    reference.child(userID).child("Information").child("Age").push();
                    reference.child(userID).child("Information").child("Phone Number").push();
                    reference.child(userID).child("Information").child("Address").push();
                    reference.child(userID).child("Information").child("Experience").push();
                    reference.child(userID).child("Information").child("ID Type").push();

                    reference.child(userID).child("Information").child("Birthday").setValue(birthday);
                    reference.child(userID).child("Information").child("Age").setValue(age);
                    reference.child(userID).child("Information").child("Phone Number").setValue(phoneNumber);
                    reference.child(userID).child("Information").child("Address").setValue(address);
                    reference.child(userID).child("Information").child("Experience").setValue(exp);
                    reference.child(userID).child("Information").child("ID Type").setValue(idTy);

                    startActivity(new Intent(JobSeekerVerify.this, JobseekerHome.class));
                    finish();
                }
            }
        });
    }
}