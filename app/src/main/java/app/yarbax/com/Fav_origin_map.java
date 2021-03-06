package app.yarbax.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
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
    Double lat = 35.705655;
    Double lng = 51.390319;
    SharedPreferences pref;

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

        pref = getSharedPreferences("mypref",MODE_PRIVATE);
        if (pref.getString("crlat","").length() > 0)
            lat = Double.parseDouble(pref.getString("crlat",""));
        if (pref.getString("crlat","").length() > 0)
            lng = Double.parseDouble(pref.getString("crlat",""));

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
        ImageView crlocation = (ImageView)findViewById(R.id.fav_map_cr);
        crlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pref.getString("crlat","")),Double.parseDouble(pref.getString("crlng",""))),15));
            }
        });
        if(i.getIntExtra("id",0) == 0)
        {
        getaddressfromcedar(lat,lng);
        lat = lat;
        lng = lng;
        }else{
            lat = i.getDoubleExtra("lat",0.0);
            lng = i.getDoubleExtra("lng",0.0);
            getaddressfromcedar(lat,lng);
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fav_place_autocomplete_fragment);
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("IR")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(filter);
        maptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(act);
                    startActivityForResult(intent,1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });

    }
    GoogleMap map;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
            }
        }

    }
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(pref.getString("crlat","")),Double.parseDouble(pref.getString("crlng",""))),15));
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
