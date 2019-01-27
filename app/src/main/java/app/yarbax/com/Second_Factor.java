package app.yarbax.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import app.yarbax.com.MyViews.GrayBorderEditText;

/**
 * Created by shayanrhm on 1/23/19.
 */

public class Second_Factor extends AppCompatActivity {

    JSONObject json;

    public void goback()
    {
        finish();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_factor);

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
        toolbar_title.setText("فاکتور مرسوله");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        String data = i.getStringExtra("detail");
        SharedPreferences pref = getSharedPreferences("mypref",MODE_PRIVATE);

        try {
            json = new JSONArray(data).getJSONObject(0);


            GrayBorderEditText sender_name = (GrayBorderEditText)findViewById(R.id.second_send_name);
            sender_name.setText(pref.getString("name","") + " " +pref.getString("last",""));

            GrayBorderEditText sender_phone = (GrayBorderEditText)findViewById(R.id.second_send_phone);
            sender_phone.setText(json.getJSONObject("origin").getString("senderPhoneNumber"));

            GrayBorderEditText sedenr_address = (GrayBorderEditText)findViewById(R.id.second_send_addr);
            sedenr_address.setText(json.getJSONObject("origin").getString("address"));

            GrayBorderEditText rec_name = (GrayBorderEditText)findViewById(R.id.second_rec_name);
            rec_name.setText(json.getJSONObject("destination").getString("receiverName"));

            GrayBorderEditText rec_phone = (GrayBorderEditText)findViewById(R.id.second_rec_phone);
            rec_phone.setText(json.getJSONObject("destination").getString("receiverPhoneNumber"));

            GrayBorderEditText rec_address = (GrayBorderEditText)findViewById(R.id.second_rec_addr);
            rec_address.setText(json.getJSONObject("destination").getString("address"));

            ImageView vehicle = (ImageView)findViewById(R.id.second_vehicle_type);
            switch (json.getJSONObject("vehicle").getInt("id"))
            {
                case 1:
                    vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.bikecolor));
                    break;
                case 2:
                    vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.carcolor));
                    break;
                case 3:
                    vehicle.setImageDrawable(getResources().getDrawable(R.mipmap.vanetcolor));
                    break;
            }

            TextView count = (TextView)findViewById(R.id.second_count);
            count.setText(json.getInt("count")+"");

            TextView explain = (TextView)findViewById(R.id.second_explain);
            explain.setText(json.getJSONObject("origin").getString("explain"));

            TextView weight = (TextView)findViewById(R.id.second_weight);
            weight.setText(json.getJSONObject("weight").getString("description"));


            TextView packing = (TextView)findViewById(R.id.second_packing);
            if (json.getBoolean("isPacking"))
                packing.setText("دارد");
            else
                packing.setText("ندارد");

            TextView insurance = (TextView)findViewById(R.id.second_insurance);
            insurance.setText(json.getInt("insurancePrice") + "تومان ");

            TextView payment_type = (TextView)findViewById(R.id.second_payment);
            boolean iscash = json.getBoolean("isCashPayment");
            boolean payorigin = json.getBoolean("payAtOrigin");
            if (iscash && payorigin)
            {
                payment_type.setText("انلاین");
            }
            if (!iscash && !payorigin)
            {
                payment_type.setText("مقصد");
            }
            if (!iscash && payorigin )
            {
                payment_type.setText("مبدا");
            }

            TextView price = (TextView)findViewById(R.id.second_price);
            price.setText(json.getInt("price")+"  تومان ");
            

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
