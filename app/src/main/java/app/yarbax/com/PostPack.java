package app.yarbax.com;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class PostPack implements Serializable {

    public boolean isPacking,isInsurance;
    public Integer typeId,weightId,count,insuranceValueId,vehicleId,insurancePrice;
    public String senderPhoneNumber,content,receiveType;

    public Origin origin;
    public Destination destination;


    class Origin implements Serializable{
        public String explain,senderPhoneNumber,province,city,floor,alley,street,plaque,latitude,longitude;
    }
    class Destination implements Serializable{
        public Integer portId;
        public String receiverPhoneNumber,receiverTelephone,receiverName,explain,province,city,floor,alley,street,plaque,latitude,longitude;
    }

    public PostPack(){
        origin = new Origin();
        destination = new Destination();
    }

    public String post(){
        JSONObject mainbody = new JSONObject();
        JSONObject originbody = new JSONObject();
        JSONObject destbody = new JSONObject();
        Field[] f = this.getClass().getDeclaredFields();
        for (int i=0;i<f.length;i++)
        {
            if (!f[i].getName().contains("origin") )
            {
                if (!f[i].getName().contains("destination") ) {
                    try {
                        mainbody.put(f[i].getName(), f[i].get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        Field[] o = origin.getClass().getDeclaredFields();
        for (int i=0;i<o.length;i++)
        {
            if (!o[i].getName().contains("this$0")) {
                try {
                    originbody.put(o[i].getName(), o[i].get(origin));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        Field[] d = destination.getClass().getDeclaredFields();
        for (int i=0;i<d.length;i++)
        {
            if (!d[i].getName().contains("this$0")) {
                try {
                    destbody.put(d[i].getName(), d[i].get(destination));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            mainbody.put("origin",originbody);
            mainbody.put("destination",destbody);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(mainbody.toString());
        return mainbody.toString();
    }
}
