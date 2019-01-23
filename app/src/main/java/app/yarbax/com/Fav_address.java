package app.yarbax.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

import app.yarbax.com.Utilities.MyDb;

/**
 * Created by shayanrhm on 1/20/19.
 */

public class Fav_address extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    MyDb db = new MyDb();
    Activity act;
    LinearLayout root;
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.fav_address);
        act = this;
        root = (LinearLayout)findViewById(R.id.fav_root);
        Button ok = (Button)findViewById(R.id.fav_add);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionSheet.createBuilder(act,getSupportFragmentManager())
                        .setCancelableOnTouchOutside(true)
                        .setCancelButtonTitle("لفو")
                        .setOtherButtonTitles("مبدا","مقصد")
                        .setListener(Fav_address.this)
                        .show();
            }
        });
        final Button origin = (Button)findViewById(R.id.fav_origin);
        final Button dest = (Button)findViewById(R.id.fav_dest);
        origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupcells("origin");
                origin.setBackgroundColor(Color.parseColor("#FF4081"));
                dest.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                origin.setTextColor(Color.WHITE);
                dest.setTextColor(Color.BLACK);
            }
        });
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupcells("dest");
                dest.setBackgroundColor(Color.parseColor("#FF4081"));
                origin.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
                dest.setTextColor(Color.WHITE);
                origin.setTextColor(Color.BLACK);
            }
        });
        setupcells("origin");
        origin.setBackgroundColor(Color.parseColor("#FF4081"));
        dest.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_stroke));
        origin.setTextColor(Color.WHITE);
        dest.setTextColor(Color.BLACK);

    }

    public void setupcells(final String location){
        final Cursor crs = db.get("SELECT `title`,`city`,`address`,`number`,`id` FROM `fav` WHERE `location` = '"+location+"' ORDER BY `id` DESC");
        System.out.println(crs.getCount());
        root.removeAllViewsInLayout();
        while (crs.moveToNext())
        {
            LinearLayout pack = new LinearLayout(act);
            LinearLayout.LayoutParams packparam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            packparam.setMargins(60,20,60,0);
            pack.setLayoutParams(packparam);
            pack.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            pack.setOrientation(LinearLayout.VERTICAL);
            pack.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_packbox_curve));
            pack.setPadding(0, 0, 0, 60);

            TextView title = new TextView(act);
            title.setPadding(0,10,30,0);
            title.setText("عنوان : " + crs.getString(0));
            title.setTextColor(Color.BLACK);
            title.setTextSize(20);
            pack.addView(title);

            TextView address = new TextView(act);
            address.setPadding(0,10,30,0);
            address.setTextColor(Color.BLACK);
            address.setTextSize(20);
            address.setTextDirection(View.TEXT_DIRECTION_RTL);
            address.setText("ادرس‌ : " + crs.getString(1) + " - " + crs.getString(2) + " - " + crs.getString(3));
            pack.addView(address);

            final int id = crs.getInt(4);
            pack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (location.contains("dest"))
                    {
                        Intent goto_dest_view = new Intent(getApplicationContext(),Fav_dest_view.class);
                        goto_dest_view.putExtra("id",id);
                        System.out.println(id);
                        startActivity(goto_dest_view);
                        finish();
                    }else{
                        Intent goto_origin_view = new Intent(getApplicationContext(),Fav_origin_view.class);
                        goto_origin_view.putExtra("id",id);
                        startActivity(goto_origin_view);
                        finish();
                    }
                }
            });

            root.addView(pack);
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (index == 0)
        {
            Intent goto_fav_origin_map = new Intent(act,Fav_origin_map.class);
            startActivity(goto_fav_origin_map);
            finish();

        }else{
            Intent goto_fav_dest = new Intent(act,Fav_dest.class);
            startActivity(goto_fav_dest);
            finish();
        }
    }
}
