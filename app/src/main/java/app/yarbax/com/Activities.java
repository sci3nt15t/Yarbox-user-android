package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class Activities extends AppCompatActivity {


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
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activities);
        mypref = getSharedPreferences("mypref",MODE_PRIVATE);
        root = (LinearLayout)findViewById(R.id.activity_root);
        act = this;
        token = mypref.getString("token", "");
    }
    public void fetch(){
        final Getter getactivities = new Getter();
            if (new CheckInternet().check())
            {
                final ProgressDialog prog = new ProgressDialog(act);
                prog.setCancelable(false);
                prog.setTitle("لطفا منتطر بمانید");
                if (prog.isShowing())
                    prog.dismiss();
                prog.show();
                exec = Executors.newFixedThreadPool(2);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        getactivities.execute("http://api.yarbox.co/api/v1/packs/arrived",token);
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
                        if (prog.isShowing())
                            prog.dismiss();

                        getactivities.cancel(true);

                    }
                });
                exec = null;

            }else{
                new MyAlert(act,"خطا!","دسترسی خود را با اینترنت چک کنید!");
            }
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
    public void setupcells(JSONArray json){
        root.removeAllViewsInLayout();
        ImageView no = new ImageView(act);
        LinearLayout.LayoutParams no_param = new LinearLayout.LayoutParams(width/2,width/2);
        no_param.gravity = Gravity.CENTER;
        no_param.setMargins(0,150,0,0);
        no.setLayoutParams(no_param);
        no.setImageDrawable(getResources().getDrawable(R.mipmap.no_arrive));
        root.addView(no);

        LinearLayout.LayoutParams no_text_param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView no_text = new TextView(act);
        no_text.setGravity(Gravity.CENTER);
        no_text.setTextSize(24);
        no_text.setLayoutParams(no_text_param);
        no_text.setText("فعالیتی برای نمایش وجود ندارد!");
        root.addView(no_text);
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
                    name_image.setImageDrawable(getResources().getDrawable(R.mipmap.packcode));
                    namedate.addView(name_image);
                    TextView name_text = new TextView(act);
                    name_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                    name_text.setGravity(Gravity.RIGHT);
                    name_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    name_text.setText("کد مرسوله: " + json.getJSONObject(i).getInt("id")  );
                    namedate.addView(name_text);

                    ImageView date_image = new ImageView(act);
                    date_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    date_image.setImageDrawable(getResources().getDrawable(R.mipmap.date));
                    namedate.addView(date_image);
                    TextView date_text = new TextView(act);
                    date_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                    date_text.setGravity(Gravity.RIGHT);
                    date_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    Date itemDate = new Date((long) (Double.parseDouble(json.getJSONObject(i).getString("createdOn")) * 1000));
                    String myDateStr = new SimpleDateFormat("yyyy-MM-dd").format(itemDate);
                    DateConverter converter = new DateConverter();
                    converter.gregorianToPersian(Integer.parseInt(myDateStr.substring(0, 4)), Integer.parseInt(myDateStr.substring(5, 7)), Integer.parseInt(myDateStr.substring(8, 10)));
                    String completedate = converter.getYear() + "/" + converter.getMonth() + "/" + converter.getDay();
                    date_text.setText("تاریخ: " + completedate);
                    namedate.addView(date_text);

                    pack.addView(namedate);
                    //End of first row(Name Date)

                    //line static e mabda
                    LinearLayout origin = new LinearLayout(act);
                    LinearLayout.LayoutParams originparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    originparam.setMargins(0, 0, 0, 50);
                    origin.setLayoutParams(namedateparam);
                    origin.setOrientation(LinearLayout.HORIZONTAL);
                    origin.setPadding(10, 30, 0, 0);
                    origin.setWeightSum(10);

                    ImageView origin_image = new ImageView(act);
                    origin_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    origin_image.setImageDrawable(getResources().getDrawable(R.mipmap.address_image));
                    origin.addView(origin_image);

                    TextView origin_text = new TextView(act);
                    origin_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                    origin_text.setGravity(Gravity.RIGHT);
                    origin_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    origin_text.setText("ادرس مبدا: " );
                    origin.addView(origin_text);

                    pack.addView(origin);
                    //line e static e mabda

                    //address e origin
                    TextView origin_address = new TextView(act);
                    origin_address.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    origin_address.setGravity(Gravity.RIGHT);
                    origin_address.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    origin_address.setText(json.getJSONObject(i).getString("origin") );

                    pack.addView(origin_address);
                    //address e origin


                    //line static e maghsad
                    LinearLayout dest = new LinearLayout(act);
                    LinearLayout.LayoutParams destparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    destparam.setMargins(0, 0, 0, 50);
                    dest.setLayoutParams(namedateparam);
                    dest.setOrientation(LinearLayout.HORIZONTAL);
                    dest.setPadding(10, 30, 0, 0);
                    dest.setWeightSum(10);

                    ImageView dest_image = new ImageView(act);
                    dest_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    dest_image.setImageDrawable(getResources().getDrawable(R.mipmap.address_image));
                    dest.addView(dest_image);

                    TextView dest_text = new TextView(act);
                    dest_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                    dest_text.setGravity(Gravity.RIGHT);
                    dest_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    dest_text.setText("ادرس مقصد: " );
                    dest.addView(dest_text);

                    pack.addView(dest);
                    //line e static e maghsad

                    //address e origin
                    TextView dest_address = new TextView(act);
                    dest_address.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    dest_address.setGravity(Gravity.RIGHT);
                    dest_address.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    dest_address.setText(json.getJSONObject(i).getString("destination") );

                    pack.addView(dest_address);
                    //address e origin

                    //line static e mabda
                    LinearLayout price = new LinearLayout(act);
                    LinearLayout.LayoutParams priceparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    priceparam.setMargins(0, 0, 0, 50);
                    price.setLayoutParams(namedateparam);
                    price.setOrientation(LinearLayout.HORIZONTAL);
                    price.setPadding(30, 30, 0, 0);
                    price.setWeightSum(10);

                    ImageView price_image = new ImageView(act);
                    price_image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                    price_image.setImageDrawable(getResources().getDrawable(R.mipmap.price));
                    price.addView(price_image);

                    TextView price_text = new TextView(act);
                    price_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                    price_text.setGravity(Gravity.RIGHT);
                    price_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    price_text.setText("مبلغ: " + json.getJSONObject(i).getInt("price") );
                    price.addView(price_text);

                    pack.addView(price);
                    //line e static e mabda

                    root.addView(pack);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{

        }
    }
}
