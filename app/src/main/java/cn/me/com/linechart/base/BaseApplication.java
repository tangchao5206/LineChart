package cn.me.com.linechart.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/1/7.
 */
public class BaseApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    public static  String  deviceId;
    private static long    mMainThreadId;
    public static  String  APP_VERSION_NAME;
    public static  int     APP_VERSION_CODE;
    public static String              currentUserNick  = "";
    //定义一个协议内存缓存的容器/存储结构
    private       Map<String, String> mProtocolHashMap = new HashMap<>();

    public Map<String, String> getProtocolHashMap() {
        return mProtocolHashMap;
    }

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return mContext;
    }


    /**
     * 得到主线程的handler
     */
    public static Handler getHandler() {
        return mHandler;
    }


    /**
     * 得到主线程的id
     */
    public static long getMainThreadId() {
        return mMainThreadId;
    }

    @Override
    public void onCreate() {//程序的入口方法
//        mRunnHandler.postDelayed(timeRunn, 3000);
       // CrashHandler.getInstance().init(this);//手机奔溃信息
        //1.上下文
        mContext = getApplicationContext();

        //2.得到主线程的handler
        mHandler = new Handler();

        //3.得到主线程的id
        mMainThreadId = android.os.Process.myTid();
        /**
         Tid: Thread
         Uid:User
         Pid:Process
         */
        super.onCreate();
        initAppVersion();





    }

    public static BaseApplication getInsance() {
        if (app == null) {
            app = new BaseApplication();
        }
        return app;
    }

    public static BaseApplication app = null;


    private void initAppVersion() {
        final PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            APP_VERSION_NAME = pi.versionName;
            APP_VERSION_CODE = pi.versionCode;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}