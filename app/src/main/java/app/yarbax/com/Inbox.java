package app.yarbax.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.CheckInternet;
import app.yarbax.com.Utilities.DateConverter;
import app.yarbax.com.Utilities.Getter;

/**
 * Created by shayanrhm on 1/17/19.
 */

public class Inbox extends AppCompatActivity {


    LinearLayout root;
    SharedPreferences mypref;
    String activites;
    JSONArray activities_json;
    Executor exec = Executors.newFixedThreadPool(2);
    Activity act;
    String token;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    public void goback(){
        finish();
    }
    @Override
    protected void onCreate(Bundle SavedInstace)
    {
        super.onCreate(SavedInstace);
        setContentView(R.layout.inbox);

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
        toolbar_title.setText("صندوق پیام");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mypref = getSharedPreferences("mypref",MODE_PRIVATE);
        root = (LinearLayout)findViewById(R.id.inbox_root);
        act = this;
        token = mypref.getString("token", "");
        fetch();

    }
    int width;
    int height;
    @Override
    public void onStart(){

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Display display = getWindowManager(). getDefaultDisplay();
                Point size = new Point();
                display. getSize(size);
                width = size.x;
                height = size.y;
                fetch();
            }
        });
        super.onStart();
    }
    public void fetch(){
        final Getter getactivities = new Getter();
        if (new CheckInternet().check())
        {
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
                    getactivities.execute("http://api.yarbox.co/api/v1/messages",token);
                    try {
                        getactivities.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activites = getactivities.mainresponse.toString();
                    try {
                        activities_json = new JSONObject(activites).getJSONArray("items");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupcells(activities_json);
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MyAlert(act,"حطا!","سشن شما به پایان رسیده! لطفا مجددا وارد شوید!");
                            }
                        });
                        e.printStackTrace();
                    }
                        prog.dismiss();

                    getactivities.cancel(true);

                }
            });
            exec = null;

        }else{
            new MyAlert(act,"خطا!","دسترسی خود را با اینترنت چک کنید!");
        }
    }
    public void setupcells(JSONArray json){
        root.removeAllViewsInLayout();
        ImageView no = new ImageView(act);
        LinearLayout.LayoutParams no_param = new LinearLayout.LayoutParams(width/2,width/2);
        no_param.setMargins(0,150,0,0);
        no_param.gravity = Gravity.CENTER;
        no.setLayoutParams(no_param);
        no.setImageDrawable(getResources().getDrawable(R.mipmap.no_inbox));
        root.addView(no);

        LinearLayout.LayoutParams no_text_param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView no_text = new TextView(act);
        no_text.setGravity(Gravity.CENTER);
        no_text.setTextSize(24);
        no_text.setLayoutParams(no_text_param);
        no_text.setText("پیغامی برای نمایش وجود ندارد!");
        root.addView(no_text);
        System.out.println(token);
        if (json.length() > 0)
        {
            root.removeAllViewsInLayout();
            for (int i =0;i<json.length();i++)
            {
                try {
                    LinearLayout pack = new LinearLayout(act);
                    LinearLayout.LayoutParams packparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    packparam.setMargins(20, 20, 20, 80);
                    pack.setLayoutParams(packparam);
                    pack.setOrientation(LinearLayout.VERTICAL);
                    pack.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_packbox_curve));
                    pack.setPadding(10, 0, 10, 60);


                    //Start of first row (Code Date)
                    LinearLayout namedate = new LinearLayout(act);
                    LinearLayout.LayoutParams namedateparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    namedateparam.setMargins(0, 0, 0, 50);
                    namedate.setLayoutParams(namedateparam);
                    namedate.setOrientation(LinearLayout.HORIZONTAL);
                    namedate.setPadding(30, 30, 0, 0);
                    namedate.setWeightSum(10);

                    ImageView name_image = new ImageView(act);
                    name_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    name_image.setImageDrawable(getResources().getDrawable(R.mipmap.inbox_msg));
                    namedate.addView(name_image);
                    TextView name_text = new TextView(act);
                    name_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 4f));
                    name_text.setGravity(Gravity.RIGHT);
                    name_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    name_text.setText(json.getJSONObject(i).getString("title")  );
                    name_text.setGravity(Gravity.CENTER);
                    namedate.addView(name_text);

                    ImageView date_image = new ImageView(act);
                    date_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    date_image.setImageDrawable(getResources().getDrawable(R.mipmap.inbox_date));
                    namedate.addView(date_image);
                    TextView date_text = new TextView(act);
                    date_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 4f));
                    date_text.setGravity(Gravity.RIGHT);
                    date_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    Date itemDate = new Date((long) (Double.parseDouble(json.getJSONObject(i).getString("createdOn")) * 1000));
                    String myDateStr = new SimpleDateFormat("yyyy-MM-dd").format(itemDate);
                    DateConverter converter = new DateConverter();
                    converter.gregorianToPersian(Integer.parseInt(myDateStr.substring(0, 4)), Integer.parseInt(myDateStr.substring(5, 7)), Integer.parseInt(myDateStr.substring(8, 10)));
                    String completedate = converter.getYear() + "/" + converter.getMonth() + "/" + converter.getDay();
                    date_text.setText(completedate);
                    date_text.setGravity(Gravity.CENTER);
                    namedate.addView(date_text);

                    pack.addView(namedate);
                    //End of first row(Name Date)


                    View spliter = new View(act);
                    ViewGroup.LayoutParams spliter_param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3);
                    spliter.setLayoutParams(spliter_param);
                    spliter.setBackgroundColor(Color.GRAY);
                    pack.addView(spliter);


                    //address e origin
                    TextView origin_address = new TextView(act);
                    origin_address.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    origin_address.setGravity(Gravity.RIGHT);
                    origin_address.setPadding(30,30,30,0);
                    origin_address.setLineSpacing(10,2);
                    origin_address.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    origin_address.setText(json.getJSONObject(i).getString("message") );

                    pack.addView(origin_address);
                    //address e origin

                    root.addView(pack);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{

        }
    }

}
