package app.yarbax.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
    @Override
    protected void onCreate(Bundle savedinstace)
    {
        super.onCreate(savedinstace);
        setContentView(R.layout.packdetail);
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
                        newpack.insurancePrice = Integer.parseInt(packprice.getText().toString());
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
        final ProgressDialog prog = new ProgressDialog(this);
        prog.setCancelable(false);
        prog.setTitle("لطفا منتطر بمانید");
        if (prog.isShowing())
            prog.dismiss();
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
                if (prog.isShowing())
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
}
