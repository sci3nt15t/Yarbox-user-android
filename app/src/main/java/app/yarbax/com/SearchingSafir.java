package app.yarbax.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import app.yarbax.com.Utilities.GeneralPoster;
import app.yarbax.com.Utilities.Getter;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class SearchingSafir extends AppCompatActivity {

    Button cancel;
    String token;
    int id;
    Handler timer;
    Intent i;

    Timer driver_timer;
    TimerTask checkdriver;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    protected void onCreate(Bundle savedinstance)
    {
        super.onCreate(savedinstance);
        setContentView(R.layout.searchingsafir);
        SharedPreferences p = getSharedPreferences("mypref",MODE_PRIVATE);
        token = p.getString("token","");
        i = getIntent();
        id = i.getIntExtra("id",0);
        cancel = (Button)findViewById(R.id.safir_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Getter cancel_pack = new Getter();
                cancel_pack.execute("http://api.yarbox.co/api/v1/packs/cancelPack?id="+id,token);
                try {
                    cancel_pack.get();
                    Intent goto_main = new Intent(getApplicationContext(),MainActivity.class);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    checkdriver.cancel();
                    timer = null;
                    startActivity(goto_main);
                    finish();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Getter cancel_pack = new Getter();
                cancel_pack.execute("http://api.yarbox.co/api/v1/packs/cancelPack?id="+id,token);
                try {
                    cancel_pack.get();
                    Intent goto_main = new Intent(getApplicationContext(),MainActivity.class);
                    goto_main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goto_main);
                    finish();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },1000*60*5);
        driver_timer = new Timer();
        checkdriver = new TimerTask() {
            @Override
            public void run() {
                GeneralPoster get_driver = new GeneralPoster();
                try {
                    get_driver.execute("http://api.yarbox.co/api/v1/driver/AcceptedCarrierInfo?postPackId="+id,"postPackId="+id);
                    get_driver.get();
                    JSONObject driver_info = new JSONObject(get_driver.mainresponse);
                    if (driver_info.getInt("id") > 0)
                    {
                        Intent goto_driver_info = new Intent(getApplicationContext(),DriverInfo.class);
                        goto_driver_info.putExtra("info",get_driver.mainresponse);
                        goto_driver_info.putExtra("id",id);
                        goto_driver_info.putExtra("iscash",i.getBooleanExtra("iscash",false));
                        goto_driver_info.putExtra("payorigin",i.getBooleanExtra("payorigin",false));
                        checkdriver.cancel();
                        timer = null;
                        startActivity(goto_driver_info);
                        finish();
                    }
                }  catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        driver_timer.schedule(checkdriver,0,1000*10);

    }
}
