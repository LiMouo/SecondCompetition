package limou.com.EnvironCatalog;


import com.google.gson.annotations.SerializedName;

public class EnvironGson {

    @SerializedName("pm2.5")
    private int _$Pm2526; // FIXME check this code
    private int co2;
    private int LightIntensity;
    private int humidity;
    private int temperature;
    private String RESULT;
    private String ERRMSG;
    /**
     * Status : 1
     */

    private int Status;

    public int get_$Pm2526() {
        return _$Pm2526;
    }

    public void set_$Pm2526(int _$Pm2526) {
        this._$Pm2526 = _$Pm2526;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getLightIntensity() {
        return LightIntensity;
    }

    public void setLightIntensity(int LightIntensity) {
        this.LightIntensity = LightIntensity;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getRESULT() {
        return RESULT;
    }

    public void setRESULT(String RESULT) {
        this.RESULT = RESULT;
    }

    public String getERRMSG() {
        return ERRMSG;
    }

    public void setERRMSG(String ERRMSG) {
        this.ERRMSG = ERRMSG;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
}
