package com.simon.safe.bean;


/**
 * Created by 38640 on 2018/3/20.
 */

public class VersionBean{

    /**
     * downloadUrl : http://www.oox.apk
     * versionCode : 2
     * versionName : 2.0
     * versionDes : 新版本发布啦！
     */

    private String downloadUrl;
    private String versionCode;
    private String versionName;
    private String versionDes;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDes() {
        return versionDes;
    }

    public void setVersionDes(String versionDes) {
        this.versionDes = versionDes;
    }
}
