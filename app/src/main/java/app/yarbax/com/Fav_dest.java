package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/21/19.
 */

public class Fav_dest extends AppCompatActivity {


    Spinner province;
    Spinner city;
    GrayEditText address;
    GrayEditText plaque;
    GrayEditText name;
    GrayEditText number;
    Button ok;
    String token;
    Executor exec;
    MyDb db = new MyDb();
    Activity act;
    int id = 0;
    public void goback(){
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_dest);

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
        toolbar_title.setText(" مقصد");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        id = i.getIntExtra("id",0);
        act = this;
        exec = Executors.newFixedThreadPool(3);
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        province = (Spinner)findViewById(R.id.fav_rec_ostan);
        city = (Spinner)findViewById(R.id.fav_rec_shahr);
        fetch_ostan();

        address = (GrayEditText)findViewById(R.id.fav_rec_addr);
        plaque = (GrayEditText)findViewById(R.id.fav_rec_plaque);
        name = (GrayEditText)findViewById(R.id.fav_rec_name);
        number = (GrayEditText)findViewById(R.id.fav_rec_phone);


        System.out.println(id);
        if (id != 0) {
            Cursor crs = db.get("SELECT * FROM `fav` WHERE `id` = "+id);
            crs.moveToFirst();
            int address_index = crs.getColumnIndex("address");
            address.setText(crs.getString(address_index));
            int plaque_index = crs.getColumnIndex("plaque");
            plaque.setText(crs.getString(plaque_index));
            int name_index = crs.getColumnIndex("name");
            name.setText(crs.getString(name_index));
            int number_index = crs.getColumnIndex("number");
            number.setText(crs.getString(number_index));
        }

        ok = (Button)findViewById(R.id.fav_rec_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (address.getText().length() > 0 && plaque.getText().length() > 0 && number.getText().length() > 0 && name.getText().length() > 0) {
                        if (id == 0) {
                            AlertDialog.Builder title = new AlertDialog.Builder(act);
                            title.setTitle("لطفا عنوان آدرس را تعیین کنید");
                            final EditText text = new EditText(act);
                            text.setHint("عنوان");
                            title.setView(text);
                            title.setNegativeButton("لفو", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            title.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (text.getText().length() > 0) {
                                        db.insert("INSERT INTO fav(`location`,`address`,`plaque`,`number`,`name`,`title`,`province`,`city`) " +
                                                "VALUES('dest','" + address.getText() + "','" + plaque.getText() + "','" + number.getText() + "','" + name.getText() + "','" + text.getText() + "', '" + selectedOstan + "','" + selectedShahr + "' )");
                                        Intent go_back = new Intent(act, Fav_address.class);
                                        startActivity(go_back);
                                        finish();
                                    } else {
                                        new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                                    }

                                }
                            });
                            title.show();
                        }else{
                            db.insert("UPDATE `fav` SET `address` = '"+address.getText()+"',`plaque` = '"+plaque.getText()+"',`number`='"+number.getText()+"'," +
                                    "`name`='"+name.getText()+"',`province`='"+selectedOstan+"',`city`='"+selectedShahr+"' WHERE `id` = "+id);
                            Intent go_back = new Intent(act, Fav_address.class);
                            startActivity(go_back);
                            finish();
                        }
                    } else {
                        new MyAlert(act, "خطا!", "لطفا تمامی فیلد ها را پر نمایید!");
                    }
            }
        });
    }

    JSONArray ostans;
    JSONArray shahrs;
    String selectedOstan;
    String selectedShahr;
    public void fetch_ostan(){
        View loading;
        LayoutInflater pinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loading = pinflate.inflate(R.layout.loading, null);
        loading.setBackgroundColor(Color.TRANSPARENT);
        android.app.AlertDialog prog = new android.app.AlertDialog.Builder(act).create();
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
        android.app.AlertDialog prog = new android.app.AlertDialog.Builder(act).create();
        prog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        prog.setInverseBackgroundForced(true);
        prog.setView(loading);
        prog.setCancelable(false);
        prog.show();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter get_shahr = new Getter();
                get_shahr.execute("http://api.yarbox.co/api/v1/provinces/"+selectedOstan+"/GetCityByType?type=1",token);

                try {
                    get_shahr.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
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
        province.setAdapter(ostanadapter);
        try {
            selectedOstan = ostans.getString(0);
            fetch_shahr();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        shahradapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item,shahr_arr);
        shahradapter.setDropDownViewResource(R.layout.spinner_item);
        city.setAdapter(shahradapter);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    selectedShahr = shahrs.getJSONObject(i).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
