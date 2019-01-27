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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.GrayBorderEditText;
import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.GeneralPoster;
import app.yarbax.com.Utilities.Getter;
import app.yarbax.com.Utilities.Network;
import app.yarbax.com.Utilities.Poster;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class NewFactor extends AppCompatActivity {

    String factorkey;
    String token;
    GrayBorderEditText rec_name;
    GrayBorderEditText rec_phone;
    GrayBorderEditText rec_addr;
    GrayBorderEditText send_name;
    GrayBorderEditText send_phone;
    GrayBorderEditText send_addr;
    ImageView vehicle;
    TextView price;
    Button payorigin;
    Button payonline;
    Button paydest;
    GrayEditText cred;
    LinearLayout inc_cred;
    Button ok;
    int credit;
    JSONObject factor_mainjson;
    JSONObject sender;
    JSONObject reciever;
    Executor exec = Executors.newFixedThreadPool(2);
    boolean iscashpayment = true;
    boolean payatorigin = false;
    boolean isitemselected = false;
    Activity act;
    PostPack newpack;
    boolean reorder = false;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent go_back;
        if (reorder)
            go_back = new Intent(getApplicationContext(),MainActivity.class);

        else if (newpack.receiveType.contains("port"))
            go_back = new Intent(getApplicationContext(),ChoosePort.class);
        else
            go_back = new Intent(getApplicationContext(),RecieverAddressDetail.class);

        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    public void goback(){
        Intent go_back;
        if (reorder)
            go_back = new Intent(getApplicationContext(),MainActivity.class);

        else if (newpack.receiveType.contains("port"))
            go_back = new Intent(getApplicationContext(),ChoosePort.class);
        else
            go_back = new Intent(getApplicationContext(),RecieverAddressDetail.class);

        go_back.putExtra("newpack",newpack);
        startActivity(go_back);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.newfactor);

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
        toolbar_title.setText("فاکتور مرسوله");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        act = this;
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        Intent i = getIntent();
        reorder = i.getBooleanExtra("reorder",false);
        factorkey = i.getStringExtra("key");
        System.out.println(factorkey);
        newpack = (PostPack) i.getSerializableExtra("newpack");
        cred = (GrayEditText) findViewById(R.id.factor_cred);
        rec_name = (GrayBorderEditText)findViewById(R.id.factor_rec_name);
        rec_phone = (GrayBorderEditText)findViewById(R.id.factor_rec_phone);
        rec_addr = (GrayBorderEditText)findViewById(R.id.factor_rec_addr);
        send_name = (GrayBorderEditText)findViewById(R.id.factor_send_name);
        send_phone = (GrayBorderEditText)findViewById(R.id.factor_send_phone);
        send_addr = (GrayBorderEditText)findViewById(R.id.factor_send_addr);
        vehicle = (ImageView)findViewById(R.id.factor_vehicle_type);
        price = (TextView)findViewById(R.id.factor_price);
        payorigin = (Button)findViewById(R.id.factor_payorigin);
        payonline = (Button)findViewById(R.id.factor_payonline);
        paydest = (Button)findViewById(R.id.factor_paydest);

        payorigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payorigin.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn_curve));
                payonline.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                paydest.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                payatorigin = true;
                iscashpayment = false;
                isitemselected = true;
            }
        });
        payonline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payorigin.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                payonline.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn_curve));
                paydest.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                payatorigin = true;
                iscashpayment = true;
                isitemselected = true;
            }
        });
        paydest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payorigin.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                payonline.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_btn_curve));
                paydest.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_btn_curve));
                payatorigin = false;
                iscashpayment = false;
                isitemselected = true;
            }
        });
        inc_cred = (LinearLayout)findViewById(R.id.factor_increase_cred);
        inc_cred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_credit = new Intent(getApplicationContext(),IncreaseCredit.class);
                startActivity(goto_credit);
            }
        });
        ok = (Button)findViewById(R.id.factor_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isitemselected) {
                    try {
                        if (/** other **/((!iscashpayment && payatorigin) || (!iscashpayment && !payatorigin)) /** other **/ ||
                                /** online **/((iscashpayment && payatorigin) && (credit >= factor_mainjson.getInt("price")))  /** online **/) {
                            final String request = "{\n" +
                                    "  \"id\": " + factor_mainjson.getInt("id") + ",\n" +
                                    "  \"payAtOrigin\": " + payatorigin + ",\n" +
                                    "  \"isCashPayment\": " + iscashpayment + "\n" +
                                    "}";
                            final GeneralPoster post = new GeneralPoster();
                            post.execute("http://api.yarbox.co/api/v1/packs/accept", request, token);
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
                                        post.get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    JSONObject isSuccess = null;
                                    try {
                                        isSuccess = new JSONObject(post.mainresponse.toString());
                                        if (isSuccess.getBoolean("isSuccess")) {
                                            Intent goto_safir = new Intent(getApplicationContext(), SearchingSafir.class);
                                            goto_safir.putExtra("id", factor_mainjson.getInt("id"));
                                            goto_safir.putExtra("iscash", iscashpayment);
                                            goto_safir.putExtra("payorigin", payatorigin);
                                            startActivity(goto_safir);
                                            System.out.println(request);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                        prog.dismiss();
                                }
                            });
                        } else if (((iscashpayment && payatorigin) && (credit < factor_mainjson.getInt("price")))) {
                            new MyAlert(act, "خطا!", "اعتبار کافی نیست!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    new MyAlert(act,"خطا!","لطفا یکی از گزینه های پرداخت را انتخاب نمایید!");
                }
            }
        });
        fetch_factor();
        fetch_cred();
        Timer cred_timer = new Timer();
        TimerTask check_credit = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetch_cred();
                    }
                });
            }
        };
        cred_timer.schedule(check_credit,1000*3);
    }
    public void fetch_cred(){
        Getter get_cred = new Getter();
        try {
            JSONObject cred_json = new JSONObject(get_cred.execute("http://api.yarbox.co/api/v1/account/check",token).get());
            credit = cred_json.getInt("credit");
            cred.setText(String.format("%,.0f", (double) credit));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void fetch_factor(){
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
                Getter get_factor = new Getter();
                try {
                    System.out.println(factorkey + " : " + token);
                    factor_mainjson = new JSONObject(get_factor.execute("http://api.yarbox.co/api/v1/packs/factor/"+factorkey,token).get());
                    sender = factor_mainjson.getJSONObject("sender");
                    reciever = factor_mainjson.getJSONObject("receiver");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rec_name.setText(reciever.getString("name"));
                                rec_phone.setText(reciever.getString("phoneNumber"));
                                rec_addr.setText(reciever.getString("address"));

                                send_name.setText(sender.getString("name"));
                                send_phone.setText(sender.getString("phoneNumber"));
                                send_addr.setText(sender.getString("address"));

                                switch (factor_mainjson.getInt("vehicleId"))
                                {
                                    case 1:
                                        vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.bikecolor));
                                        break;
                                    case 2:
                                        vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.carcolor));
                                        break;
                                    case 3:
                                        vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.vanetcolor));
                                        break;
                                }
                                price.setText(String.format("%,.0f", (double) factor_mainjson.getInt("price")) + " تومان ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                    prog.dismiss();
            }
        });
    }
}
