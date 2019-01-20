package app.yarbax.com;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import app.yarbax.com.Utilities.Getter;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class DriverInfo extends AppCompatActivity implements OnMapReadyCallback {

    String token;
    String driver_info_json;
    ImageView bike;
    ImageView car;
    ImageView vanet;
    SupportMapFragment map;
    ImageView driver_image;
    TextView plaque;
    TextView name;
    TextView payment;
    TextView where;
    Button call;
    Button cont;
    Button cancel;
    JSONObject driver_json;
    String lat;
    String lng;
    int id;
    boolean iscash;
    boolean payorigin;
    @Override
    protected void onCreate(Bundle savedinstance)
    {
        super.onCreate(savedinstance);
        setContentView(R.layout.driverinfo);
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        Intent i = getIntent();
        driver_info_json = i.getStringExtra("info");
        id = i.getIntExtra("id",0);
        iscash = i.getBooleanExtra("iscash",false);
        payorigin = i.getBooleanExtra("payorigin",false);
        bike = (ImageView)findViewById(R.id.driver_bike);
        car = (ImageView)findViewById(R.id.driver_car);
        vanet = (ImageView)findViewById(R.id.driver_vanet);
        map = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        map.getMapAsync(this);
        driver_image = (ImageView)findViewById(R.id.driver_image);
        plaque = (TextView) findViewById(R.id.driver_plaque);
        name = (TextView)findViewById(R.id.driver_name);
        payment = (TextView)findViewById(R.id.driver_payment);
        where = (TextView)findViewById(R.id.driver_pay_where);
        call = (Button)findViewById(R.id.driver_call);
        cont = (Button)findViewById(R.id.driver_cont);
        cancel = (Button)findViewById(R.id.driver_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Getter cancel_pack = new Getter();
                cancel_pack.execute("http://api.yarbox.co/api/v1/packs/cancelPack?id="+id,token);
                try {
                    cancel_pack.get();
                    Intent goto_main = new Intent(getApplicationContext(),MainActivity.class);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goto_main);
                    finish();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            driver_json = new JSONObject(driver_info_json);
            //driver_json = new JSONObject(temp);
            if (iscash && payorigin)
            {
                payment.setText("انلاین");
                where.setText("مبدا");
            }
            if (!iscash && !payorigin)
            {
                payment.setText("نقدی");
                where.setText("مقصد");
            }
            if (!iscash && payorigin )
            {
                payment.setText("نقدی");
                where.setText("مبدا");
            }
            cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goto_main = new Intent(getApplicationContext(),MainActivity.class);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goto_main);
                    finish();
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                            Intent call = new Intent(Intent.ACTION_DIAL);
                            try {
                                call.setData(Uri.parse("tel:" + driver_json.getString("mobile")));
                                startActivity(call);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                }
            });
            name.setText(driver_json.getString("firstName") + " " + driver_json.getString("lastName"));
            plaque.setText(driver_json.getString("plaque"));
            lat = driver_json.getString("latitude");
            lng = driver_json.getString("longitude");
            switch (driver_json.getInt("vehicleTypeId"))
            {
                case 1:
                    bike.setImageDrawable(getResources().getDrawable(R.mipmap.bikecolor));
                    break;
                case 2:
                    car.setImageDrawable(getResources().getDrawable(R.mipmap.carcolor));
                    break;
                case 3:
                    vanet.setImageDrawable(getResources().getDrawable(R.mipmap.vanetcolor));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                get_pic();
            }
        }).start();
    }
    Bitmap mIcon11 = null;
    public void get_pic(){
        String urldisplay = null;
        try {
            urldisplay = "http://file.yarbox.co/images/driverinfoimage/"+driver_json.getString("photoName");
            System.out.println(urldisplay);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mIcon11 = BitmapFactory.decodeStream(new URL(urldisplay).openConnection().getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                driver_image.setImageBitmap(mIcon11);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)),15));
        googleMap.addMarker(new MarkerOptions()
                .position(
                        new LatLng(Double.parseDouble(lat),Double.parseDouble(lng))
                ).draggable(false));
    }
}
