package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.MyViews.SigninEdittext;
import app.yarbax.com.Utilities.Network;
import app.yarbax.com.Utilities.extension;


public class Reg extends AppCompatActivity {

    String name;
    String last;
    String phone;
    Activity act;
    Executor exec = Executors.newFixedThreadPool(2);
    boolean isruleaccepted = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goto_signin = new Intent(getApplicationContext(),Signin.class);
        startActivity(goto_signin);
        finish();
    }

    public void goback(){
        Intent goto_signin = new Intent(getApplicationContext(),Signin.class);
        startActivity(goto_signin);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);

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
        toolbar_title.setText("ثبت نام");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.act = this;
        final SigninEdittext edit_name = (SigninEdittext)findViewById(R.id.reg_name);
        final SigninEdittext edit_last = (SigninEdittext)findViewById(R.id.reg_last);
        final SigninEdittext edit_phone = (SigninEdittext)findViewById(R.id.reg_phone);
        final Button regbtn = (Button)findViewById(R.id.reg_btn);

        regbtn.setClickable(false);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_name.getText().toString();
                last = edit_last.getText().toString();
                phone = edit_phone.getText().toString();
                if (name.length() > 0 && last.length() > 0 && phone.length() > 0) {
                    final Network net = new Network(act);
                    net.execute("http://api.yarbox.co/api/v1/account/register","{\n" +
                            "  \"phoneNumber\": \""+phone+"\",\n" +
                            "  \"firstName\": \""+name+"\",\n" +
                            "  \"lastName\": \""+last+"\"\n" +
                            "}");
                    final ProgressDialog progress = new ProgressDialog(act);
                    progress.setTitle("لطفا منتظر باشید");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
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
                            System.out.println(net.mainresponse.toString());
                            try {
                                JSONObject issuccess = new JSONObject(net.mainresponse.toString());
                                if (issuccess.getBoolean("isSuccess")) {
                                    Intent goto_login = new Intent(getApplicationContext(), Approve.class);
                                    extension converter = new extension();
                                    String finalphone = converter.ReplaceArabicDigitsWithEnglish(phone)
                                            .replace("+98","0").replaceAll(" ","");
                                    goto_login.putExtra("phone", finalphone);
                                    startActivity(goto_login);
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            new MyAlert(act,"خطا!","این شماره قبلا ثبت نام شده!");
                                        }
                                    });
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                            if(progress.isShowing())
                                progress.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"لطفا تمامی فیلد هارا پر نمایید", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
