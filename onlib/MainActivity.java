package com.md.onlib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity{


    private AppBarConfiguration mAppBarConfiguration;

    //header ad değiştirme
    private static View headerView;

    //Thread
    public static boolean online = false;
    boolean devam = true;

    //Connection
    public Connection connection;
    FloatingActionButton floating_login;

    //Service
    Intent service;
    public MyService myservice;
    boolean mBound = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("myinfo", "Main onCreate");

        if(connection == null){
            //Network Connection
            connection = new Connection(this);
            connection.start();
            UpdateHeader();
        }

        //Service
        try {
            startService(service);
        }catch(Exception e){
            service = new Intent(this, MyService.class);
            startService(service);
        }

        bindService(new Intent(this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        UpdateHeader();


    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i("myinfo", "Main onsStart()");

        if(connection == null){
            //Network Connection
            connection = new Connection(this);
            connection.start();

        }



        //Service
        try {
            startService(service);
        }catch(Exception e){
            service = new Intent(this, MyService.class);
            startService(service);
        }

        bindService(new Intent(this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("myinfo", "Main onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("myinfo", "Main onDestroy()");

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    public void refresh_floating_visibility(final boolean vis){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(vis && !online){
                    floating_login.setVisibility(View.VISIBLE);
                } else{
                    floating_login.setVisibility(View.INVISIBLE);
                }
            }
        });



    }

    public static boolean isOnline() {
        return online;
    }

    public static void setOnline(boolean online) {
        MainActivity.online = online;
    }



    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("myinfo", "onserviceconnected bağlandı.");
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myservice = binder.getService();
            myservice.check();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("myinfo", "onServiceDisconnected");
            mBound = false;
        }
    };

    public void UpdateHeader(){
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_study, R.id.nav_break, R.id.nav_chat, R.id.nav_past, R.id.nav_aboutus)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //header ad değiştirme
        headerView = navigationView.getHeaderView(0);




        floating_login = findViewById(R.id.floating_login);

        floating_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("myinfo", "floating button");
                if(online) {
                    navController.navigate(R.id.nav_to_login);
                }else{
                    new AlertDialog.Builder(MainActivity.this).setTitle("Sunucu ile bağlantı kurulamadı")
                            .setMessage("Internet bağlantınızı kontrol edin.").show();
                }
            }
        });


        TextView textView = (TextView) headerView.findViewById(R.id.header_name);
        textView.setText(connection.localDatabase.getUsername());
    }



}
