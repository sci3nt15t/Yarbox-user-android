package app.yarbax.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.MyViews.SigninEdittext;

/**
 * Created by shayanrhm on 12/30/18.
 */

public class Fav_origin_map extends AppCompatActivity implements OnMapReadyCallback {


    SigninEdittext maptext;
    Address bestMatch;
    Button ok;
    Activity act;
    Double lat;
    Double lng;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(act,Fav_address.class);
        startActivity(go_back);
        finish();
    }

    public void goback(){
        Intent go_back = new Intent(act,Fav_address.class);
        startActivity(go_back);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedinstace) {
        super.onCreate(savedinstace);
        setContentView(R.layout.fav_address_map);
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
        toolbar_title.setText("انتخاب مبدا");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Intent i = getIntent();
        //newpack = (PostPack) i.getSerializableExtra("newpack");
        act = this;
        maptext = (SigninEdittext)findViewById(R.id.fav_map_text);

        CedarMaps.getInstance()
                .setClientID("yarbox-15226092103437932740")
                .setClientSecret("btkUAHlhcmJveO_SkX2MDWVK1QY8pqFoSBLBu3LH1FhYnxWNP1RkKF5T")
                .setContext(this);

        maptext.setText("خارج از محدوده دسترسی");
        ok = (Button)findViewById(R.id.fav_map_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!maptext.getText().toString().contains("خارج از محدوده دسترسی"))
                {
                    Intent goto_fav_detail = new Intent(getApplicationContext(),Fav_origin_detail.class);
                    goto_fav_detail.putExtra("lat",lat);
                    goto_fav_detail.putExtra("lng",lng);
                    if (i.getIntExtra("id",0) != 0)
                        goto_fav_detail.putExtra("id",i.getIntExtra("id",0));
                    startActivity(goto_fav_detail);
                    finish();
                }else{
                    new MyAlert(act,"خطا!","خارج از محدوده دسترسی");
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fav_map);
        mapFragment.getMapAsync(this);
        if(i.getIntExtra("id",0) == 0)
        {
        getaddressfromcedar(35.705655,51.390319);
        lat = 35.705655;
        lng = 51.390319;
        }else{
            lat = i.getDoubleExtra("lat",0.0);
            lng = i.getDoubleExtra("lng",0.0);
            getaddressfromcedar(lat,lng);
        }

    }
    GoogleMap map;

    public void getaddressfromcedar(Double lat,Double lng){
        CedarMaps.getInstance().reverseGeocode(new com.mapbox.mapboxsdk.geometry.LatLng(lat, lng), new ReverseGeocodeResultListener() {
            @Override
            public void onSuccess(@NonNull ReverseGeocode result) {
                if (result.getProvince().contains("تهران")) {
                    maptext.setText(result.getDistrict() + " , " + result.getAddress());
                }else{
                    maptext.setText("خارج از محدوده دسترسی");
                }
            }

            @Override
            public void onFailure(@NonNull String errorMessage) {
                maptext.setText("خارج از محدوده دسترسی");
            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12));
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                getaddressfromcedar(cameraPosition.target.latitude,cameraPosition.target.longitude);
                lat = cameraPosition.target.latitude;
                lng = cameraPosition.target.longitude;
            }
        });

    }
}
