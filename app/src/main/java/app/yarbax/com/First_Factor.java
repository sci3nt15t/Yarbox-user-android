package app.yarbax.com;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.yarbax.com.Utilities.DateConverter;

/**
 * Created by shayanrhm on 1/23/19.
 */

public class First_Factor extends AppCompatActivity {


    JSONObject json;
    SharedPreferences pref;
    public void goback(){
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_factor);

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
        toolbar_title.setText("اطلاعات مرسوله");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pref = getSharedPreferences("mypref",MODE_PRIVATE);
        Intent i = getIntent();
        String data = i.getStringExtra("detail");
        TextView status = (TextView)findViewById(R.id.factor_sts);
        try {
            json = new JSONArray(data).getJSONObject(0);

            TextView name = (TextView)findViewById(R.id.factor_name);
            name.setText(pref.getString("name","") + " " +pref.getString("last",""));
            TextView number = (TextView)findViewById(R.id.factor_number);
            number.setText(json.getJSONObject("destination").getString("receiverPhoneNumber"));
            TextView code = (TextView)findViewById(R.id.factor_code);
            code.setText(json.getInt("id")+"");
            Date itemDate = new Date((long) (Double.parseDouble(json.getString("createdOn")) * 1000));
            String myDateStr = new SimpleDateFormat("yyyy-MM-dd").format(itemDate);
            DateConverter converter = new DateConverter();
            converter.gregorianToPersian(Integer.parseInt(myDateStr.substring(0, 4)), Integer.parseInt(myDateStr.substring(5, 7)), Integer.parseInt(myDateStr.substring(8, 10)));
            String completedate = converter.getYear() + "/" + converter.getMonth() + "/" + converter.getDay();
            TextView date = (TextView)findViewById(R.id.factor_date);
            date.setText(completedate);
            TextView time = (TextView)findViewById(R.id.factor_time);
            String myTimestr = new SimpleDateFormat("H:mm").format(itemDate);
            time.setText(myTimestr);

            switch (json.getString("status"))
            {
                case "waiting":
                    status.setText("در حال رسیدن وسیله حمل بار");
                    break;
                case "driverRecived":
                    status.setText("تحویل مرسوله شما به پیک");
                    break;
                case "originPort":
                    status.setText("مرسوله شما به پورت مبدا تحویل داده شد");
                    break;
                case "destinationPort":
                    status.setText("مرسوله شما به پورت مقصد تحویل داده شد");
                    break;
                case "delivered":
                    status.setText( "مرسوله شما به گیرنده تحویل داده شد");
                    break;
                default:
                    status.setText("در حال رسیدن وسیله حمل بار");
                    break;
            }

            Button detail = (Button)findViewById(R.id.factor_detail);
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent second = new Intent(getApplicationContext(),Second_Factor.class);
                    second.putExtra("detail",data);
                    startActivity(second);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
