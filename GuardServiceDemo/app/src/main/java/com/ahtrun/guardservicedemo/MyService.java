package com.ahtrun.guardservicedemo;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {
    private boolean timeFlag;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("title")
                .setContentText("describe")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        Notification notification = builder.getNotification();
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, Service.START_STICKY);
    }

    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            while (!timeFlag) {
//                Log.i("mihui","倒计时任务进行中");
                Intent i=new Intent();
                i.setAction("TIME_TASK");
                sendBroadcast(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    savaFileToSD("test.txt",String.valueOf(System.currentTimeMillis()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("lhp","TIME_TASK");
            }
        }
    };
    public void savaFileToSD(String filename, String filecontent) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory()+ "/" + filename;

            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            output.write(filecontent.getBytes());
            //将String字符串以字节流的形式写入到输出流中
            output.close();
            //关闭输出流
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timeFlag=true;

    }
    File file;
    @Override
    public void onCreate() {
        super.onCreate();
        timeFlag=false;
        file=new File(Environment.getExternalStorageDirectory(),"/test.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Thread(timerTask).start();
    }
}
