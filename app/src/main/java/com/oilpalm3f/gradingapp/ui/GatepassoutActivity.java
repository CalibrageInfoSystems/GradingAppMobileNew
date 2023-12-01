package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oilpalm3f.gradingapp.MainActivity;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.dbmodels.Gatepassoutdetails;
import com.oilpalm3f.gradingapp.dbmodels.UserDetails;
import com.oilpalm3f.gradingapp.printer.PrinterChooserFragment;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class GatepassoutActivity extends AppCompatActivity {
    private static final String LOG_TAG = GatepassinActivity.class.getName();
    String qrvalue;
    String[] splitString;
    private DataAccessHandler dataAccessHandler;
    Button   submit;
    TextView InTime,vehiclenumber,tokennumber;
    int tokenexists;
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
        InTime = findViewById(R.id.intime);
        vehiclenumber = findViewById(R.id.vehiclenumber);
        tokennumber = findViewById(R.id.tokennumber);

    }
    private void Setviews() {
        String query = Queries.getInstance().gatepassoutdetails(qrvalue);

        final Gatepassoutdetails gatepassDetails = (Gatepassoutdetails) dataAccessHandler.getgatepassDetails(query, 0);
     //   Log.e("=>gatepassDetails",gatepassDetails.getVehicleNumber() + "=="+ gatepassDetails.getCreatedDate());
        tokennumber.setText(gatepassDetails.getGatePassSerialNumber()+"");
        vehiclenumber.setText(gatepassDetails.getVehicleNumber()+"");
        InTime.setText(gatepassDetails.getCreatedDate()+"");


        tokenexists = dataAccessHandler.getOnlyOneIntValueFromDb(Queries.getInstance().getgateoutTokenExistQuery(qrvalue));
    //  Log.d("tokenexists",tokenexists + "");

        if (tokenexists == 1){

            showDialog(GatepassoutActivity.this, "The gate pass-out process for this token has already been completed");

        }
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


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String whereCondition = " where GatePassCode = '"+ qrvalue +"'" ;
                List<LinkedHashMap> details = new ArrayList<>();
                LinkedHashMap map = new LinkedHashMap();
                map.put("UpdatedByUserId",Integer.parseInt(CommonConstants.USER_ID));
                map.put("UpdatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
                map.put("IsVehicleOut",true);
                map.put("ServerUpdatedStatus",0);
                details.add(map);
                dataAccessHandler.updateData("GatePass", details, true, whereCondition, new ApplicationThread.OnComplete<String>() {
                    @Override
                    public void execute(boolean success, String result, String msg) {
                        if (success) {
                            if (CommonUtils.isNetworkAvailable(GatepassoutActivity.this)) {
                                DataSyncHelper.performRefreshTransactionsSync(GatepassoutActivity.this, new ApplicationThread.OnComplete() {
                                    @Override
                                    public void execute(boolean success, Object result, String msg) {
                                        if (success) {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                                @Override
                                                public void run() {
                                                    CommonConstants.IsLogin = false;
                                                    UiUtils.showCustomToastMessage("Successfully data sent to server", GatepassoutActivity.this, 0);
                                                    startActivity(new Intent(GatepassoutActivity.this, MainActivity.class));
                                                }
                                            });
                                        } else {
                                            ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                                @Override
                                                public void run() {
                                                    UiUtils.showCustomToastMessage("Data sync failed", GatepassoutActivity.this, 1);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                startActivity(new Intent(GatepassoutActivity.this, MainActivity.class));
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });




            }
        });
    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        final ImageView img = dialog.findViewById(R.id.img_cross);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Animatable) img.getDrawable()).start();
            }
        }, 500);
    }
}