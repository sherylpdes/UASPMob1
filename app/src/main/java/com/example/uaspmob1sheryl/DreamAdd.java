package com.example.uaspmob1sheryl;

import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.annotation.Nullable;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class DreamAdd extends AppCompatActivity {
    String id ="", name, desc, image;

    private AppBarConfiguration appBarConfiguration;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nama, deskripsi;
    private ImageView imageView;
    private Button saveDream, chooseImage;
    private Uri imageUri;
    private FirebaseFirestore dbDream;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_add); // Set the content view

        dbDream = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        nama = findViewById(R.id.nama);
        deskripsi = findViewById(R.id.deskripsi);
        imageView = findViewById(R.id.imageView);
        saveDream = findViewById(R.id.btnAdd);
        chooseImage = findViewById(R.id.btnChooseImage);

        progressDialog = new ProgressDialog(DreamAdd.this);
        progressDialog.setTitle("Loading");

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveDream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dreamNama = nama.getText().toString().trim();
                String dreamDeskripsi = deskripsi.getText().toString().trim();

                if (dreamNama.isEmpty() || dreamDeskripsi.isEmpty()){
                    Toast.makeText(DreamAdd.this, "Nama and Deskripsi cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();

                if(imageUri != null){
                    uploadImageToStorage(dreamNama, dreamDeskripsi);
                } else{
                    saveData(dreamNama, dreamDeskripsi, image);
                }
            }
        });

        Intent updateOption = getIntent();
        if(updateOption != null){
            id = updateOption.getStringExtra("id");
            name = updateOption.getStringExtra("nama");
            desc = updateOption.getStringExtra("deskripsi");
            image = updateOption.getStringExtra("imageUrl");

            nama.setText(name);
            deskripsi.setText(desc);

            if (image != null && !image.isEmpty()) {
                Log.d("DreamAdd", "Loading image: " + image);
                Glide.with(this).load(image).into(imageView);
            } else {
                Log.d("DreamAdd", "No image URL provided");
            }
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToStorage(String dreamNama, String dreamDeskripsi){
        if(imageUri != null){
            StorageReference storageRef = storage.getReference().child("dream_images/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveData(dreamNama, dreamDeskripsi, imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(DreamAdd.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveData(String dreamNama, String dreamDeskripsi, String imageUrl) {
        Map<String, Object> dream = new HashMap<>();
        dream.put("nama", dreamNama);
        dream.put("deskripsi", dreamDeskripsi);
        dream.put("imageUrl", imageUrl);

        if (id != null && !id.isEmpty()) {
            dbDream.collection("dream").document(id)
                    .update(dream)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(DreamAdd.this, "Document updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(DreamAdd.this, "Error updating news: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("DreamAdd", "Error updating document", e);
                    });
        } else {
            dbDream.collection("dream")
                    .add(dream)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(DreamAdd.this, "File added successfully", Toast.LENGTH_SHORT).show();
                        nama.setText("");
                        deskripsi.setText("");
                        imageView.setImageResource(0);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(DreamAdd.this, "Error adding file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("DreamAdd", "Error adding document", e);
                    });
        }
    }
}