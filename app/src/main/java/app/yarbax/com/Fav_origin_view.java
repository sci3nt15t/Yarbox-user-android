package app.yarbax.com;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/22/19.
 */

public class Fav_origin_view extends AppCompatActivity implements OnMapReadyCallback {



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),Fav_address.class);
        startActivity(go_back);
        finish();
    }

    Double lat;
    Double lng;
    int id  = 0;
    MyDb db = new MyDb();
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.fav_origin_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fav_origin_view_map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        id = i.getIntExtra("id",0);
        Cursor crs = db.get("SELECT * FROM `fav` WHERE `id` = "+id);
        crs.moveToFirst();
        int lat_index = crs.getColumnIndex("lat");
        int lng_index = crs.getColumnIndex("lng");
        lat = crs.getDouble(lat_index);
        lng = crs.getDouble(lng_index);

        GrayEditText address = (GrayEditText)findViewById(R.id.fav_origin_view_address);
        int city_index = crs.getColumnIndex("city");
        int address_index = crs.getColumnIndex("address");
        int plaque_index = crs.getColumnIndex("plaque");
        address.setText(crs.getString(city_index)+ " "+crs.getString(address_index) + " پلاک " + crs.getString(plaque_index));
        GrayEditText number = (GrayEditText)findViewById(R.id.fav_origin_view_number);
        int number_index = crs.getColumnIndex("number");
        number.setText(crs.getString(number_index));
        Button delete = (Button)findViewById(R.id.fav_origin_view_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.insert("DELETE FROM `fav` WHERE `id` = "+id);
                Intent go_back = new Intent(getApplicationContext(),Fav_address.class);
                startActivity(go_back);
                finish();
            }
        });
        Button edit = (Button)findViewById(R.id.fav_origin_view_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_map_view = new Intent(getApplicationContext(),Fav_origin_map.class);
                goto_map_view.putExtra("id",id);
                goto_map_view.putExtra("lat",lat);
                goto_map_view.putExtra("lng",lng);
                startActivity(goto_map_view);
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),12));
        googleMap.addMarker(new MarkerOptions().draggable(false).position(new LatLng(lat,lng)));
    }
}
