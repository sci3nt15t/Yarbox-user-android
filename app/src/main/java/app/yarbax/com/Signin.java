package app.yarbax.com;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.Extension;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.MyViews.SigninEdittext;
import app.yarbax.com.Utilities.*;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class Signin extends AppCompatActivity {

    SigninEdittext phone;
    Button login_btn;
    Activity act;
    Executor exec = Executors.newFixedThreadPool(2);
    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.signin);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    5
            );
        }
        phone = (SigninEdittext)findViewById(R.id.login_phone);
        login_btn = (Button)findViewById(R.id.login_btn);
        act = this;
        TextView reg = (TextView)findViewById(R.id.login_notreg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goto_reg = new Intent(getApplicationContext(),Reg.class);
                startActivity(goto_reg);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().length() == 11)
                {
                        if (new CheckInternet().check()) {
                            final Network net = new Network(getApplicationContext());
                            extension converter = new extension();
                            String finalphone = converter.ReplaceArabicDigitsWithEnglish(phone.getText().toString())
                                    .replace("+98","0").replaceAll(" ","");
                            net.execute("http://api.yarbox.co/api/v1/account/retry-verify", "{\n" +
                                    "  \"phoneNumber\": \"" + finalphone + "\"\n" +
                                    "}");
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
                            exec = Executors.newFixedThreadPool(2);
                            exec.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        net.get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    boolean status = false;
                                    try {
                                        status = new JSONObject(net.mainresponse).getBoolean("isSuccess");
                                        if (status) {
                                            Intent i = new Intent(getApplicationContext(), Approve.class);
                                            i.putExtra("phone", phone.getText().toString());
                                            startActivity(i);
                                            finish();
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MyAlert(act,"خطا!","این شماره ثبت نام نشده است");
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new MyAlert(act,"خطا!","خطا در دریافت!");
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                    prog.dismiss();
                                }
                            });

                        }
                        else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"لطفا اتصال خود را به اینترنت چک کنید",Toast.LENGTH_LONG).show();
                                    }
                                });
                        }


                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MyAlert(Signin.this,"شماره تلفن","شماره تلفن وارد شده صحیح نیست!");
                        }
                    });
                }
            }
        });
    }
}
