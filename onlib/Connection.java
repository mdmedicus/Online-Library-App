package com.md.onlib;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Connection extends Thread {



    /////
    //Socket and Reader-Writer
    Socket socket;
    public static DataInputStream input;
    public static DataOutputStream output;


    //Server-response
    public static int n_online = 0;
    public static int n_studying = 0;
    public static String[] names_studying =  {"Yenile >"};

    //server-response-2
    public boolean isLogin = false;
    public boolean wait = true;
    public boolean firstLoop = false;

    public boolean login_positive = false;
    public boolean register_positive = false;
    public boolean refresh_positive = false;
    public boolean ask_question = false;
    public boolean ask_brief = false;
    public boolean getquestion = false;
    public boolean give_answer = false;
    public boolean givepencil = false;



    public static int myStudyingStart = 0;
    public static int myStudyingStop = 0;




    ////


    //login
    public String user_pass;


    //class
    public static LocalDatabase localDatabase;


    //question
    public String title;
    public String content;


    //getquestion
    public ArrayList<String[]> brief;
    public int brief_position;
    public ArrayList<String[]> questionContent;
    public int askBriefInt = 0;


    public String givepencil_answerid;

    public int getQuestionRadio = 0;


    /////
    MainActivity mainActivity;

    public Connection(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        //LocalDatabase
        localDatabase = new LocalDatabase(mainActivity);
        localDatabase.start();

    }


    @Override
    public void run() {
        super.run();

        reConnect();
        String[] gelen;
        int i = 100;
        while(mainActivity.devam){
            try {


                    if(i == 100) {
                        i = 0;

                        try{
                         //   Log.i("myinfo", "checking connection");
                            output.writeUTF("0/");
                            gelen = input.readUTF().split("/");
                            if(gelen[0].equals("1")){
                                Log.i("myinfo", "connection is Ok 1");
                                mainActivity.online = true;
                            } else if(gelen[0].equals("2")){
                                n_online = Integer.parseInt(gelen[1]);
                                n_studying = Integer.parseInt(gelen[2]);
                                Log.i("myinfo", "connection is Ok 2"+gelen[2]);
                                mainActivity.online = true;
                        //        mainActivity.resfreshNotification();

                            } else{
                                socket.close();
                                mainActivity.online = false;
                                wait = false;
                                reConnect();

                            }



                        } catch(Exception e) {
                            mainActivity.online = false;
                            wait = false;
                            reConnect();
                        }


                    }
                    firstLoop = true;


                if(login_positive) Login();

                if(register_positive) Register();


                if(myStudyingStart == 1){
                    output.writeUTF("1");
                    myStudyingStart = 0;
                }

                if(myStudyingStop == 1){
                    output.writeUTF("-1");
                    myStudyingStop = 0;
                }


                if(refresh_positive) Refresh_Listview();

                if (ask_question) askQuestion();

                if(ask_brief) askBrief();

                if(getquestion) getQuestion();

                if(give_answer) sendAnswer();

                if(givepencil) givePencil();




                try {
                    if(mainActivity.online){
                        Thread.sleep(50);
                    }else{

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }catch(Exception e){
                mainActivity.online = false;
                wait = false;
            }
            i++;
        }

    }


    public void reConnect(){
        try {
         //   Log.i("myinfo", "reconnect()");
            socket = new Socket();
            socket.connect(new InetSocketAddress("serverip", "serverport_INTEGER"),1000);

            Log.i("myinfo","reconnected");


            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());

            sign_in_on_lib_server();

        } catch (IOException e) {
           // Log.i("myinfo", "Connection is refused.");

        }
    }

    public void sign_in_on_lib_server(){

            try {
                Log.i("myinfo", "Sign in yapılmaya çalışılıyor");


                String hashcode = localDatabase.getHashcode();

                Log.i("myinfo", "sign_in_on_lib_server onCreate " + hashcode);

                if (hashcode != null) {

                    if (alreadyLogin(hashcode)) {
                        Log.i("myinfo", "Servera başarıyla giriş yapıldı");
                        int i = (int)localDatabase.checkStudy()[0];
                        if(i == 1){
                            myStudyingStart = 1;
                        }else{
                            myStudyingStop = 1;
                        }
                        isLogin = true;

                    } else {
                        Log.i("myinfo", "sign in dosyası içindekiler hatalı");

                    }
                } else {
                    //silmeyi kaldırdım

                    Log.i("myinfo", "sign in , user_pass null");
                }

            } catch (Exception e) {
                Log.i("myinfo", "Local database'e bağlanılamadı.");
            }




    }

    public boolean alreadyLogin(String hashcode){

        System.out.println("alreadylogin hashcode: "+hashcode);
        String already_hashcode = ""+hashcode;
        try{
            output.writeUTF("2/"+already_hashcode);
            String[] input_from_server = ((String) input.readUTF()).split("/");
            Log.i("myinfo", "alreadylogin cevabı: "+input_from_server[0]);
            if(Integer.parseInt(input_from_server[0]) == 1){
                isLogin = true;
                localDatabase.setHashcode(input_from_server[1]);
                return true;
            } else {
                Log.i("myinfo", "alreadyLogin : hashcode hatalı");
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("myinfo", "Serverdan input alınamadı");
            return false;
        }

    }

    public void Login(){
        System.out.println("user_pass: "+user_pass);


        try{
            output.writeUTF("3/"+user_pass);//3 olmalı
            String[] input_from_server = ((String) input.readUTF()).split("/");
            Log.i("myinfo", "login cevabı: "+input_from_server[1]);
            if(Integer.parseInt(input_from_server[0]) == 1){
                isLogin = true;
                localDatabase.setUsername(user_pass.split("/")[0]);
                localDatabase.setHashcode(input_from_server[1]);


                System.out.println(localDatabase.getHashcode());


            } else {
                Log.i("myinfo", "Login : Kullanıcı adı ya da şifre hatalı");

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("myinfo", "Serverdan input alınamadı");

        }

        login_positive = false;
        wait = false;

    }





    //Register
    public void Register(){

        System.out.println("register user_pass: "+user_pass);


        try{
            output.writeUTF("1/"+user_pass);//
            String[] input_from_server = ((String) input.readUTF()).split("/");
            Log.i("myinfo", "register cevabı: "+input_from_server[1]);
            if(Integer.parseInt(input_from_server[0]) == 1){
                isLogin = true;
                localDatabase.setUsername(user_pass.split("/")[0]);
                localDatabase.setHashcode(input_from_server[1]);



            } else {
                Log.i("myinfo", "Register : Kullanıcı adı ya da şifre hatalı");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("myinfo", "Serverdan input alınamadı");
        }

        register_positive = false;
        wait = false;

    }

    public void Refresh_Listview(){
        System.out.println("refresh");

        try {
            output.writeUTF("2/"+user_pass);

            names_studying = ((String) input.readUTF()).split("/");


        } catch (Exception e) {
            e.printStackTrace();
        }

        refresh_positive = false;
        wait = false;

    }


    public void askQuestion(){

        try {
            output.writeUTF("ask");
            output.writeUTF(title);
            output.writeUTF(content);
        }catch(Exception e){
            e.printStackTrace();
        }

        ask_question = false;
        wait = false;

    }

    public void askBrief(){
        brief = new ArrayList<>();

        if(getQuestionRadio == 0){
            try {
                output.writeUTF("getaskbrief/"+askBriefInt);
                while (input.readUTF().equals("-5")) {
                    String[] newOne = new String[5];
                    newOne[0] = input.readUTF();
                    newOne[1] = input.readUTF();
                    newOne[2] = input.readUTF();
                    newOne[3] = input.readUTF();
                    newOne[4] = input.readUTF();

                    brief.add(newOne);

                }

            }catch(Exception e){
                e.printStackTrace();
            }

        } else if(getQuestionRadio == 1){
            try {
                output.writeUTF("getmyaskbrief/"+askBriefInt);
                while (input.readUTF().equals("-5")) {
                    String[] newOne = new String[5];
                    newOne[0] = input.readUTF();
                    newOne[1] = input.readUTF();
                    newOne[2] = input.readUTF();
                    newOne[3] = input.readUTF();
                    newOne[4] = input.readUTF();

                    brief.add(newOne);

                }

            }catch(Exception e){
                e.printStackTrace();
            }

        } else if(getQuestionRadio == 2){
            try {
                output.writeUTF("getmuansbrief/"+askBriefInt);
                while (input.readUTF().equals("-5")) {
                    String[] newOne = new String[5];
                    newOne[0] = input.readUTF();
                    newOne[1] = input.readUTF();
                    newOne[2] = input.readUTF();
                    newOne[3] = input.readUTF();
                    newOne[4] = input.readUTF();

                    brief.add(newOne);

                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }

        ask_brief = false;
        wait = false;

    }

    public void getQuestion(){
        questionContent = new ArrayList<>();
        try {
            output.writeUTF("getquestion/" + brief.get(brief_position)[0]);

            if(input.readUTF().equals("-5")) {
                String[] newOne = new String[7];
                newOne[0] = input.readUTF();//questionid
                newOne[1] = input.readUTF();//questiontitle
                newOne[2] = input.readUTF();//question
                newOne[3] = input.readUTF();//askedby
                newOne[4] = input.readUTF();//answersize
                newOne[5] = input.readUTF();//time
                newOne[6] = input.readUTF();//questiondone

                questionContent.add(newOne);
            }

            while(input.readUTF().equals("-5")){
                String[] newOne = new String[5];
                newOne[0] = input.readUTF();//answerid
                newOne[1] = input.readUTF();//answer
                newOne[2] = input.readUTF();//askedby
                newOne[3] = input.readUTF();//answertime
                newOne[4] = input.readUTF();//answerdone

                questionContent.add(newOne);
            }



        }catch(Exception e){
            e.printStackTrace();
        }

        getquestion = false;
        wait= false;

    }

    public void sendAnswer(){

        try {

            output.writeUTF("giveanswer/"+questionContent.get(0)[0]);
            output.writeUTF(content);

        }catch(Exception e){
            e.printStackTrace();
        }

        give_answer = false;
        wait = false;

    }

    public void givePencil(){
        try {
            output.writeUTF("givepencil/" + questionContent.get(0)[0] + "/" + givepencil_answerid);
        }catch(Exception e){
            e.printStackTrace();
        }

        givepencil = false;
        wait = false;
    }

}


