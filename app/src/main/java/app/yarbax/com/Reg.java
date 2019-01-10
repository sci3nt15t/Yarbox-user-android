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

import app.yarbax.com.Utilities.Network;


public class Reg extends AppCompatActivity {

    String name;
    String last;
    String phone;
    Activity act;
    Executor exec = Executors.newFixedThreadPool(2);
    boolean isruleaccepted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
        this.act = this;
        final EditText edit_name = (EditText)findViewById(R.id.reg_name);
        final EditText edit_last = (EditText)findViewById(R.id.reg_last);
        final EditText edit_phone = (EditText)findViewById(R.id.reg_phone);
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
                                    progress.dismiss();
                                    Intent goto_login = new Intent(getApplicationContext(), Approve.class);
                                    goto_login.putExtra("phone", phone);
                                    startActivity(goto_login);
                                    finish();
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"لطفا تمامی فیلد هارا پر نمایید", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
