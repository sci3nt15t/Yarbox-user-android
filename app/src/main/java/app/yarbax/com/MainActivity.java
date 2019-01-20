package app.yarbax.com;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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

import app.yarbax.com.MyViews.CircularImageView;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.CheckInternet;
import app.yarbax.com.Utilities.DateConverter;
import app.yarbax.com.Utilities.Getter;
import app.yarbax.com.Utilities.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences mypref;
    String marsuleha;
    JSONArray marsuleha_json;
    String token;
    Executor exec = null;
    Activity act;
    LinearLayout root;
    Button running = null;
    Button canceled = null;
    int score;
    int credit;
    View navheader;
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

                if (new CheckInternet().check()){
                    fetch_marsuleha();
                    get_profile();
                    checkforupdate();
                }else{
                    new MyAlert(act,"خطا!","دسترسی خود را با اینترنت چک کنید!");
                }
            }
        });
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mypref = getSharedPreferences("mypref",MODE_PRIVATE);
        if (mypref.getString("token","").length() == 0)
        {
            Intent gotosign = new Intent(getApplicationContext(),Signin.class);
            finish();
            startActivity(gotosign);
        }else {
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
            token = mypref.getString("token", "");
            act = this;
            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            final NavigationView nav = (NavigationView) drawer.findViewById(R.id.nav_view);
            navheader = nav.getHeaderView(0);
            nav.setItemIconTintList(null);
            final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
            Button goto_newpack = (Button) findViewById(R.id.newpack);
            goto_newpack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goto_newpack = new Intent(getApplicationContext(), SelectSize.class);
                    startActivity(goto_newpack);
                    finish();
                }
            });
            root = (LinearLayout) findViewById(R.id.main_root);
            running = (Button) findViewById(R.id.main_proc_btn);
            canceled = (Button) findViewById(R.id.main_inproc_btn);
            running.setBackgroundColor(Color.parseColor("#FF4081"));
            canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fetch_marsuleha();
                    get_profile();
                    swipe.setRefreshing(false);
                }
            });
            running.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    running.setBackgroundColor(Color.parseColor("#FF4081"));
                    canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                    running.setTextColor(Color.WHITE);
                    canceled.setTextColor(Color.BLACK);
                    setupcells(marsuleha_json, false);
                }
            });
            canceled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    canceled.setBackgroundColor(Color.parseColor("#FF4081"));
                    running.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                    running.setTextColor(Color.BLACK);
                    canceled.setTextColor(Color.WHITE);
                    setupcells(marsuleha_json, true);
                }
            });
            nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    switch (id)
                    {
                        case R.id.credit:
                            Intent goto_credit = new Intent(getApplicationContext(),IncreaseCredit.class);
                            startActivity(goto_credit);
                            break;
                        case R.id.reports:
                            Intent goto_report = new Intent(getApplicationContext(),Reports.class);
                            startActivity(goto_report);
                            break;
                        case R.id.activities:
                            Intent goto_activities = new Intent(getApplicationContext(),Activities.class);
                            startActivity(goto_activities);
                            break;
                        case R.id.inbox:
                            Intent goto_inbox = new Intent(getApplicationContext(),Inbox.class);
                            startActivity(goto_inbox);
                            break;
                        case R.id.share:
                            Intent goto_share = new Intent(getApplicationContext(),Share.class);
                            startActivity(goto_share);
                            break;
                        case R.id.about:
                            Intent goto_about = new Intent(getApplicationContext(),AboutUs.class);
                            startActivity(goto_about);
                            break;
                        case R.id.support:
                            Intent goto_support = new Intent(getApplicationContext(),Support.class);
                            startActivity(goto_support);
                            break;
                        case R.id.logout:
                            Intent goto_login = new Intent(getApplicationContext(), Signin.class);
                            SharedPreferences.Editor mypref = getSharedPreferences("mypref",MODE_PRIVATE).edit();
                            mypref.putString("token","");
                            mypref.commit();
                            goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(goto_login);
                            finish();
                            break;
                    }
                    return false;
                }
            });
            drawer.setVisibility(View.GONE);
            drawer.setClickable(false);
            drawer.setFocusable(false);
            ImageView menubtn = (ImageView) findViewById(R.id.menubtn);
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                    drawer.setVisibility(View.VISIBLE);
                    drawer.setClickable(true);
                    drawer.setFocusable(true);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    drawer.setVisibility(View.GONE);
                    drawer.setClickable(false);
                    drawer.setFocusable(false);
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
            menubtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.setVisibility(View.GONE);
                        drawer.setClickable(false);
                        drawer.setFocusable(false);
                        drawer.closeDrawer(Gravity.RIGHT);
                    } else {
                        drawer.setVisibility(View.VISIBLE);
                        drawer.setClickable(true);
                        drawer.setFocusable(true);
                        drawer.openDrawer(Gravity.RIGHT);
                    }
                }
            });
        }
    }
    public void checkforupdate(){
        exec = Executors.newFixedThreadPool(2);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter update = new Getter();
                update.execute("https://yarbox.co/androidv.txt");
                try {
                    update.get();
                    if (update.mainresponse.length() > 0)
                    {
                        PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        int new_version = Integer.parseInt(update.mainresponse);
                        System.out.println("new version: "+new_version);
                        int current_version = (int) (Float.parseFloat(pinfo.versionName) * 10);
                        System.out.println("current version: "+current_version);
                        if (new_version > current_version){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new MyAlert(act,"بروز رسانی","برای دسترسی به اخرین امکانات برنامه و همچنین رفع ایرادات نسخه فعلی، نسخه جدید یارباکس را از کافه بازار یا بصورت مستقیم از سایت یار باکس دانلود نمایید");
                                }
                            });
                        }
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void get_profile(){
        exec = Executors.newFixedThreadPool(2);
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter get_score = new Getter();
                get_score.execute("http://api.yarbox.co/api/v1/account/check",token);
                try {
                    get_score.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject j = new JSONObject(get_score.mainresponse);
                    score = j.getInt("score");
                    credit = j.getInt("credit");
                    SharedPreferences.Editor p = getSharedPreferences("mypref",MODE_PRIVATE).edit();
                    p.putInt("score",score);
                    p.putInt("credit",credit);
                    p.commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView name = (TextView)navheader.findViewById(R.id.header_name);
                            TextView head_score = (TextView)navheader.findViewById(R.id.header_score);
                            TextView head_cred = (TextView)navheader.findViewById(R.id.header_cred);
                            name.setText(mypref.getString("name",""));
                            head_score.setText("امتیاز : " + score);
                            head_cred.setText("کیف پول : " + credit);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        exec = null;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void fetch_marsuleha(){
        final Getter getmarsuleha = new Getter();
        running.setBackgroundColor(Color.parseColor("#FF4081"));
        canceled.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
        running.setTextColor(Color.WHITE);
        canceled.setTextColor(Color.BLACK);
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
                        getmarsuleha.execute("http://api.yarbox.co/api/v1/packs/running",token);
                        try {
                            getmarsuleha.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        marsuleha = getmarsuleha.mainresponse.toString();
                        try {
                            marsuleha_json = new JSONObject(marsuleha).getJSONArray("items");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setupcells(marsuleha_json,false);
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
                            Intent goto_login = new Intent(getApplicationContext(), Signin.class);
                            SharedPreferences.Editor mypref = getSharedPreferences("mypref",MODE_PRIVATE).edit();
                            mypref.putString("token","");
                            mypref.commit();
                            goto_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(goto_login);
                            finish();
                        }
                        if (prog.isShowing())
                            prog.dismiss();

                        getmarsuleha.cancel(true);

                    }
                });
                exec = null;
            }else{
                new MyAlert(act,"خطا!","دسترسی خود را با اینترنت چک کنید!");
            }
    }
    public void setupcells(JSONArray data, boolean filter)
    {
        root.removeAllViewsInLayout();
        ImageView no = new ImageView(act);
        LinearLayout.LayoutParams no_param = new LinearLayout.LayoutParams(width/2,width/2);
        no_param.gravity = Gravity.CENTER;
        no.setLayoutParams(no_param);
        no_param.setMargins(0,150,0,0);
        no.setImageDrawable(getResources().getDrawable(R.mipmap.no_activity));
        root.addView(no);

        LinearLayout.LayoutParams no_text_param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView no_text = new TextView(act);
        no_text.setGravity(Gravity.CENTER);
        no_text.setTextSize(24);
        no_text.setLayoutParams(no_text_param);
        if (!filter)
            no_text.setText("مرسوله ای برای پیگیری وجود ندارد!");
        else
            no_text.setText("مرسوله ی ناموفقی وجود ندارد!");
        root.addView(no_text);
        if (data.length() > 0) {
            root.removeAllViewsInLayout();
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getJSONObject(i).getBoolean("isCanceled") == filter) {

                        LinearLayout horiz = new LinearLayout(act);
                        LinearLayout.LayoutParams horizparam = new LinearLayout.LayoutParams(width+400, ViewGroup.LayoutParams.WRAP_CONTENT);
                        horizparam.setMargins(20, 20, 20, 80);
                        horiz.setLayoutParams(horizparam);
                        horiz.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout pack = new LinearLayout(act);
                        LinearLayout.LayoutParams packparam = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        packparam.setMargins(60,0,60,0);
                        pack.setLayoutParams(packparam);
                        pack.setOrientation(LinearLayout.VERTICAL);
                        pack.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_packbox_curve));
                        pack.setPadding(0, 0, 0, 60);

                        //Start of first row (name Date)
                        LinearLayout namedate = new LinearLayout(act);
                        LinearLayout.LayoutParams namedateparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        namedateparam.setMargins(0, 0, 0, 50);
                        namedate.setLayoutParams(namedateparam);
                        namedate.setOrientation(LinearLayout.HORIZONTAL);
                        namedate.setPadding(30, 30, 30, 0);
                        namedate.setWeightSum(10);
                        TextView name_text = new TextView(act);
                        name_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 5f));
                        name_text.setGravity(Gravity.RIGHT);
                        name_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        name_text.setText(data.getJSONObject(i).getJSONObject("destination").getString("receiverName"));
                        namedate.addView(name_text);
                        TextView date_text = new TextView(act);
                        date_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 5f));
                        date_text.setGravity(Gravity.RIGHT);
                        date_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        Date itemDate = new Date((long) (Double.parseDouble(data.getJSONObject(i).getString("createdOn")) * 1000));
                        String myDateStr = new SimpleDateFormat("yyyy-MM-dd").format(itemDate);
                        DateConverter converter = new DateConverter();
                        converter.gregorianToPersian(Integer.parseInt(myDateStr.substring(0, 4)), Integer.parseInt(myDateStr.substring(5, 7)), Integer.parseInt(myDateStr.substring(8, 10)));
                        String completedate = converter.getYear() + "/" + converter.getMonth() + "/" + converter.getDay();
                        date_text.setText(completedate);
                        namedate.addView(date_text);
                        pack.addView(namedate);
                        //End of first row(Name Date)

                        //Start of second row(marsule code)
                        LinearLayout marsulecode_lay = new LinearLayout(act);
                        LinearLayout.LayoutParams marsulecode_layparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        marsulecode_layparam.setMargins(0, 0, 0, 50);
                        marsulecode_lay.setLayoutParams(marsulecode_layparam);
                        marsulecode_lay.setOrientation(LinearLayout.HORIZONTAL);
                        marsulecode_lay.setPadding(30, 0, 30, 0);
                        marsulecode_lay.setWeightSum(10);
                        TextView code_text = new TextView(act);
                        code_text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 7f));
                        code_text.setGravity(Gravity.RIGHT);
                        code_text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        code_text.setText("کد مرسوله : " + data.getJSONObject(i).getString("id"));
                        marsulecode_lay.addView(code_text);
                        pack.addView(marsulecode_lay);
                        //End of second row (marsuleh code)


                        System.out.println(data.getJSONObject(i).getString("status"));
                        //Start of third row(images!)
                        LinearLayout marsulests_lay = new LinearLayout(act);
                        LinearLayout.LayoutParams marsulests_layparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        marsulests_layparam.setMargins(0, 0, 0, 50);
                        marsulests_lay.setLayoutParams(marsulests_layparam);
                        marsulests_lay.setOrientation(LinearLayout.HORIZONTAL);
                        marsulests_lay.setGravity(Gravity.CENTER);
                        marsulests_lay.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        marsulests_lay.setPadding(30, 0, 30, 0);
                        marsulests_lay.setWeightSum(8);

                        LinearLayout.LayoutParams images_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);

                        View peyk;
                        LayoutInflater pinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        peyk = pinflate.inflate(R.layout.marsulests, null);
                        peyk.setLayoutParams(images_param);
                        TextView peyk_text = (TextView) peyk.findViewById(R.id.marsulests_text);
                        peyk_text.setText("تحویل به پیک");
                        CircularImageView peyk_image = (CircularImageView) peyk.findViewById(R.id.marsulests_image);
                        peyk_image.setImageDrawable(getResources().getDrawable(R.mipmap.bikegray));
                        if (data.getJSONObject(i).getString("status").contains("driverRecived")) {
                            peyk_image.setImageDrawable(getResources().getDrawable(R.mipmap.bikegreen));
                        }
                        marsulests_lay.addView(peyk);

                        View origin;
                        LayoutInflater oinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        origin = oinflate.inflate(R.layout.marsulests, null);
                        origin.setLayoutParams(images_param);
                        TextView origin_text = (TextView) origin.findViewById(R.id.marsulests_text);
                        origin_text.setText("پورت مبدا");
                        CircularImageView origin_image = (CircularImageView) origin.findViewById(R.id.marsulests_image);
                        origin_image.setImageDrawable(getResources().getDrawable(R.mipmap.origingray));
                        if (data.getJSONObject(i).getString("status").contains("originPort")) {
                            peyk_image.setImageDrawable(getResources().getDrawable(R.mipmap.bikegreen));
                            origin_image.setImageDrawable(getResources().getDrawable(R.mipmap.origingreen));
                        }
                        marsulests_lay.addView(origin);

                        View dest;
                        LayoutInflater dinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        dest = dinflate.inflate(R.layout.marsulests, null);
                        dest.setLayoutParams(images_param);
                        TextView dest_text = (TextView) dest.findViewById(R.id.marsulests_text);
                        dest_text.setText("پورت مقصد");
                        CircularImageView dest_image = (CircularImageView) dest.findViewById(R.id.marsulests_image);
                        dest_image.setImageDrawable(getResources().getDrawable(R.mipmap.destgray));
                        if (data.getJSONObject(i).getString("status").contains("destinationPort")) {
                            peyk_image.setImageDrawable(getResources().getDrawable(R.mipmap.bikegreen));
                            origin_image.setImageDrawable(getResources().getDrawable(R.mipmap.origingreen));
                            dest_image.setImageDrawable(getResources().getDrawable(R.mipmap.portgreen));
                        }
                        marsulests_lay.addView(dest);



                        View reciever;
                        LayoutInflater rinflate = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        reciever = rinflate.inflate(R.layout.marsulests, null);
                        reciever.setLayoutParams(images_param);
                        TextView reciever_text = (TextView) reciever.findViewById(R.id.marsulests_text);
                        reciever_text.setText("گیرنده");
                        CircularImageView reciever_image = (CircularImageView) reciever.findViewById(R.id.marsulests_image);
                        reciever_image.setImageDrawable(getResources().getDrawable(R.mipmap.destgray));
                        if (data.getJSONObject(i).getString("status").contains("delivered")) {
                            peyk_image.setImageDrawable(getResources().getDrawable(R.mipmap.bikegreen));
                            origin_image.setImageDrawable(getResources().getDrawable(R.mipmap.origingreen));
                            dest_image.setImageDrawable(getResources().getDrawable(R.mipmap.portgreen));
                            reciever_image.setImageDrawable(getResources().getDrawable(R.mipmap.destgreen));
                        }
                        marsulests_lay.addView(reciever);



                        pack.addView(marsulests_lay);
                        //End of Third Row(Images!)
                        if (data.getJSONObject(i).getBoolean("isCanceled") == true)
                        {
                            LinearLayout.LayoutParams btn_param = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
                            final int finalI = i;

                            Button reorder = new Button(this);
                            reorder.setLayoutParams(btn_param);
                            reorder.setBackgroundColor(Color.parseColor("#4BAE45"));
                            reorder.setText("سفارش مجدد");
                            horiz.addView(reorder);
                            reorder.setTextColor(Color.WHITE);
                            reorder.setX(200);
                            reorder.setAlpha(0);
                            reorder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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
                                            Getter reorder_pack = new Getter();
                                            try {
                                                reorder_pack.execute("http://api.yarbox.co/api/v1/packs/alopeyk/"+data.getJSONObject(finalI).getString("id")+"/re-order",token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                reorder_pack.get();
                                                try {
                                                    String factorkey = new JSONObject(reorder_pack.mainresponse).getString("packKey");
                                                    Intent goto_factor = new Intent(getApplicationContext(),NewFactor.class);
                                                    goto_factor.putExtra("key",factorkey);
                                                    goto_factor.putExtra("reorder",true);
                                                    startActivity(goto_factor);
                                                    finish();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (prog.isShowing())
                                                prog.dismiss();
                                        }
                                    });
                                }
                            });

                            Button delete = new Button(this);
                            delete.setLayoutParams(btn_param);
                            delete.setBackgroundColor(Color.RED);
                            delete.setText("حذف");
                            delete.setTextColor(Color.WHITE);
                            horiz.addView(delete);
                            delete.setX(400);
                            delete.setAlpha(0);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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
                                            Getter delete_pack = new Getter();
                                            try {
                                                delete_pack.execute("http://api.yarbox.co/api/v1/packs/remove?id="+data.getJSONObject(finalI).getString("id"),token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                delete_pack.get();
                                                if (prog.isShowing())
                                                    prog.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fetch_marsuleha();
                                                    }
                                                });
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (prog.isShowing())
                                                prog.dismiss();
                                        }
                                    });
                                }
                            });

                            final AnimatorSet mAnimationSet = new AnimatorSet();
                            pack.setOnTouchListener(new OnSwipeTouchListener(this){
                                @Override
                                public void onSwipeLeft() {
                                    if (delete.getAlpha() == 0) {
                                        ObjectAnimator hide = ObjectAnimator.ofFloat(pack, "X", pack.getX(), pack.getX() - 400);
                                        hide.setDuration(200);
                                        delete.setX(delete.getX() - 400);
                                        reorder.setX(reorder.getX() - 200);
                                        delete.setAlpha(1);
                                        reorder.setAlpha(1);
                                        mAnimationSet.play(hide);
                                        mAnimationSet.start();
                                    }
                                    super.onSwipeLeft();
                                }

                                @Override
                                public void onSwipeRight() {
                                    if (delete.getAlpha() == 1) {
                                        ObjectAnimator hide = ObjectAnimator.ofFloat(pack, "X", pack.getX(), pack.getX() + 400);
                                        hide.setDuration(200);
                                        delete.setX(delete.getX() + 400);
                                        reorder.setX(reorder.getX() + 200);
                                        delete.setAlpha(0);
                                        reorder.setAlpha(0);
                                        mAnimationSet.play(hide);
                                        mAnimationSet.start();
                                    }
                                    super.onSwipeRight();
                                }
                            });
                            pack.setX(480);
                        }else{
                            pack.setX(80);
                        }
                        horiz.addView(pack);
                        root.addView(horiz);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
