package me.sanbo.usm;

import org.json.JSONObject;

public class Usm {

    public long closeTime;
    public long openTime;
    public String pkgName;
    public String appName;
    public String versionCode;

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("ACT", closeTime);
            jsonObject.putOpt("AOT", openTime);
            jsonObject.putOpt("APN", pkgName);
            jsonObject.putOpt("AN", appName);
            jsonObject.putOpt("AVC", versionCode);

            Logs.i("|" + appName + "|" + pkgName + "|" + versionCode + "|" + openTime + "|" + closeTime);
        } catch (Throwable igone) {
            Logs.e(igone);
        }
        return jsonObject;
    }
}
