package com.md.onlib;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("myinfo","Service onBind.");

        return binder;
    }




    //
    LocalDatabase localDatabase;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("myinfo", "Service created");
        createNotificationChannel();
        localDatabase = new LocalDatabase(this);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "onLib::myService");
        wakeLock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("myinfo", "onStartCommand");
        wakeLock.acquire();
        if(intent != null && intent.getAction() != null){
            if(intent.getAction().equals("clickStart")){
                click_noti_start();
                Log.i("myinfo", "Made it real");

            }
        }


        return Service.START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("myinfo", "Service destroyed");
        wakeLock.release();
    }



    //Notification
    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;

    RemoteViews remoteview;

    private void createNotificationChannel() {



        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Online Library", name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        remoteview = new RemoteViews(getPackageName(),R.layout.notification_chr);
        remoteview.setTextViewText(R.id.noti_textview, "İyi Çalışmalar");

        Intent intent = new Intent(this, getClass());
        intent.setAction("clickStart");
        PendingIntent p_intent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteview.setOnClickPendingIntent(R.id.not_chr_start, p_intent);


        Log.i("myinfo", "remoteview created");

        Intent intent_openApp = new Intent(this, MainActivity.class);
        intent_openApp.setAction(Intent.ACTION_MAIN);
        intent_openApp.addCategory(Intent.CATEGORY_LAUNCHER);
        intent_openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        PendingIntent pendingIntent_openApp = PendingIntent.getActivity(this, 0, intent_openApp, 0);


        builder = new NotificationCompat.Builder(this, "Online Library")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.book_colorize)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteview)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent_openApp)
                .setOngoing(false);


        startForeground(11, builder.build());
       /* notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(11, builder.build());*/

        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){




                    if(localDatabase != null && localDatabase.getNotify() == 1){
                        check();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }

            }
        }.start();

    }

    public void check(){

        try{
            boolean setOn = false;
            long[] ret = localDatabase.checkStudy();

            if (ret[0] == 0) {
                remoteview.setChronometer(R.id.noti_chr, SystemClock.elapsedRealtime(), null, false );
                remoteview.setTextViewText(R.id.not_chr_start, "Başla");
                setOn=false;

            } else if (ret[0] == 1) {
                remoteview.setChronometer(R.id.noti_chr, ret[1], null, true);
                remoteview.setTextViewText(R.id.not_chr_start, "Durdur");
                setOn=true;

            } else if (ret[0] == 2) {

                remoteview.setChronometer(R.id.noti_chr, SystemClock.elapsedRealtime()-ret[1], null, false);
                remoteview.setTextViewText(R.id.not_chr_start, "Devam");
                setOn=false;
            }

            builder.setCustomContentView(remoteview);
            setOngoing(setOn);

            localDatabase.clearNotify();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void click_noti_start(){



        long[] ret = localDatabase.checkStudy();
        boolean setOn = false;

        if(ret[0] == 0){
            remoteview.setChronometer(R.id.noti_chr, SystemClock.elapsedRealtime(), null, true );
            localDatabase.startStudy(SystemClock.elapsedRealtime(),2);
            remoteview.setTextViewText(R.id.not_chr_start, "Durdur");
            setOn=true;


        } else if(ret[0] == 1){
            remoteview.setChronometer(R.id.noti_chr, ret[1], null, false);
            localDatabase.breakAdd(SystemClock.elapsedRealtime()-ret[1],2);
            remoteview.setTextViewText(R.id.not_chr_start, "Devam");
            setOn=false;

        } else if (ret[0] == 2) {
            remoteview.setChronometer(R.id.noti_chr, SystemClock.elapsedRealtime()-ret[1], null, true );
            localDatabase.startStudy(SystemClock.elapsedRealtime()-ret[1],2);
            remoteview.setTextViewText(R.id.not_chr_start, "Durdur");
            setOn=true;
        }
        builder.setCustomContentView(remoteview);
        setOngoing(setOn);
    }


    public void setOngoing(boolean set){
        builder.setOngoing(set);
        if(set){
            startForeground(11, builder.build());
        }else{
            startForeground(11, builder.build());
            stopForeground(false);
        }

    }



}
