package limou.com.NetworkHome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*jsonObject 是返回得到的数据*/
public class OkHttpData {
    private static final MediaType JSONType = MediaType.parse("");
    private static final String TAG = "OkHttpData";
    public static String jsonData;
    public static int code;

    public static void sendConnect(String url, String json) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                //.readTimeout(5, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = RequestBody.create(JSONType, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = client.newCall(request).execute();
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static JSONObject JsonObjectRead() {
        JSONObject jsonObject = null;
        try {
            if (jsonData != null) {
                jsonObject = new JSONObject(jsonData);
            }

            //  Log.e(TAG, "" + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static List JsonArrayRead() {
        JSONObject jbData = null;
        List<Map<String, String>> listData = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jb = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                map.put("id", jb.getString("id"));
                map.put("title", jb.getString("title"));
                map.put("url", jb.getString("url"));
                listData.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listData;
    }
}
