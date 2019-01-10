package app.yarbax.com;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
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
                    try {
                        if (new CheckInternet().check()) {
                            final Network net = new Network(getApplicationContext());
                            net.execute("http://api.yarbox.co/api/v1/account/retry-verify", "{\n" +
                                    "  \"phoneNumber\": \"" + phone.getText().toString() + "\"\n" +
                                    "}");
                            final ProgressDialog prog = new ProgressDialog(act);
                            prog.setCancelable(false);
                            prog.setTitle("لطفا منتطر بمانید");
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
                                    prog.dismiss();
                                    Intent i = new Intent(getApplicationContext(),Approve.class);
                                    i.putExtra("phone",phone.getText().toString());
                                    startActivity(i);
                                    finish();
                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(),"لطفا اتصال خود را به اینترنت چک کنید",Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    new MyAlert(Signin.this,"شماره تلفن","شماره تلفن وارد شده صحیح نیست!");
                }
            }
        });
    }
}
