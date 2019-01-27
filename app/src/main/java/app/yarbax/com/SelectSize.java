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
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.CheckInternet;
import app.yarbax.com.Utilities.Getter;

import java.io.Serializable;

/**
 * Created by shayanrhm on 12/30/18.
 */

public class SelectSize extends AppCompatActivity implements Serializable {



    PostPack newpack = new PostPack();
    LinearLayout vehicle_view;
    ImageView vehicleimage;
    TextView type;
    TextView des;
    TextView weight;
    Executor exec =  Executors.newFixedThreadPool(2);
    String data;
    String token;
    Activity act;
    int select = 0;
    @Override
    protected void onCreate(Bundle savedinstace){
        super.onCreate(savedinstace);
        setContentView(R.layout.selectsize);
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
        toolbar_title.setText("انتخاب وسیله ی نقلیه");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        vehicle_view = (LinearLayout)findViewById(R.id.selectsize_vehicleview);
        vehicleimage = (ImageView)findViewById(R.id.vehicleimage);
        type = (TextView)findViewById(R.id.vehicletype);
        des = (TextView)findViewById(R.id.description);
        weight = (TextView)findViewById(R.id.weightDescription);
        newpack.vehicleId = 1;
        Button SelectSize = (Button)findViewById(R.id.selectsize);
        SelectSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_packdetail = new Intent(getApplicationContext(),PackDetail.class);
                goto_packdetail.putExtra("newpack",newpack);
                finish();
                startActivity(goto_packdetail);
            }
        });
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");

            if (new CheckInternet().check())
            fetchsizes();
            else{
                new MyAlert(this,"خطا","دسترسی خود را به اینترنت چک کنید!");
                goback();
            }

    }

    public void fetchsizes(){
        View loading;
        LayoutInflater pinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loading = pinflate.inflate(R.layout.loading, null);
        loading.setBackgroundColor(Color.TRANSPARENT);
        AlertDialog prog = new AlertDialog.Builder(act).create();
        prog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        prog.setInverseBackgroundForced(true);
        prog.setView(loading);
        prog.setCancelable(false);
        prog.show();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter getvehicles = new Getter();
                getvehicles.execute("http://api.yarbox.co/api/v1/vehicles",token);
                try {
                    getvehicles.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                data = getvehicles.mainresponse.toString();
                prog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupsizes(select);
                    }
                });

                    prog.dismiss();
            }
        });
    }
    public void setupsizes(int selected)
    {
        vehicle_view.removeAllViewsInLayout();
        try {
            final JSONArray vehicles = new JSONObject(data).getJSONArray("items");
            for (int i=0;i<vehicles.length() - 1;i++)
            {
                JSONObject vehicle = vehicles.getJSONObject(i);
                ImageView img = new ImageView(getApplicationContext());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                img.setLayoutParams(param);
                if (i == select)
                {
                    switch (i)
                    {
                        case 0:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.bikecolor));
                            break;
                        case 1:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.carcolor));
                            break;
                        case 2:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.vanetcolor));
                            break;
                    }
                    type.setText(vehicles.getJSONObject(i).getString("name"));
                    des.setText(vehicles.getJSONObject(i).getString("description"));
                    weight.setText(vehicles.getJSONObject(i).getString("weightDescription"));
                    newpack.vehicleId = vehicles.getJSONObject(i).getInt("id");
                }else{
                    switch (i)
                    {
                        case 0:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.bikebw));
                            break;
                        case 1:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.carbw));
                            break;
                        case 2:
                            img.setImageDrawable(getResources().getDrawable(R.mipmap.vanetbw));
                            break;
                    }
                }
                final int finalI = i;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select = finalI;
                        setupsizes(select);
                        switch (select)
                        {
                            case 0:
                                try {
                                    type.setText(vehicles.getJSONObject(finalI).getString("name"));
                                    des.setText(vehicles.getJSONObject(finalI).getString("description"));
                                    weight.setText(vehicles.getJSONObject(finalI).getString("weightDescription"));
                                    newpack.vehicleId = vehicles.getJSONObject(finalI).getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                vehicleimage.setImageDrawable(getResources().getDrawable(R.mipmap.bikecolor));
                                break;
                            case 1:
                                vehicleimage.setImageDrawable(getResources().getDrawable(R.mipmap.carcolor));
                                break;
                            case 2:
                                vehicleimage.setImageDrawable(getResources().getDrawable(R.mipmap.vanetcolor));
                                break;
                        }
                    }
                });
                vehicle_view.addView(img);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent goto_main = new Intent(this,MainActivity.class);
        startActivity(goto_main);
        finish();
    }
    public void goback(){
        Intent goto_main = new Intent(this,MainActivity.class);
        startActivity(goto_main);
        finish();
    }

}
