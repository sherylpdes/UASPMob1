package com.example.uaspmob1sheryl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class DreamDetail extends AppCompatActivity {
    TextView dreamNama, dreamDeskripsi;
    ImageView dreamImage;
    Button edit, hapus;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_detail); // Ensure this matches your layout file name

        dreamNama = findViewById(R.id.dreamNama);
        dreamDeskripsi = findViewById(R.id.dreamDeskripsi);
        dreamImage = findViewById(R.id.dreamImage);
        edit = findViewById(R.id.editButton);
        hapus = findViewById(R.id.deleteButton);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String nama = intent.getStringExtra("nama");
        String deskripsi = intent.getStringExtra("deskripsi");
        String imageUrl = intent.getStringExtra("imageUrl");

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DreamDetail.this, DreamAdd.class);
                intent.putExtra("id", id);
                intent.putExtra("nama", nama);
                intent.putExtra("deskripsi", deskripsi);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
            }
        });

        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("dream").document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(DreamDetail.this, "Document deleted successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DreamDetail.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DreamDetail.this, "Error deleting: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.w("DreamDetail", "Error deleting document", e);
                        });
            }
        });

        dreamNama.setText(nama);
        dreamDeskripsi.setText(deskripsi);
        Glide.with(this).load(imageUrl).into(dreamImage);
    }
}