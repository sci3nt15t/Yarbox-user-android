package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.model.routing.Instruction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.MyViews.SigninEdittext;
import app.yarbax.com.Utilities.CheckInternet;
import app.yarbax.com.Utilities.Getter;
import app.yarbax.com.Utilities.Network;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class Approve extends AppCompatActivity {


    SigninEdittext code;
    Button approve;
    TextView resend;
    Intent i;
    Activity act;
    String phone;
    SharedPreferences.Editor mypref;
    JSONObject token;
    Executor exec = Executors.newFixedThreadPool(2);
    @Override
    protected void onCreate(Bundle savedinstance){
        super.onCreate(savedinstance);
        setContentView(R.layout.approve);
        act = this;
        i = getIntent();
        if (i.getStringExtra("phone").length() == 0)
        {
            Intent goback = new Intent(getApplicationContext(),Signin.class);
            finish();
            startActivity(goback);
        }
        phone = i.getStringExtra("phone");

        code = (SigninEdittext)findViewById(R.id.app_code);
        approve = (Button)findViewById(R.id.app_btn);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (new CheckInternet().check()) {
                        Network net = new Network(act);
                        net.execute("http://api.yarbox.co/api/v1/account/verify", "" +
                                "{\n" +
                                "  \"verifyCode\": \"" + code.getText().toString() + "\",\n" +
                                "  \"phoneNumber\": \"" + phone + "\"\n" +
                                "}");
                        final ProgressDialog prog = new ProgressDialog(act);
                        prog.setCancelable(false);
                        prog.setTitle("لطفا منتطر بمانید");
                        prog.show();
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
                                try {
                                    token = new JSONObject(net.mainresponse.toString());
                                    mypref = getSharedPreferences("mypref", MODE_PRIVATE).edit();
                                    mypref.putString("token", token.getString("access_token"));
                                    Getter getprofile = new Getter();
                                    getprofile.execute("http://api.yarbox.co/api/v1/profile", token.getString("access_token"));
                                    getprofile.get();

                                    JSONObject info = new JSONObject(getprofile.mainresponse.toString());
                                    mypref.putString("name", info.getString("firstName"));
                                    mypref.putString("last", info.getString("lastName"));
                                    mypref.putString("phone", phone);
                                    mypref.commit();
                                    prog.dismiss();
                                    Intent gotomain = new Intent(getApplicationContext(), MainActivity.class);
                                    finish();
                                    startActivity(gotomain);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        new MyAlert(act, "خطا!", "اتصال خود را چک کنید!");
                    }

            }
        });
        resend = (TextView)findViewById(R.id.app_resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i.getStringExtra("phone").length() == 11)
                {
                    try {
                        if (new CheckInternet().check()) {
                            final Network net = new Network(getApplicationContext());
                            net.execute("http://api.yarbox.co/api/v1/account/retry-verify", "{\n" +
                                    "  \"phoneNumber\": \"" + i.getStringExtra("phone") + "\"\n" +
                                    "}");

                            net.get();
                            Toast.makeText(getApplicationContext(),"کد ارسال شد",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"لطفا اتصال خود را به اینترنت چک کنید",Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }else{
                    new MyAlert(Approve.this,"شماره تلفن","شماره تلفن وارد شده صحیح نیست!");
                }
            }
        });

    }
    public void onBackPressed(){
        super.onBackPressed();
        Intent goto_signin = new Intent(getApplicationContext(), Signin.class);
        startActivity(goto_signin);
        finish();
    }
}
