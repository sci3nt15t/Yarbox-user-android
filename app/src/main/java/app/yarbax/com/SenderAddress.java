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
import android.view.View;
import android.widget.Button;

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

public class SenderAddress extends FragmentActivity implements OnMapReadyCallback {


    SigninEdittext maptext;
    Address bestMatch;
    Button ok;
    Activity act;
    PostPack newpack;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(this,PackDetail.class);
        go_back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedinstace) {
        super.onCreate(savedinstace);
        setContentView(R.layout.senderaddress);
        Intent i = getIntent();
        //newpack = (PostPack) i.getSerializableExtra("newpack");
        act = this;
        maptext = (SigninEdittext)findViewById(R.id.maptext);
        newpack = (PostPack) i.getSerializableExtra("newpack");
        newpack.origin.province = "تهران";
        newpack.origin.city = "تهران";
        newpack.origin.latitude = "";
        newpack.origin.longitude = "";
        newpack.origin.street = "";

        CedarMaps.getInstance()
                .setClientID("yarbox-15226092103437932740")
                .setClientSecret("btkUAHlhcmJveO_SkX2MDWVK1QY8pqFoSBLBu3LH1FhYnxWNP1RkKF5T")
                .setContext(this);

        maptext.setText("خارج از محدوده دسترسی");
        ok = (Button)findViewById(R.id.senderaddress_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!maptext.getText().toString().contains("خارج از محدوده دسترسی"))
                {
                    Intent goto_senderaddressdetail = new Intent(getApplicationContext(),SenderAddressDetail.class);
                    goto_senderaddressdetail.putExtra("newpack",newpack);
                    startActivity(goto_senderaddressdetail);
                    finish();
                }else{
                    new MyAlert(act,"خطا!","خارج از محدوده دسترسی");
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getaddressfromcedar(35.705655,51.390319);

    }
    GoogleMap map;

    public void getaddressfromcedar(Double lat,Double lng){
        CedarMaps.getInstance().reverseGeocode(new com.mapbox.mapboxsdk.geometry.LatLng(lat, lng), new ReverseGeocodeResultListener() {
            @Override
            public void onSuccess(@NonNull ReverseGeocode result) {
                if (result.getProvince().contains("تهران")) {
                    maptext.setText(result.getDistrict() + " , " + result.getAddress());
                    newpack.origin.province = "تهران";
                    newpack.origin.city = "تهران";
                    newpack.origin.latitude = lat+"";
                    newpack.origin.longitude = lng+"";
                    newpack.origin.street = result.getProvince() + " , " + result.getDistrict() + " , " + result.getAddress();
                }else{
                    maptext.setText("خارج از محدوده دسترسی");
                }
            }

            @Override
            public void onFailure(@NonNull String errorMessage) {
                maptext.setText("خارج از محدوده دسترسی");
            }
        });

//        Geocoder geo = new Geocoder(this,new Locale("fa"));
//        try
//        {
//            List<Address> address = geo.getFromLocation(lat,lng,1);
//            bestMatch = (address.isEmpty() ? null : address.get(0));
//            maptext.setText(bestMatch.getFeatureName());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.705655,51.390319),12));
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                getaddressfromcedar(cameraPosition.target.latitude,cameraPosition.target.longitude);
//                newpack.origin.latitude = cameraPosition.target.latitude+"";
//                newpack.origin.longitude = cameraPosition.target.longitude+"";
            }
        });

    }
}
