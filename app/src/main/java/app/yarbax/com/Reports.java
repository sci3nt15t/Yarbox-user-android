package app.yarbax.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.Inflater;

import app.yarbax.com.Utilities.CheckInternet;
import app.yarbax.com.Utilities.DateConverter;
import app.yarbax.com.Utilities.Getter;

/**
 * Created by shayanrhm on 1/2/19.
 */

public class Reports extends AppCompatActivity {

    String wallets;
    String wallets_paid;
    Executor exec = Executors.newFixedThreadPool(2);
    String token;
    LinearLayout root;
    Button wallet;
    Button paid;
    AlertDialog prog;
    Activity act;
    public void goback(){
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);
        act = this;
        android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar);
        tool.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(tool);
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked!");
                goback();
            }
        });
        toolbar_title.setText("گزارشات");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SharedPreferences mypref = getSharedPreferences("mypref",MODE_PRIVATE);
        token = mypref.getString("token","");
        root = (LinearLayout)findViewById(R.id.reports_root);
        wallet = (Button)findViewById(R.id.wallet);
        paid = (Button)findViewById(R.id.wallet_paid);
        wallet.setBackgroundColor(Color.parseColor("#FF4081"));
        paid.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
        wallet.setTextColor(Color.WHITE);
        paid.setTextColor(Color.BLACK);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wallet.setBackgroundColor(Color.parseColor("#FF4081"));
                paid.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                wallet.setTextColor(Color.WHITE);
                paid.setTextColor(Color.BLACK);
                setup_reports(wallets);
            }
        });
        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paid.setBackgroundColor(Color.parseColor("#FF4081"));
                wallet.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                wallet.setTextColor(Color.BLACK);
                paid.setTextColor(Color.WHITE);
                setup_reports(wallets_paid);
            }
        });

        final Getter getmarsuleha = new Getter();
        getmarsuleha.execute("http://api.yarbox.co/api/v1/packs/running",token);
        View loading;
        LayoutInflater pinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loading = pinflate.inflate(R.layout.loading, null);
        loading.setBackgroundColor(Color.TRANSPARENT);
         prog = new AlertDialog.Builder(act).create();
        prog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        prog.setInverseBackgroundForced(true);
        prog.setView(loading);
        prog.setCancelable(false);
        prog.show();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                fetch_reports();
            }
        });
    }
    public void fetch_reports(){

            if (new CheckInternet().check())
            {
                final Getter get_reports = new Getter();
                get_reports.execute("http://api.yarbox.co/api/v1/wallets",token);
                final Getter get_paid = new Getter();
                get_paid.execute("http://api.yarbox.co/api/v1/wallets/paid",token);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            get_reports.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wallets = get_reports.mainresponse.toString();
                        try {
                            get_paid.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wallets_paid = get_paid.mainresponse;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setup_reports(wallets);
                            }
                        });
                    }
                });

            }

    }
    public void setup_reports(String json_str){
        root.removeAllViewsInLayout();
        try {
            JSONArray items = new JSONObject(json_str).getJSONArray("items");
            if (items.length() > 0) {
                for (int i = 0; i < items.length(); i++) {
                    int packid = items.getJSONObject(i).getInt("packId");
                    String payment_type = items.getJSONObject(i).getString("type");
                    int price = items.getJSONObject(i).getInt("price");
                    String createdon = items.getJSONObject(i).getString("createdOn");

                    LinearLayout.LayoutParams images_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    View v;
                    LayoutInflater inflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflate.inflate(R.layout.reportcell, null);
                    v.setLayoutParams(images_param);
                    TextView cell_date = (TextView) v.findViewById(R.id.cell_date);
                    Date itemDate = new Date((long)(Double.parseDouble(createdon)*1000));
                    String myDateStr = new SimpleDateFormat("yyyy-MM-dd").format(itemDate);
                    DateConverter converter = new DateConverter();
                    converter.gregorianToPersian(Integer.parseInt(myDateStr.substring(0,4)), Integer.parseInt(myDateStr.substring(5,7)), Integer.parseInt(myDateStr.substring(8,10)));
                    String completedate = converter.getYear() + "/" + converter.getMonth() + "/" + converter.getDay();
                    cell_date.setText(completedate);
                    TextView cell_price = (TextView) v.findViewById(R.id.report_cell_price);
                    cell_price.setText(String.format("%,.0f", (double) price));
                    TextView cell_type = (TextView) v.findViewById(R.id.cell_type);
                    if (payment_type.contains("debtor"))
                        cell_type.setText("واریز");
                    else
                        cell_type.setText("برداشت");
                    TextView cell_code = (TextView) v.findViewById(R.id.cell_code);
                    if (packid == 0)
                        cell_code.setText("ندارد");
                    else
                        cell_code.setText(packid + "");
                    root.addView(v);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            prog.dismiss();
    }
}
