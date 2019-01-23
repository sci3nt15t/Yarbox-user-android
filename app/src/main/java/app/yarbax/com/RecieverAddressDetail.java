package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.suke.widget.SwitchButton;

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
import app.yarbax.com.Utilities.Poster;
import app.yarbax.com.Utilities.extension;

/**
 * Created by shayanrhm on 12/31/18.
 */

public class RecieverAddressDetail extends AppCompatActivity {


    Spinner ostan;
    Spinner shahr;
    Button ok;
    String token;
    Executor exec = Executors.newFixedThreadPool(2);
    Activity act;
    PostPack newpack;
    String factorKey;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back = new Intent(getApplicationContext(),ChooseReciever.class);
        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    GrayEditText address;
    GrayEditText plaque;
    GrayEditText phone;
    GrayEditText name;
    MyDb db = new MyDb();
    @Override
    protected void onCreate(Bundle savedinstance)
    {
        super.onCreate(savedinstance);
        setContentView(R.layout.recieveraddressdetail);
        act = this;
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        Intent i = getIntent();
        newpack = (PostPack) i.getSerializableExtra("newpack");
        token = p.getString("token","");
        ostan = (Spinner)findViewById(R.id.rec_ostan);
        shahr = (Spinner)findViewById(R.id.rec_shahr);
        address = (GrayEditText)findViewById(R.id.rec_addr);
        address.setText(newpack.destination.street);
        plaque = (GrayEditText)findViewById(R.id.rec_plaque);
        plaque.setText(newpack.destination.plaque);
        phone = (GrayEditText)findViewById(R.id.rec_phone);
        phone.setText(newpack.destination.receiverPhoneNumber);
        name = (GrayEditText)findViewById(R.id.rec_name);
        name.setText(newpack.destination.receiverName);
        ok = (Button)findViewById(R.id.rec_ok);
        SwitchButton add_fav = (SwitchButton)findViewById(R.id.rec_favorit);
        add_fav.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                AlertDialog.Builder title = new AlertDialog.Builder(act);
                title.setTitle("لطفا عنوان ادرس را تعیین کنید");
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
                                    "VALUES('dest','" + address.getText() + "','" + plaque.getText() + "','" + phone.getText() + "','" + name.getText() + "','" + text.getText() + "', '" + selectedOstan + "','" + selectedShahr + "' )");
                            dialogInterface.dismiss();
                            add_fav.setEnabled(false);
                            add_fav.setEnableEffect(false);
                        } else {
                            new MyAlert(act, "خطا!", "لطفا عنوان را انتخاب نمایید!");
                        }

                    }
                });
                title.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedOstan.length() > 0 && selectedShahr.length() > 0 && address.getText().length() > 0
                        && plaque.getText().length() > 0 && phone.getText().length() > 0 && name.getText().length() > 0)
                {
                    newpack.destination.portId = 0;
                    newpack.receiveType = "doorToDoor";
                    newpack.destination.province = selectedOstan;
                    newpack.destination.city = selectedShahr;
                    newpack.destination.street = address.getText().toString();
                    newpack.destination.plaque = plaque.getText().toString();
                    newpack.destination.receiverPhoneNumber = new extension().ReplaceArabicDigitsWithEnglish(phone.getText().toString());
                    newpack.destination.receiverName = name.getText().toString();
                    newpack.destination.latitude = "a";
                    newpack.destination.longitude = "a";
                    System.out.println(newpack.post());
                    final ProgressDialog prog = new ProgressDialog(act);
                    prog.setCancelable(false);
                    prog.setTitle("لطفا منتطر بمانید");
                    if (prog.isShowing())
                        prog.dismiss();
                    prog.show();
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Poster post = new Poster();
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
                            if (prog.isShowing())
                                prog.dismiss();
                        }
                    });
                }else{
                    new MyAlert(act,"خطا!","تمامی فیلد ها پر شوند!");
                }
            }
        });

        ImageView fav_address = (ImageView)findViewById(R.id.dest_fav_address);
        fav_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_fav = new Intent(getApplicationContext(),Select_Fav_dest.class);
                goto_fav.putExtra("newpack",newpack);
                startActivity(goto_fav);
                finish();
            }
        });



        fetch_ostan();
    }

    JSONArray ostans;
    JSONArray shahrs;
    String selectedOstan;
    String selectedShahr;
    public void fetch_ostan(){
        final ProgressDialog prog = new ProgressDialog(this);
        prog.setCancelable(false);
        prog.setTitle("لطفا منتطر بمانید");
        if (prog.isShowing())
            prog.dismiss();
        prog.show();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter get_ostan = new Getter();
                get_ostan.execute("http://api.yarbox.co/api/v1/provinces",token);
                while (get_ostan.mainresponse.length() == 0)
                {
                    System.out.println("while!");
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
                if (prog.isShowing())
                    prog.dismiss();
            }
        });
    }
    public void fetch_shahr(){
        final ProgressDialog prog = new ProgressDialog(this);
        prog.setCancelable(false);
        prog.setTitle("لطفا منتطر بمانید");
        if (prog.isShowing())
            prog.dismiss();
        prog.show();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter get_shahr = new Getter();
                get_shahr.execute("http://api.yarbox.co/api/v1/provinces/"+selectedOstan+"/GetCityByType?type=1",token);

                while (get_shahr.mainresponse.length() == 0)
                {
                    System.out.println("while!");
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
                if (prog.isShowing())
                    prog.dismiss();
            }
        });
    }
    ArrayAdapter<String> ostanadapter;
    public void setup_ostan(){
        List<String> ostan_arr = new ArrayList<String>();
        if (newpack.destination.province != null) {
            ostan_arr.add(newpack.destination.province);
        }
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
            if (newpack.destination.province != null) {
                selectedOstan = newpack.destination.province;
            }
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
        if (newpack.destination.city !=  null) {
            shahr_arr.add(newpack.destination.city);
        }
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
            if (newpack.destination.province != null) {
                selectedShahr = newpack.destination.city;
            }
        } catch (JSONException e) {
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
