 package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.gradingapp.MainActivity;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.datasync.helpers.DataSyncHelper;
import com.oilpalm3f.gradingapp.printer.BluetoothDevicesFragment;
import com.oilpalm3f.gradingapp.printer.PrinterChooserFragment;
import com.oilpalm3f.gradingapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.gradingapp.printer.onPrinterType;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

 public class GatepasstokenActivity extends AppCompatActivity implements BluetoothDevicesFragment.onDeviceSelected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected  {
     private static final String LOG_TAG = GatepasstokenActivity.class.getName();
     Spinner fruittype;
     EditText vehiclenumber;
     boolean selectedfruittype;
     Button submit;
     String GatePassSerialNumber;
     String fruitType;
     private DataAccessHandler dataAccessHandler;
     String qrCodeValue;
     String currentDateTime;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_gatepasstoken);
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         toolbar.setTitle("GatePass Token");
         setSupportActionBar(toolbar);
         intviews();
         Setviews();

     }

     private void intviews() {
         dataAccessHandler = new DataAccessHandler(GatepasstokenActivity.this);
         fruittype = findViewById(R.id.fruit_spinner);
         vehiclenumber = findViewById(R.id.vehiclenumber);
         submit = findViewById(R.id.gatepasstokensubmit);
     }
     private void Setviews() {

         //Binding data to isloosefruitavailable spinner and onclick listener
         String[] fruittypeArray = getResources().getStringArray(R.array.fruittype);
         List<String> fruittypeList = Arrays.asList(fruittypeArray);
         ArrayAdapter<String> isloosefruitavailableAdapter = new ArrayAdapter<>(GatepasstokenActivity.this, android.R.layout.simple_spinner_item, fruittypeList);
         isloosefruitavailableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         fruittype.setAdapter(isloosefruitavailableAdapter);

         fruittype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                 if (fruittype.getSelectedItemPosition() == 1) {
                     selectedfruittype = true;
                     fruitType= "Collection";

                 } else {
                     selectedfruittype = false;
                     fruitType= "Consignment";
                 }


             }

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             }
         });


         submit.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if (validation()){
//                    enablePrintBtn(false);
//                    submit.setAlpha(0.5f);

//                     Calendar calendar = Calendar.getInstance();
//                     Date currentDate = calendar.getTime();
//
//                     calendar.set(Calendar.HOUR_OF_DAY, 6);
//                     calendar.set(Calendar.MINUTE, 0);
//                     calendar.set(Calendar.SECOND, 0);
//                     calendar.set(Calendar.MILLISECOND, 0);
//
//
//                     // Get today's date at 6:00:00
//                     Date todayAt6AM = calendar.getTime();
//
//                     // Move back one day
//                     calendar.add(Calendar.DAY_OF_MONTH, +1);
//
//                     // Get yesterday's date at 6:00:00
//                     Date tommorowAt6AM = calendar.getTime();
//
//                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



                     String currentDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY);
//                     Log.d("currentDate", currentDate + "");
//                     Log.d("todayAt6AM", dateFormat.format(todayAt6AM));

                     String maxnumber = dataAccessHandler.getOnlyOneValueFromDb(Queries.getInstance().getGatePassSerialNumber(currentDate));
                     Log.d("maxnumber", maxnumber + "");
                     String incrementedMaxNumber = "";
                     if(maxnumber!=null) {
                         try {
                             // Convert maxnumber to integer and increment by 1
                             int incrementedNumber = Integer.parseInt(maxnumber) + 1;

                             // Convert back to string
                              incrementedMaxNumber = String.valueOf(incrementedNumber);
                             GatePassSerialNumber = dataAccessHandler.getserialnumber(incrementedMaxNumber);
                             Log.d("maxnumber", incrementedMaxNumber);
                             Log.d("GatePassSerialNumber", GatePassSerialNumber + "");
                         } catch (NumberFormatException e) {
                             // Handle the case where maxnumber is not a valid integer
                             Log.e("maxnumber", "Error parsing maxnumber as an integer", e);
                         }
                     }
                     else{
                         GatePassSerialNumber = dataAccessHandler.getserialnumber(incrementedMaxNumber);
                         Log.d("GatePassSerialNumber134", GatePassSerialNumber + "");

                     }
//        select GatePassSerialNumber  as Maxnumber FROM  GatePassToken Where CreatedDate like '%2023-11-21%' ORDER BY ID DESC LIMIT 1



                     // Get current date and time stamp
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                      currentDateTime = sdf.format(new Date());
                     Log.d("currentDateTime", currentDateTime + "");
                     qrCodeValue =  currentDateTime +"/"+ GatePassSerialNumber +"/" +selectedfruittype+"/" + vehiclenumber.getText().toString();
                     Log.d("qrCodeValue", qrCodeValue + "");
                    FragmentManager fm = getSupportFragmentManager();
                    PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
                    printerChooserFragment.setPrinterType(GatepasstokenActivity.this);
                    printerChooserFragment.show(fm, "bluetooth fragment");
                // savegatepasstoken();
                 }
             }
         });

     }

     private void savegatepasstoken() {

         List<LinkedHashMap> details = new ArrayList<>();
         LinkedHashMap map = new LinkedHashMap();

         map.put("GatePassTokenCode", currentDateTime + GatePassSerialNumber );
         map.put("VehicleNumber", vehiclenumber.getText().toString());
         map.put("GatePassSerialNumber", GatePassSerialNumber);


//        int isfruitavailable = 0;
//
//        if (isloosefruitavailable_spinner.getSelectedItemPosition() == 1){
//
//            isfruitavailable = 1;
//        }else if (isloosefruitavailable_spinner.getSelectedItemPosition() == 2){
//            isfruitavailable = 0;
//        }
         if (fruittype.getSelectedItemPosition() == 1) {
             selectedfruittype = true;
             fruitType= "Collection";

         } else {
             selectedfruittype = false;
             fruitType= "Consignment";
         }


         map.put("IsCollection", selectedfruittype);
         map.put("CreatedByUserId", CommonConstants.USER_ID);
         map.put("CreatedDate", CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
         map.put("ServerUpdatedStatus", false);


         details.add(map);

         dataAccessHandler.saveData("GatePassToken", details, new ApplicationThread.OnComplete<String>() {
             @Override
             public void execute(boolean success, String result, String msg) {

                 if (success) {
                     Log.d(GradingActivity.class.getSimpleName(), "==>  Analysis ==> TABLE_GatePassToken INSERT COMPLETED");
                     if (CommonUtils.isNetworkAvailable(GatepasstokenActivity.this)) {
                         DataSyncHelper.performRefreshTransactionsSync(GatepasstokenActivity.this, new ApplicationThread.OnComplete() {
                             @Override
                             public void execute(boolean success, Object result, String msg) {
                                 if (success) {
                                     ApplicationThread.uiPost(LOG_TAG, "transactions sync message", new Runnable() {
                                         @Override
                                         public void run() {
                                             CommonConstants.IsLogin = false;
                                             UiUtils.showCustomToastMessage("Successfully data sent to server", GatepasstokenActivity.this, 0);
                                             startActivity(new Intent(GatepasstokenActivity.this, MainActivity.class));
                                         }
                                     });
                                 } else {
                                     ApplicationThread.uiPost(LOG_TAG, "transactions sync failed message", new Runnable() {
                                         @Override
                                         public void run() {
                                             UiUtils.showCustomToastMessage("Data sync failed", GatepasstokenActivity.this, 1);
                                         }
                                     });
                                 }
                             }
                         });
                     } else {
                         startActivity(new Intent(GatepasstokenActivity.this, MainActivity.class));
                     }


                 } else {
                     Toast.makeText(GatepasstokenActivity.this, "Submit Failed", Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }




     private boolean validation() {
         if (TextUtils.isEmpty(vehiclenumber.getText().toString())) {
             UiUtils.showCustomToastMessage("Please Enter Vehicle Number", GatepasstokenActivity.this, 0);
             return false;
         }

         if (fruittype.getSelectedItemPosition() == 0) {
             UiUtils.showCustomToastMessage("Please Select  Fruit Type", GatepasstokenActivity.this, 0);
             return false;
         }
         return true;
     }

     @Override
     public void selectedDevice(PrinterInstance printerInstance) {
         Log.v(LOG_TAG, "selected address is ");
         if (null != printerInstance) {
             enablePrintBtn(false);
             for (int i = 0; i < 1; i++) {
                 printGAtepasstoken(printerInstance, false, i);
             }
         } else {
             UiUtils.showCustomToastMessage("Printing failed", GatepasstokenActivity.this, 1);
         }
     }


     public void printGAtepasstoken(PrinterInstance mPrinter, boolean isReprint, int printCount) {

         int token = Integer.parseInt(GatePassSerialNumber);
         Log.d("token", token + "");
         String formattedToken = String.valueOf(token);
         String tokenCount = "";
         tokenCount = formattedToken;
         Log.d("tokenCount", tokenCount + "");

         mPrinter.init();
         StringBuilder sb = new StringBuilder();
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);
         mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);
         mPrinter.printText(" Gate Serial Number " + "\n");
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
         mPrinter.setCharacterMultiple(0, 0);
         mPrinter.setLeftMargin(15, 15);
         sb.append("==============================================" + "\n");

         sb.append(" ");
         sb.append(" Vehicle Number : ").append(vehiclenumber.getText().toString() + "").append("\n");
         sb.append(" ");
//         sb.append(" CCCode : ").append(splitString[1] + "").append("\n");
//         sb.append(" ");
         sb.append(" Fruit Type : ").append(fruitType + "").append("\n");
         sb.append(" ");

         sb.append(" Date : ").append(CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_4) + "").append("\n");

         mPrinter.printText(sb.toString());



//         String hashString = qrvalue+"/"+CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS)+"/"+unripen.getText().toString()+"/"+underripe.getText().toString()+"/"+ripen.getText().toString()
//                 +"/"+overripe.getText().toString()+"/"+diseased.getText().toString()+"/"+emptybunches.getText().toString()+"/"
//                 +longstalk.getText().toString()+"/"+mediumstalk.getText().toString()+"/"+shortstalk.getText().toString()+"/"+
//                 optimum.getText().toString()+"/"+fruitavailable+"/"+fruightweight+"/"+rejectedbunches+
//                 "/"+gradingdoneby.getText().toString();
//         String qrCodeValue = hashString;
         Log.d("qrCodeValueis", qrCodeValue  + "");
         Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);

         String space = "-----------------------------------------------";
         String tokenNumber  =  "Token Number";
       String spaceBuilderr = "\n";

         mPrinter.printText(space);
         mPrinter.printText(spaceBuilderr);
         mPrinter.printText(tokenNumber);
         mPrinter.printText(spaceBuilderr);
         mPrinter.printText(tokenCount);
         mPrinter.printText(spaceBuilderr);
         mPrinter.printText(space);
         mPrinter.printText(spaceBuilderr);


         if(CommonConstants.PrinterName.contains("AMIGOS")){
             Log.d(LOG_TAG,"########### NEW ##############");
             print_qr_code(mPrinter,qrCodeValue);
         }else{
             Log.d(LOG_TAG,"########### OLD ##############");
             mPrinter.printBarCode(barcode);
         }
         mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
         mPrinter.setCharacterMultiple(0, 1);
         //mPrinter.printText(qrCodeValue);

         String spaceBuilder = "\n" +
                 " " +
                 "\n" +
                 " " +
                 "\n" +
                 "\n" +
                 " " +
                 "\n" +
                 "\n";
         mPrinter.printText(spaceBuilder);

         boolean printSuccess = false;
         try {
             mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
             printSuccess = true;
         } catch (Exception e) {
             Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
             UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), GatepasstokenActivity.this, 1);
             printSuccess = false;
         } finally {
             if (printSuccess) {
                 Toast.makeText(GatepasstokenActivity.this, "Print Success", Toast.LENGTH_SHORT).show();
                 savegatepasstoken();
             }
         }

     }

     //Generate QRCode
     public void print_qr_code(PrinterInstance mPrinter,String qrdata)
     {
         int store_len = qrdata.length() + 3;
         byte store_pL = (byte) (store_len % 256);
         byte store_pH = (byte) (store_len / 256);


         // QR Code: Select the modelc
         //              Hex     1D      28      6B      04      00      31      41      n1(x32)     n2(x00) - size of model
         // set n1 [49 x31, model 1] [50 x32, model 2] [51 x33, micro qr code]
         // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=140
         byte[] modelQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x04, (byte)0x00, (byte)0x31, (byte)0x41, (byte)0x32, (byte)0x00};

         // QR Code: Set the size of module
         // Hex      1D      28      6B      03      00      31      43      n
         // n depends on the printer
         // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=141


         byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x10};


         //          Hex     1D      28      6B      03      00      31      45      n
         // Set n for error correction [48 x30 -> 7%] [49 x31-> 15%] [50 x32 -> 25%] [51 x33 -> 30%]
         // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=142
         byte[] errorQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x45, (byte)0x31};


         // QR Code: Store the data in the symbol storage area
         // Hex      1D      28      6B      pL      pH      31      50      30      d1...dk
         // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=143
         //                        1D          28          6B         pL          pH  cn(49->x31) fn(80->x50) m(48->x30) d1…dk
         byte[] storeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, store_pL, store_pH, (byte)0x31, (byte)0x50, (byte)0x30};


         // QR Code: Print the symbol data in the symbol storage area
         // Hex      1D      28      6B      03      00      31      51      m
         // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=144
         byte[] printQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x51, (byte)0x30};

         // flush() runs the print job and clears out the print buffer
//        flush();

         // write() simply appends the data to the buffer
         mPrinter.sendByteData(modelQR);

         mPrinter.sendByteData(sizeQR);
         mPrinter.sendByteData(errorQR);
         mPrinter.sendByteData(storeQR);
         mPrinter.sendByteData(qrdata.getBytes());
         mPrinter.sendByteData(printQR);

     }
     public void enablePrintBtn(final boolean enable) {
         ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
             @Override
             public void run() {
                 submit.setEnabled(enable);
                 submit.setClickable(enable);
                 submit.setFocusable(enable);
             }
         });

     }

     @Override
     public void enablingPrintButton(boolean rePrint) {
         enablePrintBtn(rePrint);
     }

     //When Printer type selected
     @Override
     public void onPrinterTypeSelected(int printerType) {

         if (printerType == PrinterChooserFragment.USB_PRINTER) {
             FragmentManager fm = getSupportFragmentManager();
             UsbDevicesListFragment usbDevicesListFragment = new UsbDevicesListFragment();
             usbDevicesListFragment.setOnUsbDeviceSelected(GatepasstokenActivity.this);
             usbDevicesListFragment.show(fm, "usb fragment");
         } else {
             FragmentManager fm = getSupportFragmentManager();
             BluetoothDevicesFragment bluetoothDevicesFragment = new BluetoothDevicesFragment();
             bluetoothDevicesFragment.setOnDeviceSelected(GatepasstokenActivity.this);
             bluetoothDevicesFragment.show(fm, "bluetooth fragment");
         }

     }





 }