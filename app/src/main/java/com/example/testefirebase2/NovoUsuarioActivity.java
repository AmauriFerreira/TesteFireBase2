package com.example.testefirebase2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class NovoUsuarioActivity extends AppCompatActivity {

    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private ImageView pictureImageView;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;

    private static final int REQ_CODE_CAMERA = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);
        loginNovoUsuarioEditText =
                findViewById(R.id.loginNovoUsuarioEditText);
        senhaNovoUsuarioEditText =
                findViewById(R.id.senhaNovoUsuarioEditText);
        pictureImageView =
                findViewById(R.id.pictureImageView);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void criarNovoUsuario (View v){
        String email =
                loginNovoUsuarioEditText.
                        getEditableText().toString();
        String senha =
                senhaNovoUsuarioEditText.
                        getEditableText().toString();
        firebaseAuth.createUserWithEmailAndPassword(
                email,
                senha
        ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(
                        NovoUsuarioActivity.this,
                        getString(
                                R.string.cadastro_funcionou,
                                authResult.
                                        getUser().
                                        getDisplayName().
                                        toString()
                        ),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        NovoUsuarioActivity.this,
                        getString(
                                R.string.erro_inesperado
                        ),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
    }


    public void tirarFoto (View view){
        if (loginNovoUsuarioEditText.getText() != null &&
                !loginNovoUsuarioEditText.getText().toString().isEmpty()){
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            //dá pra tirar foto?
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQ_CODE_CAMERA);
            }
            else{
                Toast.makeText(
                        this,
                        getString(R.string.cant_take_pic),
                        Toast.LENGTH_SHORT
                ).show();
            }

        }
        else{
            Toast.makeText(
                    this,
                    getString(R.string.no_email_no_pic),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void uploadPicture (Bitmap picture){
        StorageReference pictureStorageReference =
                FirebaseStorage.
                        getInstance().
                        getReference(
                                String.format(
                                        Locale.getDefault(),
                                        "images/%s/profilePic.jpg",
                                        loginNovoUsuarioEditText.
                                                getText().
                                                toString().
                                                replace("@", "")
                                )

                        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();
        //aqui foi feito o upload
        pictureStorageReference.putBytes(bytes);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data) {

        if (requestCode == REQ_CODE_CAMERA){
            if (resultCode == Activity.RESULT_OK){
                Bitmap picture = (Bitmap)
                        data.getExtras().get("data");
                uploadPicture(picture);
                pictureImageView.setImageBitmap(picture);

            }
            else{
                Toast.makeText(this,
                        getString(R.string.no_pic_taken),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}