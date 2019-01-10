package app.yarbax.com.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by shayanrhm on 1/10/19.
 */

public class extension {

    public String ReplaceArabicDigitsWithEnglish(String str)
    {
        String init = str;

        Map<String,String> map = new HashMap<String,String>();
        map.put("۰","0");
        map.put("۱","1");
        map.put("۲","2");
        map.put("۳","3");
        map.put("۴","4");
        map.put("۵","5");
        map.put("۶","6");
        map.put("۷","7");
        map.put("۸","8");
        map.put("۹","9");

        Set keys = map.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String arabic = (String) i.next();
            String english = (String) map.get(arabic);
            init  = init.replace(arabic,english);
        }
        return init;
    }
}
