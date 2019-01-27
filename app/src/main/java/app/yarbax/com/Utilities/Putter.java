package app.yarbax.com.Utilities;

import android.os.AsyncTask;

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

public class Putter extends AsyncTask<String, String, String> {


    String url;
    String json;
    String token;
    Request request;
    public String mainresponse = "";
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
        if (strings.length > 2) {
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type","application/json")
                    .addHeader("Authorization","bearer "+strings[2])
                    .put(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .addHeader("Content-Type","application/json")
                    .build();
        }
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mainresponse = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mainresponse;
    }
}
