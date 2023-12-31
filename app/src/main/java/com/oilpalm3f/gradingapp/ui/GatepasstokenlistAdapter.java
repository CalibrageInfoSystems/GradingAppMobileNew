package com.oilpalm3f.gradingapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.oilpalm3f.gradingapp.R;
import com.oilpalm3f.gradingapp.cloudhelper.Log;
import com.oilpalm3f.gradingapp.database.DataAccessHandler;
import com.oilpalm3f.gradingapp.dbmodels.GatepassTokenListModel;
import com.oilpalm3f.gradingapp.dbmodels.GradingReportModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatepasstokenlistAdapter extends RecyclerView.Adapter<GatepasstokenlistAdapter.GatepasstokenReportViewHolder> {

    private static final String LOG_TAG = GatePassTokenReportActivity.class.getName();
    private List<GatepassTokenListModel> mList;
    private Context context;
    private GatepassTokenListModel item;
    private DataAccessHandler dataAccessHandler = null;
    private ongatepasstokenprintselected onPrintSelected;

    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
    int row_index = -1;
    LayoutInflater mInflater;

    public GatepasstokenlistAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        dataAccessHandler = new DataAccessHandler(context);
    }

    @NonNull
    @Override
    public GatepasstokenReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gatepasstoken_item, null);
        GatepasstokenReportViewHolder myHolder = new GatepasstokenReportViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GatepasstokenReportViewHolder holder, int position) {

        item = mList.get(position);
        Log.d("Size", mList.size() + "");

        Log.d("getIsCollection", mList.get(position).getIsCollection() + "");

        holder.tvgatepasstoken.setText(mList.get(position).getGatePassTokenCode() + "");
        holder.tvvehicleNumber.setText(mList.get(position).getVehicleNumber() + "");
        holder.tvgatepassserialnumber.setText(mList.get(position).getGatePassSerialNumber() + "");
        holder.tviscollection.setText(mList.get(position).getIsCollection() + "");
        holder.tvcreateddate.setText(mList.get(position).getCreatedDate() + "");

        String datetime = mList.get(position).getGatePassTokenCode().substring(0, 14);
        String serialNumber = mList.get(position).getGatePassTokenCode().substring(14, 18);

        Log.d("datetime", datetime + "");
        Log.d("serialNumber", serialNumber + "");


        String qrCodeValue = datetime +"/" +serialNumber+"/" +mList.get(position).getIsCollection()+"/" + mList.get(position).getVehicleNumber();

        holder.tvgatepasstoken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(context,qrCodeValue);

            }
        });

        holder.printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Method1", "into this");

                if (null != onPrintSelected) {

                    Log.d("Method2", "into this");

                    onPrintSelected.gatepasstokenprintOptionSelected(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateAdapter(List<GatepassTokenListModel> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void setonPrintSelected(final ongatepasstokenprintselected onPrintSelected) {
        this.onPrintSelected = onPrintSelected;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class GatepasstokenReportViewHolder extends RecyclerView.ViewHolder {

        private TextView tvgatepasstoken, tvvehicleNumber, tvgatepassserialnumber, tviscollection, tvcreateddate;
        private ImageView printBtn;

        public GatepasstokenReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvgatepasstoken = (TextView) itemView.findViewById(R.id.tvgatepasstokencode);
            tvvehicleNumber = (TextView) itemView.findViewById(R.id.tv_vehicleNumber);
            tvgatepassserialnumber = (TextView) itemView.findViewById(R.id.tv_serialNumber);
            tviscollection = (TextView) itemView.findViewById(R.id.tv_collectiontype);
            tvcreateddate = (TextView) itemView.findViewById(R.id.tv_tokendate);
            printBtn = (ImageView) itemView.findViewById(R.id.printBtn);
        }
    }

    public void showDialog(Context context, String imgString) {
        final Dialog dialog = new Dialog(context, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialogg);
        ImageView img = dialog.findViewById(R.id.imageView);
        TextView cancel =dialog.findViewById(R.id.cancel);

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(imgString, BarcodeFormat.QR_CODE, 256, 256);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            img.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
