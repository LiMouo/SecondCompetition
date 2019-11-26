package limou.com.NetworkHome;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpJson {
    private static final String TAG = "OkHttpJson";
    public static String jsonData;

    /**
     *
     * @param url 网络路径
     * @param mediaType 请求头尾
     * @param json JSON数据
     */

    public static void SendOKHttp(String url, MediaType mediaType, JSONObject json) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(mediaType,json.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try{
            Response response = client.newCall(request).execute();
            jsonData = response.body().string();
            Log.e(TAG, "HSDHJSHDS "+ jsonData );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return 进行判断如果返回的 JSON 数据不为空，进行赋值刷新
     */
    public static JSONObject JsonObjectRead() {
        JSONObject jsonObject = null;
        try {
            if (jsonData != null){
                jsonObject = new JSONObject(jsonData);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }


    /*RequestBody requestBody = RequestBody.create(mediaType,json.toString());
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    String ResponseData = response.body().string();
                    JSONObject json1 = new JSONObject(ResponseData);
                    money = json1.getString("Balance");
                    show_moneyText(money);*/
}
