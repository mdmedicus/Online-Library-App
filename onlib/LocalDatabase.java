package com.md.onlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocalDatabase extends Thread{

    //Local Database
    SQLiteDatabase database;
    Cursor rs;

    public static boolean isStudied = true;


    public LocalDatabase(MyService myService){

        database = myService.openOrCreateDatabase("onLib", myService.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS days(day varchar(150), time INT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR(20),hashcode VARCHAR(100))");
        database.execSQL("CREATE TABLE IF NOT EXISTS study(studying BOOLEAN, basetime INTEGER, breakadd INTEGER, notifyto INT, target INT)");

      //  database.execSQL("ALTER TABLE study ADD COLUMN target INT NOT NULL DEFAULT 0");



    }

    public LocalDatabase(MainActivity mainActivity){
     //   this.mainActivity = mainActivity;
        //LOCAL DATABASE
        database = mainActivity.openOrCreateDatabase("onLib", mainActivity.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS days(day varchar(150), time INT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS user(username VARCHAR(20),hashcode VARCHAR(100))");
        database.execSQL("CREATE TABLE IF NOT EXISTS study(studying BOOLEAN, basetime INTEGER, breakadd INTEGER, notifyto INT, target INT)");



    }

    public void saveToDataDate(int dakika, int notify){ //0 none, 1 noti, 2 studyfragment
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();

        rs = database.rawQuery("SELECT time FROM days WHERE day ='"+dateFormat.format(cal.getTime())+"'", null);
        if(rs.moveToNext()){
            dakika = dakika + Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
            database.execSQL("UPDATE days SET time="+dakika+" WHERE day='"+dateFormat.format(cal.getTime())+"'");


        }else{
            database.execSQL("INSERT INTO days (day, time) VALUES ('"+dateFormat.format(cal.getTime())+"','"+dakika+"')");

        }

        database.execSQL("UPDATE study SET studying=0, breakAdd = 0,  basetime=0, notifyto="+notify+"");

        isStudied = true;
        rs.close();
    }

    public int[] getDataDate(){

        int[] data = new int[2];

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();



        rs = database.rawQuery("SELECT time FROM days WHERE day ='"+dateFormat.format(cal.getTime())+"'", null);
        if(rs.moveToNext()){
            data[0] = Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
        }


        data[1] += data[0];

        for(int i = 1; i < 7; i++){
            cal.add(Calendar.DATE, -1);
            rs = database.rawQuery("SELECT time FROM days WHERE day ='"+dateFormat.format(cal.getTime())+"'", null);

            if(rs.moveToNext()){
                data[1] +=Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
                System.out.println(Integer.parseInt(rs.getString(rs.getColumnIndex("time"))));
            }


        }
        rs.close();
        return data;
    }

    public String getHashcode(){
        String hashcode = null;
        Log.i("myinfo", "getHashcode giriş:");
        rs = database.rawQuery("SELECT hashcode FROM user", null);
        while(rs.moveToNext()) {
            hashcode = rs.getString(rs.getColumnIndex("hashcode"));
            Log.i("myinfo", "getHashcode while:");
        }
        Log.i("myinfo", "getHashcode tamam:");
        rs.close();
        return hashcode;
    }

    public void setHashcode(String hashcode){
        System.out.println(hashcode);
        database.execSQL("UPDATE user SET hashcode='"+hashcode+"'");
    }

    public void setUsername(String username){
        if(getUsername() == null){
            database.execSQL("INSERT INTO user (username) VALUES ('"+username+"')");
        }else{
        database.execSQL("UPDATE user SET username='"+username+"'");}
    }

    public String getUsername(){
        rs = database.rawQuery("SELECT username FROM user", null);
        while(rs.moveToNext()) {
            String username = rs.getString(rs.getColumnIndex("username"));
            return username;
        }
        rs.close();
        return null;
    }

    public int[] getPast(){

        int[] past = new int[7];
        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar cal = Calendar.getInstance();


            rs = database.rawQuery("SELECT time FROM days WHERE day ='" + dateFormat.format(cal.getTime()) + "'", null);
            if (rs.moveToNext()) {
                past[6] = Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
            }

            for (int i = 1; i < 7; i++) {
                cal.add(Calendar.DATE, -1);
                rs = database.rawQuery("SELECT time FROM days WHERE day ='" + dateFormat.format(cal.getTime()) + "'", null);

                if (rs.moveToNext()) {
                    past[6 - i] = Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }
        rs.close();

        return past;
    }

    public int[] getPastMonth(){
        int[] past = new int[28];


        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar cal = Calendar.getInstance();


            rs = database.rawQuery("SELECT time FROM days WHERE day ='" + dateFormat.format(cal.getTime()) + "'", null);

            if (rs.moveToNext()) {
                past[27] = Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
            }

            for (int i = 1; i < 28; i++) {
                cal.add(Calendar.DATE, -1);
                rs = database.rawQuery("SELECT time FROM days WHERE day ='" + dateFormat.format(cal.getTime()) + "'", null);

                if (rs.moveToNext()) {
                    past[27 - i] = Integer.parseInt(rs.getString(rs.getColumnIndex("time")));
                }


            }

        }catch(Exception e){
            e.printStackTrace();
        }
        rs.close();
        return past;
    }

    public void startStudy(Long baseTime, int notify){
        database.execSQL("UPDATE study SET studying=1, breakadd = 0,  basetime="+baseTime+",notifyto="+notify+"");
    }

    public void breakAdd(Long breakadd,int notify){
        database.execSQL("UPDATE study SET studying=2, baseTime = 0,  breakadd='"+breakadd+"',notifyto="+notify+"");
    }

    public long[] checkStudy(){

        long[] ret = new long[2];

        try {
            rs = database.rawQuery("SELECT * FROM study", null);

            if (rs.moveToNext()) {

                switch (rs.getString(rs.getColumnIndex("studying"))) {
                    case "0": //do nothing
                        ret[0] = 0;
                        break;
                    case "1": //çalışıyor
                        ret[0] = 1;
                        ret[1] = Long.parseLong(rs.getString(rs.getColumnIndex("basetime")));
                        break;
                    case "2":
                        ret[0] = 2;
                        ret[1] = Long.parseLong(rs.getString(rs.getColumnIndex("breakadd")));
                        break;

                    default://do nothing
                        ret[0] = 0;
                }

            } else {
                database.execSQL("INSERT INTO study (studying, basetime, breakadd, notifyto, target) VALUES (0,0,0,0,0)");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        rs.close();

        return ret;
    }

    public int getNotify(){

        int a = 0;

        try {
            rs = database.rawQuery("SELECT notifyto FROM study", null);
            if (rs.moveToNext()) {
                a = Integer.parseInt(rs.getString(rs.getColumnIndex("notifyto")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        rs.close();

        return a;
    }

    public void clearNotify(){
        try {
            database.execSQL("UPDATE study SET notifyto=0");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setTarget(int i){
        database.execSQL("UPDATE study SET target="+i+"");
    }

    public int getTarget(){
        rs = database.rawQuery("SELECT target FROM study", null);
        if(rs.moveToNext()){

            int rtrn = Integer.parseInt(rs.getString(rs.getColumnIndex("target")));
            rs.close();
            return rtrn;
        }else{
            rs.close();
            return 0;
        }
    }

}
