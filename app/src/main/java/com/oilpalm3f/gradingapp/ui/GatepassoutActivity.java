package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;

public class GatepassoutActivity extends AppCompatActivity {
    private static final String LOG_TAG = GatepassinActivity.class.getName();
    String qrvalue;
    String[] splitString;
    private DataAccessHandler dataAccessHandler;
    Button   submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatepassout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gate Pass Out");
        setSupportActionBar(toolbar);
        intviews();
        Setviews();
    }

    private void intviews() {
        dataAccessHandler = new DataAccessHandler(GatepassoutActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrvalue = extras.getString("qrvalue");
            Log.d("QR Code Value is", qrvalue + "");
        }
        submit = findViewById(R.id.gatepassoutsubmit);
    }
    private void Setviews() {
//        splitString = qrvalue.split("/");
//
//        Log.d("Length", splitString.length  + "");
//        Log.d("Length", splitString.length  + "");
//
//        Log.d("String1", splitString[0] + "");
//        Log.d("String2", splitString[1] + "");
//        Log.d("String3", splitString[2] + "");
//        Log.d("String4", splitString[3] + "");
//
//        if (splitString.length > 4) {
//            Log.d("String5", splitString[4] + "");
//        }
    }
}