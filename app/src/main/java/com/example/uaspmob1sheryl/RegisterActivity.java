package com.example.uaspmob1sheryl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    EditText editUsername, editPass, reEditPass;
    Button buttonRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editUsername = findViewById(R.id.editUsername);
        editPass = findViewById(R.id.editPass);
        reEditPass = findViewById(R.id.reEditPass);
        buttonRegister = findViewById(R.id.buttonRegister);
        dbHelper = new DBHelper(this);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user, pass, rePass;
                user = editUsername.getText().toString();
                pass = editPass.getText().toString();
                rePass = reEditPass.getText().toString();
                String berhasil = "Pendaftaran berhasil untuk pengguna: " + user;
                if (user.isEmpty() || pass.isEmpty() || rePass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Tolong isi semua bagian", Toast.LENGTH_LONG).show();
                } else {
                    if (pass.equals(rePass)) {
                        if (dbHelper.checkUsername(user)) {
                            Toast.makeText(RegisterActivity.this, "Username sudah ada", Toast.LENGTH_LONG).show();
                            return;
                        }
                        boolean successRegister = dbHelper.insertData(user, pass);
                        if (successRegister) {
                            Log.v("Sukses", berhasil);
                            Toast.makeText(RegisterActivity.this, "Register telah berhasil", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register gagal", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}