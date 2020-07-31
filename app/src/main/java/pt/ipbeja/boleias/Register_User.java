package pt.ipbeja.boleias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import me.anwarshahriar.calligrapher.Calligrapher;
import pt.ipbeja.boleias.Model.User;

public class Register_User extends AppCompatActivity {

    private RelativeLayout rootLayout;
    private Button btnSave;

    private ImageView mPhoto;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 100;

    private Bitmap PostPhotoBitmap = null;
    Uri pickedImg = null;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference users;

    /**
     * 
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__user);

        Toolbar toolbar = findViewById(R.id.toolbar_alt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users"); //table users


        btnSave = (Button)findViewById(R.id.btnConfirmRegister);
        mPhoto = findViewById(R.id.userImage);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        //when click on button save register
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //registUser();

                //btnSave.setVisibility(View.INVISIBLE);

                MaterialEditText edtEmail = findViewById(R.id.edtEmail);
                MaterialEditText edtPassword = findViewById(R.id.edtPassword);
                MaterialEditText edtName = findViewById(R.id.edtName);
                MaterialEditText edtPhone = findViewById(R.id.edtPhone);

                final String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();
                final String name = edtName.getText().toString();
                final String phone = edtPhone.getText().toString();

                //if fields are empty show message, else create user
                if(email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty())
                {

                    showMessage(getString(R.string.alert_fields));


                }
                else {

                    CreateUserAccount(email, password, name, phone);
                }


            }
        });

    }

    private void CreateUserAccount(final String email, final String password, final String name, final String phone) {

        if(pickedImg == null){
            pickedImg = getImageUri(getApplicationContext(), PostPhotoBitmap);
        }

        //final Uri imageUri = getImageUri(getApplicationContext(), PostPhotoBitmap);

        //create authentication for user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {

                            //save data from user to database storage
                            //showMessage("Registado com sucesso !!!");
                            User user = new User();
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setName(name);
                            user.setPhone(phone);

                            //uploadPhoto(name);
                            //updateUserPhoto(name, auth.getCurrentUser());


                            //update photo of imageView
                            updateUserInfo(name, pickedImg, auth.getCurrentUser());

                            //associate user authentication to set user value to database storage (table users)
                            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showMessage(getString(R.string.regist_success));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessage(getString(R.string.regist_fail) + e.getMessage());
                                        }
                                    });



                        }
                        else {

                            showMessage(getString(R.string.regist_fail) + task.getException().getMessage());
                        }


                    }
                });

    }

    //show message with string
    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
    }


    /*
    *Update user photo to datastorage from firebase
    * associate photo to user authentication
     */
    private void updateUserInfo(final String name,Uri pickedImg, final FirebaseUser currentUser){

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImg.getLastPathSegment());
        imageFilePath.putFile(pickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        showMessage(getString(R.string.regist_complete));
                                        updateUI();
                                    }
                                });

                    }
                });

            }
        });


    }

    //go to home activity
    private void updateUI() {

        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
        finish();

    }

    //select options to request image
    //option camera
    //option gallery
    //cancel
    private void SelectImage() {

        final CharSequence[] items = {getString(R.string.camara), getString(R.string.gallery),getString(R.string.btn_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(Register_User.this);
        builder.setTitle(R.string.add_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //if select camera go to phone capture
                if (items[i].equals(getString(R.string.camara))){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    }

                    //if select gallery go to phone gallery
                }else if (items[i].equals(getString(R.string.gallery))){

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), GALLERY_REQUEST_CODE);

                    //cancel options
                }else if (items[i].equals(getString(R.string.btn_cancel))){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //save gallery photo in imageView
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE)
        {
            pickedImg = data.getData();
            mPhoto.setImageURI(pickedImg);



        }

        //save photo in imageview
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {

            //pickedImg = data.getData();
            //mPhoto.setImageURI(pickedImg);
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap)extras.get("data");
            mPhoto.setImageBitmap(image);

            PostPhotoBitmap = image;


        }

        mPhoto.setImageURI(pickedImg);


    }

    //get type URI from image capture
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }*/
}
