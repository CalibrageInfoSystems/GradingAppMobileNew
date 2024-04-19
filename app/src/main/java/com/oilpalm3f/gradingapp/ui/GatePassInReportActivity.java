package com.oilpalm3f.gradingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.ApplicationThread;
import com.oilpalm3f.gradingapp.cloudhelper.Log;
import com.oilpalm3f.gradingapp.common.CommonConstants;
import com.oilpalm3f.gradingapp.common.CommonUtils;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.database.Queries;
import com.oilpalm3f.gradingapp.dbmodels.GatepassInListModel;
import com.oilpalm3f.gradingapp.dbmodels.GatepassTokenListModel;
import com.oilpalm3f.gradingapp.printer.BluetoothDevicesFragment;
import com.oilpalm3f.gradingapp.printer.PrinterChooserFragment;
import com.oilpalm3f.gradingapp.printer.UsbDevicesListFragment;
import com.oilpalm3f.gradingapp.printer.onPrinterType;
import com.oilpalm3f.gradingapp.uihelper.ProgressBar;
import com.oilpalm3f.gradingapp.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GatePassInReportActivity extends AppCompatActivity implements ongatepassinprintselected, onPrinterType, UsbDevicesListFragment.onUsbDeviceSelected, BluetoothDevicesFragment.onDeviceSelected{

    private DataAccessHandler dataAccessHandler;
    private RecyclerView gatepassinList;
    private Button searchBtn;

    private LinearLayoutManager layoutManager;

    private EditText fromDateEdt, toDateEdt;
    private Calendar myCalendar = Calendar.getInstance();
    private String searchQuery = "";

    private String fromDateStr = "";
    private String toDateStr = "";

    private GatepassInListModel selectedReport;

    private TextView tvNorecords;

    private List<GatepassInListModel> mReportsList = new ArrayList<>();

    private GatepassinlistAdapter gatepassinlistAdapter;

    private static final String LOG_TAG = GatePassInReportActivity.class.getName();

    private BluetoothDevicesFragment bluetoothDevicesFragment = null;
    private UsbDevicesListFragment usbDevicesListFragment = null;

    private EditText searchtext;
    private ImageView clearsearch;
    private android.widget.ProgressBar searchprogress;
    private boolean isSearch = false;

    String searchKey = "";



    public static final int LIMIT = 30;
    private int offset;
    private boolean hasMoreItems = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_pass_in_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gate Pass-In Reports");
        setSupportActionBar(toolbar);

        dataAccessHandler = new DataAccessHandler(this);
        initUI();
        setViews();

        String currentDate = CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        fromDateEdt.setText(sdf.format(new Date()));
        toDateEdt.setText(sdf.format(new Date()));
//
//        searchQuery = Queries.getInstance().getGatepassInReports(currentDate, currentDate);
//        updateLabel(0);
//        updateLabel(1);
//        getGatepassinReports(searchQuery);
        CommonUtils.currentActivity = this;
        gatepassinlistAdapter = new GatepassinlistAdapter(GatePassInReportActivity.this, mReportsList);
//        gatepassinList.setLayoutManager(new LinearLayoutManager(GatePassInReportActivity.this, LinearLayoutManager.VERTICAL, false));
//        gatepassinList.setAdapter(gatepasstokenlistAdapter);
    }

    private void setViews() {

        offset = offset + LIMIT;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar.showProgressBar(GatePassInReportActivity.this, "Please wait...");
                mReportsList = (List<GatepassInListModel>) dataAccessHandler.getGatpassinList(Queries.getInstance().getGatepassInReports(LIMIT, offset,fromDateStr,toDateStr, searchKey), 1);
                Collections.reverse(mReportsList);
                ApplicationThread.bgndPost(LOG_TAG, "getting reports data", new Runnable() {
                    @Override
                    public void run() {

                        ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                            @Override
                            public void run() {
                                ProgressBar.hideProgressBar();
                                gatepassinlistAdapter = new GatepassinlistAdapter(GatePassInReportActivity.this, mReportsList);
                                if (mReportsList != null && !mReportsList.isEmpty() && mReportsList.size()!= 0) {
                                    gatepassinList.setVisibility(View.VISIBLE);
                                    tvNorecords.setVisibility(View.GONE);
                                    layoutManager = new LinearLayoutManager(GatePassInReportActivity.this, LinearLayoutManager.VERTICAL, false);
                                    gatepassinList.setLayoutManager(layoutManager);

                                    gatepassinList.setAdapter(gatepassinlistAdapter);
                                    gatepassinlistAdapter.setonGatepassinPrintSelected((GatePassInReportActivity.this));

                                    //   setTitle(alert_type, offset == 0 ? alertsVisitsInfoList.size() : offset);
                                }
                                else{
                                    gatepassinList.setVisibility(View.GONE);
                                    tvNorecords.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(0);
            }
        };



        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(1);
            }
        };

        String dateFormatter = "yyyy-MM-dd";
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);
        fromDateStr = sdf2.format(myCalendar.getTime());
        toDateStr = sdf2.format(myCalendar.getTime());

        toDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(GatePassInReportActivity.this, toDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

        fromDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(GatePassInReportActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));  //date is dateSetListener as per your code in question
                datePickerDialog.show();
            }
        });

        clearsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = false;
                mReportsList.clear();
                searchtext.setText("");
            }
        });

        searchtext.addTextChangedListener(mTextWatcher);

        gatepassinlist(offset);
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("WhatisinSearch", "is :"+ s);
            //
            offset = 0;
            ApplicationThread.uiPost(LOG_TAG, "search", new Runnable() {
                @Override
                public void run() {
                    doSearch(s.toString().trim());
                    if (s.toString().length() > 0) {
                        clearsearch.setVisibility(View.VISIBLE);
                    } else {
                        clearsearch.setVisibility(View.GONE);
                    }
                }
            }, 100);
        }

        @Override
        public void afterTextChanged(final Editable s) {

        }
    };

    public void doSearch(String searchQuery) {
        Log.d("DoSearchQuery", "is :" +  searchQuery);
        offset = 0;
        hasMoreItems = true;
        if (searchQuery !=null &  !TextUtils.isEmpty(searchQuery)  & searchQuery.length()  > 0) {

            offset = 0;
            isSearch = true;
            searchKey = searchQuery.trim();
            gatepassinlist(offset);
        } else {
            searchKey = "";
            isSearch = false;
            gatepassinlist(offset);
        }
    }

//    private void getGatepassinReports(String searchQuery) {
//
//        ProgressBar.showProgressBar(this, "Please wait...");
//        ApplicationThread.bgndPost(LOG_TAG, "getting reports data", new Runnable() {
//            @Override
//            public void run() {
//                dataAccessHandler.getGatepassInreportDetails(searchQuery, new ApplicationThread.OnComplete<List<GatepassInListModel>>() {
//                    @Override
//                    public void execute(boolean success, final List<GatepassInListModel> reports, String msg) {
//                        ProgressBar.hideProgressBar();
//                        if (success) {
//                            if (reports != null && reports.size() > 0) {
//                                mReportsList.clear();
//                                mReportsList = reports;
//                                ApplicationThread.uiPost(LOG_TAG, "update ui", new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int recordsSize = reports.size();
//                                        Log.v(LOG_TAG, "data size " + recordsSize);
//                                        if (recordsSize > 0) {
//                                            gatepasstokenlistAdapter.updateAdapter(reports);
//                                            tvNorecords.setVisibility(View.GONE);
//                                            gatepassinList.setVisibility(View.VISIBLE);
//                                            //  setTile(getString(R.string.collection_report) + " ("+recordsSize+")");
//                                        } else {
//                                            tvNorecords.setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                });
//                            } else {
//                                ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tvNorecords.setVisibility(View.VISIBLE);
//                                        Log.v(LOG_TAG, "@@@ No records found");
//                                        gatepassinList.setVisibility(View.GONE);
//                                    }
//                                });
//                            }
//                        } else {
//                            ApplicationThread.uiPost(LOG_TAG, "updating ui", new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvNorecords.setVisibility(View.VISIBLE);
//                                    Log.v(LOG_TAG, "@@@ No records found");
//                                    gatepassinList.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    }
//                });
//            }
//        });
//    }


    private void initUI() {
        gatepassinList = (RecyclerView) findViewById(R.id.gatepass_in_list);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        tvNorecords = (TextView) findViewById(R.id.no_records);
        tvNorecords.setVisibility(View.GONE);

        fromDateEdt = (EditText) findViewById(R.id.fromDate);
        toDateEdt = (EditText) findViewById(R.id.toDate);

        searchtext = (EditText) findViewById(R.id.searchtext);
        clearsearch = (ImageView) findViewById(R.id.clearsearch);
        searchprogress = (android.widget.ProgressBar) findViewById(R.id.searchprogress);

    }

    //Update the from and to date labels
    private void updateLabel(int type) {
        String myFormat = "dd-MM-yyyy";
        String dateFormatter = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormatter, Locale.US);

        if (type == 0) {
            fromDateStr = sdf2.format(myCalendar.getTime());
            fromDateEdt.setText(sdf.format(myCalendar.getTime()));
        } else {
            toDateStr = sdf2.format(myCalendar.getTime());
            toDateEdt.setText(sdf.format(myCalendar.getTime()));
        }

    }



    public static boolean isDateAfter(String startDate,String endDate)
    {
        try
        {
            String myFormatString = "yyyy-MM-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else if (date1.equals(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e)
        {

            return false;
        }
    }

    @Override
    public void enablingPrintButton(boolean rePrint) {

    }

    @Override
    public void selectedDevice(PrinterInstance printerInstance) {
        for (int i = 0; i < 1; i++) {
            printttGatepasstokenData(printerInstance, i);
        }
    }

    private void printttGatepasstokenData(PrinterInstance mPrinter, int i) {

        String fruitType;

        Log.d("Collection", selectedReport.getFruitType() + "");

        if ("0".equals(selectedReport.getFruitType())) {
            fruitType = "Collection";
        } else {
            fruitType = "Consignment";
        }

        String printfruitType;

        Log.d("Collection", selectedReport.getFruitType() + "");

        if ("0".equals(selectedReport.getFruitType())) {
            printfruitType = "true";
        } else {
            printfruitType = "false";
        }

        //int token = Integer.parseInt(selectedReport.getGatePassCode());

        long token = 0;

        try {
             token = Long.parseLong(selectedReport.getGatePassCode());
            // Continue with your code using the 'token' variable of type long
        } catch (NumberFormatException e) {
            // Handle the exception (e.g., print an error message or log it)
            e.printStackTrace();
        }

        android.util.Log.d("token", token + "");
        String formattedToken = String.valueOf(token);
        String tokenCount = "";
        tokenCount = formattedToken;
        android.util.Log.d("tokenCount", tokenCount + "");

        mPrinter.init();
        StringBuilder sb = new StringBuilder();
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        mPrinter.printText(" 3F OILPALM PVT LTD " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.printText(" Duplicate Copy of Gate Pass-In " + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        mPrinter.setCharacterMultiple(0, 0);
        mPrinter.setLeftMargin(15, 15);
        sb.append("==============================================" + "\n");

        sb.append(" ");
        sb.append(" Vehicle Number : ").append(selectedReport.getVehicleNumber() + "").append("\n");
        sb.append(" ");

        sb.append(" Fruit Type : ").append(fruitType + "").append("\n");
        sb.append(" ");

        sb.append(" Vehicle Category : ").append(selectedReport.getVehicleCategory() + "").append("\n");
        sb.append(" ");

        sb.append(" Vehicle Type Name : ").append(selectedReport.getVehicleType() + "").append("\n");
        sb.append(" ");
        sb.append(" Date : ").append(selectedReport.getCreatedDate() + "").append("\n");

        sb.append("  Created By : ").append(selectedReport.getCreatedBy() + "").append("\n");

        mPrinter.printText(sb.toString());



//         String hashString = qrvalue+"/"+CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS)+"/"+unripen.getText().toString()+"/"+underripe.getText().toString()+"/"+ripen.getText().toString()
//                 +"/"+overripe.getText().toString()+"/"+diseased.getText().toString()+"/"+emptybunches.getText().toString()+"/"
//                 +longstalk.getText().toString()+"/"+mediumstalk.getText().toString()+"/"+shortstalk.getText().toString()+"/"+
//                 optimum.getText().toString()+"/"+fruitavailable+"/"+fruightweight+"/"+rejectedbunches+
//                 "/"+gradingdoneby.getText().toString();
//         String qrCodeValue = hashString;

        String datetime = selectedReport.getGatePassCode().substring(0, 14);
        String serialNumber = selectedReport.getGatePassCode().substring(14, 18);

        Log.d("datetime", datetime + "");
        Log.d("serialNumber", serialNumber + "");


        String qrCodeValue = selectedReport.getGatePassCode() +"/" +selectedReport.getSerialNumber()+"/" +printfruitType+"/" + selectedReport.getVehicleNumber()
                + "/" + selectedReport.getVehicleCategoryId()+"/" +selectedReport.getVehicleTypeId()+"/" + selectedReport.getWBID();

        // String qrCodeValue =  currentDateTime +"/"+ GatePassSerialNumber +"/" +selectedfruittype+"/" + vehiclenumber.getText().toString();
        android.util.Log.d("qrCodeValueis", qrCodeValue  + "");
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 95, 3, qrCodeValue);

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);

        String space = "-----------------------------------------------";
        String tokenNumber  =  "WeighBridge";
        String spaceBuilderr = "\n";

//        mPrinter.printText(space);
//        mPrinter.printText(spaceBuilderr);
//        mPrinter.printText(tokenNumber);
//        mPrinter.printText(spaceBuilderr);
//        mPrinter.printText(selectedReport.getWBCode());
//        mPrinter.printText(spaceBuilderr);
//        mPrinter.printText(space);
//        mPrinter.printText(spaceBuilderr);

        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(tokenNumber + " - "+ selectedReport.getWBCode());
        mPrinter.printText(spaceBuilderr);
        mPrinter.printText(space);
        mPrinter.printText(spaceBuilderr);

//
//        if(CommonConstants.PrinterName.contains("AMIGOS")){
//            android.util.Log.d(LOG_TAG,"########### NEW ##############");
//            print_qr_code(mPrinter,qrCodeValue);
//        }else{
//            android.util.Log.d(LOG_TAG,"########### OLD ##############");
//            mPrinter.printBarCode(barcode);
//        }

        if(CommonConstants.PrinterName.contains("G-8BT3 AMIGOS")){
            android.util.Log.d(LOG_TAG,"########### NEW ##############");
            //mPrinter.setPrintModel(false,true,true,false);
            print_qr_codee(mPrinter,qrCodeValue);
        }else if (CommonConstants.PrinterName.contains("AMIGOS")){
            print_qr_code(mPrinter,qrCodeValue);
        }else{
            android.util.Log.d(LOG_TAG,"########### OLD ##############");
            mPrinter.printBarCode(barcode);
        }

        mPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
        mPrinter.setCharacterMultiple(0, 1);
        //mPrinter.printText(qrCodeValue);

        String spaceBuilder = "\n" +
                " " ;
        mPrinter.printText(spaceBuilder);

        boolean printSuccess = false;
        try {
            mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
            printSuccess = true;
        } catch (Exception e) {
            android.util.Log.v(LOG_TAG, "@@@ printing failed " + e.getMessage());
            UiUtils.showCustomToastMessage("Printing failes due to " + e.getMessage(), GatePassInReportActivity.this, 1);
            printSuccess = false;
        } finally {
            if (printSuccess) {
                Toast.makeText(GatePassInReportActivity.this, "Print Success", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public void onPrinterTypeSelected(int printerType) {

        if (printerType == PrinterChooserFragment.USB_PRINTER) {
            FragmentManager fm = getSupportFragmentManager();
            usbDevicesListFragment = new UsbDevicesListFragment();
            usbDevicesListFragment.setOnUsbDeviceSelected(this);
            usbDevicesListFragment.show(fm, "usb fragment");
        } else {
            FragmentManager fm = getSupportFragmentManager();
            bluetoothDevicesFragment = new BluetoothDevicesFragment();
            bluetoothDevicesFragment.setOnDeviceSelected(this);
            bluetoothDevicesFragment.show(fm, "bluetooth fragment");
        }
    }

    public void print_qr_codee(PrinterInstance mPrinter,String qrdata)
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


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x1};


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

//    @Override
//    public void printOptionSelected(int position) {
//Log.d("Method", "into this");
//        selectedReport = mReportsList.get(position);
//        FragmentManager fm = getSupportFragmentManager();
//        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
//        printerChooserFragment.setPrinterType(this);
//        printerChooserFragment.show(fm, "bluetooth fragment");
//
//        Log.d("SVehicleNumber", selectedReport.getVehicleNumber());
//        Log.d("SSerialNumber", selectedReport.getGatePassSerialNumber());
//        Log.d("SCollection", selectedReport.getIsCollection());
//        Log.d("SCreateddate", selectedReport.getCreatedDate());
//
//    }

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


        byte[] sizeQR = {(byte)0x1d, (byte)0x28, (byte)0x6b, (byte)0x03, (byte)0x00, (byte)0x31, (byte)0x43, (byte)0x05};


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

//    @Override
//    public void gatepasstokenprintOptionSelected(int position) {
//        Log.d("Method", "into this");
//        selectedReport = mReportsList.get(position);
//        FragmentManager fm = getSupportFragmentManager();
//        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
//        printerChooserFragment.setPrinterType(this);
//        printerChooserFragment.show(fm, "bluetooth fragment");
//
////        Log.d("SVehicleNumber", selectedReport.getVehicleNumber());
////        Log.d("SSerialNumber", selectedReport.getGatePassSerialNumber());
////        Log.d("SCollection", selectedReport.getIsCollection());
////        Log.d("SCreateddate", selectedReport.getCreatedDate());
//    }

    @Override
    public void gatepassinprinOptionSelected(int position) {

        selectedReport = mReportsList.get(position);
        FragmentManager fm = getSupportFragmentManager();
        PrinterChooserFragment printerChooserFragment = new PrinterChooserFragment();
        printerChooserFragment.setPrinterType(this);
        printerChooserFragment.show(fm, "bluetooth fragment");

    }

    private void gatepassinlist(final int offset) {
        //ProgressBar.showProgressBar(this, "Please wait...");

        if (searchprogress != null) {
            searchprogress.setVisibility(View.VISIBLE);
        }
        ApplicationThread.bgndPost(LOG_TAG, "notvisitedplots", new Runnable() {
            @Override
            public void run() {

                mReportsList = (List<GatepassInListModel>) dataAccessHandler.getGatpassinList(Queries.getInstance().getGatepassInReports(LIMIT, offset,fromDateStr,toDateStr, searchKey), 1);
                Collections.reverse(mReportsList);

                ApplicationThread.uiPost(LOG_TAG, "", new Runnable() {
                    @Override
                    public void run() {
                        //ProgressBar.hideProgressBar();
                        searchprogress.setVisibility(View.GONE);
                        gatepassinlistAdapter = new GatepassinlistAdapter(GatePassInReportActivity.this, mReportsList);
                        if (mReportsList != null && !mReportsList.isEmpty()) {
                            gatepassinList.setVisibility(View.VISIBLE);
                            tvNorecords.setVisibility(View.GONE);
                            layoutManager = new LinearLayoutManager(GatePassInReportActivity.this, LinearLayoutManager.VERTICAL, false);
                            gatepassinList.setLayoutManager(layoutManager);

                            gatepassinList.setAdapter(gatepassinlistAdapter);
                            gatepassinlistAdapter.setonGatepassinPrintSelected((GatePassInReportActivity.this));

                            //   setTitle(alert_type, offset == 0 ? alertsVisitsInfoList.size() : offset);
                        }
                        else{
                            gatepassinList.setVisibility(View.GONE);
                            tvNorecords.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
    }
}