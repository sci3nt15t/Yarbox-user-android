package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
        plaque = (GrayEditText)findViewById(R.id.rec_plaque);
        phone = (GrayEditText)findViewById(R.id.rec_phone);
        name = (GrayEditText)findViewById(R.id.rec_name);
        ok = (Button)findViewById(R.id.rec_ok);
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
                    newpack.destination.receiverPhoneNumber = phone.getText().toString();
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
