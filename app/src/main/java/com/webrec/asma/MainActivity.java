package com.webrec.asma;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    EditText name, contact1, contact2;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Permissions
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        name = findViewById(R.id.name);
        contact1 = findViewById(R.id.contact1);
        contact2 = findViewById(R.id.contact2);
        save = findViewById(R.id.save);

        // Checking if it's authenticated
        SharedPreferences sp_get = getSharedPreferences("Database", MODE_PRIVATE);
        if(!sp_get.getBoolean("call", false)) {
            authenticate();
        }
        else{
            name.setText(sp_get.getString("Name", "Name"));
            contact1.setText(sp_get.getString("Contact1", "Contact1"));
            contact2.setText(sp_get.getString("Contact2", "Contact2"));
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sname = name.getText().toString();
                String scontact1 = contact1.getText().toString();
                String scontact2 = contact2.getText().toString();

                SharedPreferences sp_put = getSharedPreferences("Database", MODE_PRIVATE);
                SharedPreferences.Editor sp_editor = sp_put.edit();

                if(TextUtils.isEmpty(sname)||TextUtils.isEmpty(scontact1)||TextUtils.isEmpty(scontact2)){
                    Toast.makeText(MainActivity.this, "Incomplete or Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Saving credentials
                    sp_editor.putString("Name", sname);
                    sp_editor.putString("Contact1", scontact1);
                    sp_editor.putString("Contact2", scontact2);

                    sp_editor.putBoolean("Verified", true);
                    sp_editor.apply();

                    Toast.makeText(MainActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();

                    // Authenticating User - Details are saved
                    authenticate();
                }
            }
        });
    }

    public void authenticate(){
        SharedPreferences sp_get = getSharedPreferences("Database", MODE_PRIVATE);
        if(sp_get.getBoolean("Verified", false)){
            Intent intent = new Intent(MainActivity.this,SMSActivity.class);
            startActivity(intent);
            finish();
        }
    }
}