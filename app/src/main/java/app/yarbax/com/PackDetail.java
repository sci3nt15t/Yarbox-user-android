package app.yarbax.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.suke.widget.SwitchButton;

import app.yarbax.com.MyViews.GrayEditText;
import app.yarbax.com.MyViews.MyAlert;
import app.yarbax.com.Utilities.Getter;
import app.yarbax.com.Utilities.extension;

/**
 * Created by shayanrhm on 12/30/18.
 */

public class PackDetail extends AppCompatActivity implements Serializable {

    class weight{
        public int id;
        public String name;

        public weight(int id,String name){
            this.id = id;
            this.name = name;
        }
    }
    PostPack newpack;
    JSONObject weights;
    Executor exec = Executors.newFixedThreadPool(1);
    Spinner weights_spinner;
    ArrayAdapter<String> dataAdapter;
    List<weight> weights_list = new ArrayList<weight>();
    List<String> weight_names = new ArrayList<String>();
    SwitchButton ispacking;
    SwitchButton insurance;
    GrayEditText packprice;
    GrayEditText explain;
    GrayEditText count;
    Button ok;
    Activity act;
    int money = 0;
    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.packdetail);

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
        toolbar_title.setText("جزییات مرسوله");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        act = this;
        newpack = (PostPack)i.getSerializableExtra("newpack");
        weights_spinner = (Spinner)findViewById(R.id.weights);
        count = (GrayEditText)findViewById(R.id.count);
        ispacking = (SwitchButton)findViewById(R.id.ispacking);
        insurance = (SwitchButton)findViewById(R.id.isinsurance);
        packprice = (GrayEditText)findViewById(R.id.packprice);
        explain = (GrayEditText)findViewById(R.id.explain);
        ok = (Button)findViewById(R.id.packdetail_ok);
        newpack.count = 0;
        newpack.isPacking = false;
        newpack.isInsurance = true;
        newpack.insuranceValueId = 0;
        newpack.content = "ندارد";
        newpack.weightId = 0;
        newpack.typeId = 999;
        newpack.insuranceValueId = 1;
        ispacking.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    newpack.isPacking = true;
                    new MyAlert(act,"توجه!","بسته بندی شامل هزینه میباشد");
                }
                else
                    newpack.isPacking = false;
            }
        });
        packprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                packprice.removeTextChangedListener(this);
                if (packprice.getText().length() > 0) {
                    money = Integer.parseInt(packprice.getText().toString().replace(" تومان ", "").replace(",", ""));
                    packprice.setText(String.format("%,.0f", (double) money) + " تومان ");
                }
                packprice.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (packprice.getText().toString().length() > 0)
                    money = Integer.parseInt(packprice.getText().toString().replace(" تومان ", "").replace(",", ""));

            }
        });
        packprice.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL)
                {
                    packprice.setText("");
                }
                return false;
            }
        });
        insurance.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    newpack.isInsurance = true;
                    packprice.setVisibility(View.VISIBLE);
                }
                else{
                    newpack.isInsurance = false;
                    packprice.setText("");
                    money = 0;
                    packprice.setVisibility(View.INVISIBLE);
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count.getText().length() > 0)
                {
                    if (newpack.isInsurance && packprice.getText().toString().length() == 0)
                    {
                        MyAlert alert = new MyAlert(act,"خطا!","تمامی فیلد هارا پر کنید");
                    }
                    else{
                        if (explain.getText().length() > 0) {
                            newpack.content = explain.getText().toString();
                        }
                        if (packprice.getText().toString().length() > 0)
                        newpack.insurancePrice = money;
                        newpack.origin.explain = "ندارد";
                        newpack.count = Integer.parseInt(new extension().ReplaceArabicDigitsWithEnglish(count.getText().toString()));
                        if (newpack.weightId == 999)
                        {
                            new MyAlert(act,"خطا!","وزن بسته را انتخاب نمایید");
                        }else {
                            Intent goto_senderaddress = new Intent(getApplicationContext(), SenderAddress.class);
                            goto_senderaddress.putExtra("newpack", newpack);
                            startActivity(goto_senderaddress);
                            finish();
                        }
                    }
                }else{
                    MyAlert alert = new MyAlert(act,"خطا!","تمامی فیلد هارا پر کنید");
                }
            }
        });
        weights_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newpack.weightId = weights_list.get(i).id;
                newpack.typeId = weights_list.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fetch_weights();
    }

    public void setup_weights()
    {
        try {
            weights_list.add(new weight(999,"به ازای هر بسته (کیلوگرم)"));
            weight_names.add("به ازای هر بسته (کیلوگرم)");
            JSONArray arr = weights.getJSONArray("items");
            for (int i =0;i<arr.length();i++)
            {
                int id = arr.getJSONObject(i).getInt("id");
                String name = arr.getJSONObject(i).getString("description");
                weights_list.add(new weight(id,name));
                weight_names.add(name);
            }
            dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.spinner_item,weight_names);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            weights_spinner.setAdapter(dataAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void fetch_weights(){
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
        exec.execute(new Runnable() {
            @Override
            public void run() {
                Getter get_weights = new Getter();
                get_weights.execute("http://api.yarbox.co/api/v1/pack-data/weights");
                try {
                    get_weights.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println(get_weights.mainresponse);
                    weights = new JSONObject(get_weights.mainresponse);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setup_weights();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    prog.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent goto_selectsize = new Intent(this,SelectSize.class);
        goto_selectsize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goto_selectsize);
        finish();
    }
    public void goback(){
        Intent goto_selectsize = new Intent(this,SelectSize.class);
        goto_selectsize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goto_selectsize);
        finish();
    }
}
