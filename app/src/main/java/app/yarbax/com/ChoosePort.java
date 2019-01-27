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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.Getter;
import app.yarbax.com.Utilities.Poster;

/**
 * Created by shayanrhm on 12/31/18.
 */

public class ChoosePort extends AppCompatActivity implements OnMapReadyCallback {


    Spinner ostan;
    Spinner shahr;
    GrayEditText rec_name;
    GrayEditText rec_phone;
    Button ok;
    Executor exec = Executors.newFixedThreadPool(2);
    String token;
    PostPack newpack;
    Activity act;
    GoogleMap map;
    String factorKey;

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),ChooseReciever.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    public void goback(){
        Intent go_back = new Intent(getApplicationContext(),ChooseReciever.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.chooseport);

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
        toolbar_title.setText("پورت مقصد");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        act = this;
        Intent i = getIntent();
        newpack = (PostPack) i.getSerializableExtra("newpack");
        newpack.receiveType = "doorToDoor";
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        ostan = (Spinner)findViewById(R.id.port_ostan);
        shahr = (Spinner)findViewById(R.id.port_shahr);
        rec_name = (GrayEditText)findViewById(R.id.port_rec_name);
        rec_phone = (GrayEditText)findViewById(R.id.port_rec_phone);
        ok = (Button)findViewById(R.id.port_ok);
        fetch_ostan();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.port_map);
        mapFragment.getMapAsync(this);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOstan.length() > 0 && selectedShahr.length() > 0 &&
                        rec_phone.getText().length() > 0 && rec_name.getText().length() > 0)
                {
                    newpack.destination.portId = portid;
                    newpack.receiveType = "port";
                    newpack.destination.province = selectedOstan;
                    newpack.destination.city = selectedShahr;
                    newpack.destination.street = "a";
                    newpack.destination.plaque = "a";
                    newpack.destination.receiverPhoneNumber = rec_phone.getText().toString();
                    newpack.destination.receiverName = rec_name.getText().toString();
                    newpack.destination.latitude = portlat;
                    newpack.destination.longitude = portlong;
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
                            try {
                                Poster post = new Poster();
                                System.out.println(newpack.post());
                                post.execute("http://api.yarbox.co/api/v1/packs",newpack.post(),token);
                                post.get();
                                factorKey = post.factorkey;
                                Intent goto_factor = new Intent(getApplicationContext(),NewFactor.class);
                                goto_factor.putExtra("key",factorKey);
                                goto_factor.putExtra("newpack",newpack);
                                startActivity(goto_factor);
                                finish();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                                prog.dismiss();
                        }
                    });
                }else{
                    new MyAlert(act,"خطا!","تمامی فیلد ها پر شوند!");
                }
            }
        });

    }
    JSONArray ostans;
    JSONArray shahrs;
    String selectedOstan;
    String selectedShahr;
    String portlat;
    String portlong;
    int portid;
    public void fetch_ostan(){
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
                Getter get_ostan = new Getter();
                get_ostan.execute("http://api.yarbox.co/api/v1/provinces",token);
                try {
                    get_ostan.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println(get_ostan.mainresponse);
                    ostans = new JSONObject(get_ostan.mainresponse).getJSONArray("items");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setup_ostan();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    prog.dismiss();
            }
        });
    }
    public void fetch_shahr(){
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
                Getter get_shahr = new Getter();
                get_shahr.execute("http://api.yarbox.co/api/v1/provinces/"+selectedOstan+"/GetCityByType?type=0",token);

                try {
                    get_shahr.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println(get_shahr.mainresponse);
                    shahrs = new JSONObject(get_shahr.mainresponse).getJSONArray("items");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setup_shahr();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    prog.dismiss();
            }
        });
    }
    ArrayAdapter<String> ostanadapter;
    public void setup_ostan(){
        List<String> ostan_arr = new ArrayList<String>();
        for (int i=0;i<ostans.length();i++)
        {
            try {
                ostan_arr.add(ostans.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ostanadapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item,ostan_arr);
        ostanadapter.setDropDownViewResource(R.layout.spinner_item);
        ostan.setAdapter(ostanadapter);
        try {
            selectedOstan = ostans.getString(0);
            fetch_shahr();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ostan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedOstan = ostans.getString(i);
                    fetch_shahr();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    ArrayAdapter<String> shahradapter;
    public void setup_shahr(){
        List<String> shahr_arr = new ArrayList<String>();
        for (int i=0;i<shahrs.length();i++)
        {
            try {
                shahr_arr.add(shahrs.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            selectedShahr = shahrs.getJSONObject(0).getString("name");
            Getter getport = new Getter();
            getport.execute("http://api.yarbox.co/api/v1/ports?province="+selectedOstan+"&countis="+selectedShahr);
            getport.get();
            JSONObject port = new JSONObject(getport.mainresponse);
            portlat = port.getString("latitude");
            portlong = port.getString("longitude");
            portid = port.getInt("id");
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(portlat),Double.parseDouble(portlong)),12));
            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(portlat),Double.parseDouble(portlong)))
            .draggable(false)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        shahradapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item,shahr_arr);
        shahradapter.setDropDownViewResource(R.layout.spinner_item);
        shahr.setAdapter(shahradapter);
        shahr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedShahr = shahrs.getJSONObject(i).getString("name");
                    Getter getport = new Getter();
                    getport.execute("http://api.yarbox.co/api/v1/ports?province="+selectedOstan+"&countis="+selectedShahr);
                    getport.get();
                    JSONObject port = new JSONObject(getport.mainresponse);
                    portlat = port.getString("latitude");
                    portlong = port.getString("longitude");
                    portid = port.getInt("id");
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(portlat),Double.parseDouble(portlong)),12));
                    map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(portlat),Double.parseDouble(portlong)))
                            .draggable(false)
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
