package app.yarbax.com.Utilities;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shayanrhm on 1/1/19.
 */

public class Poster extends AsyncTask<String, String, String> {


    String url;
    String json;
    String token;
    Request request;
    public String factorkey = "";
    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected String doInBackground(String... strings) {
        url = strings[0];
        json = strings[1];
        RequestBody body = RequestBody.create(JSON, json);
        if (strings.length > 2)
            token = strings[2];
        if (token.length() > 1) {
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization","bearer "+token)
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type","application/json")
                    .build();
        }
        Response response = null;
        try {
            response = client.newCall(request).execute();
            try {
                factorkey = new JSONObject(response.body().string()).getString("packKey");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return factorkey;
    }
}
