package com.webrec.asma;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SMSActivity extends AppCompatActivity {
    ImageView user;
    EditText message;
    Button send;
    FusedLocationProviderClient flpc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_m_s);

        flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        SharedPreferences sp_put = getSharedPreferences("Database", MODE_PRIVATE);
        SharedPreferences.Editor sp_editor = sp_put.edit();
        sp_editor.putBoolean("call", true);
        sp_editor.apply();

        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SMSActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        message = findViewById(R.id.message);
        message.setText(getString(R.string.msg));

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                // Check For Permissions
                if (!checkPermissions())
                    return;
                SharedPreferences sp_get = getSharedPreferences("Database", MODE_PRIVATE);

                // Accessing Location
                String msg = message.getText().toString() + location();
                String contact1 = sp_get.getString("Contact1", "Contact1");
                String contact2 = sp_get.getString("Contact2", "Contact2");
                message.setText(msg);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contact1,null,msg, null, null);
                    smsManager.sendTextMessage(contact2,null,msg, null, null);

                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Message Not Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Boolean checkPermissions() {
        int permission_check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) + ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission_check != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(SMSActivity.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(SMSActivity.this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(SMSActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            return false;
        } else
            return true;
    }

    @SuppressLint("MissingPermission")
    public String location() {
        final String[] address = new String[1];
        address[0] = " Not Found";

        flpc.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        address[0] = "\nLocality : " + addresses.get(0).getLocality() + "\nAddress : " + addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return address[0];
    }
}