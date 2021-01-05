package com.md.onlib.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.md.onlib.MainActivity;
import com.md.onlib.R;

import java.util.concurrent.TimeUnit;

public class StudyFragment extends Fragment {




    public boolean home_resume = false;
    boolean study_started = false;
    //Chronometer
    Button start_chr, break_chr;

    Chronometer chr;
    //for addTime animation
    long add = 0;
    TextView animationadd;


    TextView textView_blink, textView_studying, textView_day, textView_week, textView_weektoday;



    public StudyFragment(){

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("myinfo", "on Attach");




    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myinfo", "Studyfragment onCreate");

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("myinfo", "onCreateView");
        final View root = inflater.inflate(R.layout.fragment_study, container, false);
        textView_blink = (TextView) root.findViewById(R.id.blink);
        textView_studying = (TextView) root.findViewById(R.id.textView2);
        textView_day = (TextView) root.findViewById(R.id.textView5);
        textView_week = (TextView) root.findViewById(R.id.textView6);
        textView_weektoday = (TextView) root.findViewById(R.id.textView7);


        chr = (Chronometer) root.findViewById(R.id.study_chr);


        start_chr = (Button) root.findViewById(R.id.start_chr);
        break_chr = (Button) root.findViewById(R.id.break_chr);

        start_chr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_chr();
            }
        });

        break_chr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                break_chr();
            }
        });


        animationadd = (TextView) root.findViewById(R.id.animationadd);



        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("myinfo", "onViewCreated");



        try {
            final ListView sections = (ListView) view.findViewById(R.id.sections);

            final ArrayAdapter<String> sectionAdt = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, ((MainActivity) getActivity()).connection.names_studying) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);

                    textView.setTextColor(Color.GRAY);


                    return view;
                }
            };

            sections.setAdapter(sectionAdt);

            Button resfresh_Lisview = getActivity().findViewById(R.id.refresh_studyings);

            resfresh_Lisview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((MainActivity) getActivity()).online) {
                        ((MainActivity) getActivity()).connection.refresh_positive = true;
                        ((MainActivity) getActivity()).connection.wait = true;

                        while (((MainActivity) getActivity()).connection.wait) {

                        }


                        sections.setAdapter(new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, android.R.id.text1, ((MainActivity) getActivity()).connection.names_studying) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                                textView.setTextColor(Color.GRAY);


                                return view;
                            }
                        });
                    }
                }


            });

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.i("myinfo", "inflate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("myinfo", "onStart");
        text_Studied();
        ((MainActivity)getActivity()).refresh_floating_visibility(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("myinfo", "Studyfragment onResume");
        home_resume = true;
        check_chr();
        openThread();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("myinfo", "onPause");
        home_resume = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("myinfo", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("myinfo", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void openThread(){

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    int i = 0;
                    while (home_resume) {
                        if(((MainActivity)getActivity()).connection.localDatabase.getNotify() == 2){
                            check_chr();
                            ((MainActivity)getActivity()).connection.localDatabase.clearNotify();
                        }

                  //      Log.i("myinfo", "openThread");

                        if (((MainActivity)getActivity()).online && home_resume) {
                            text_studying();
                            try {
                                if (((MainActivity) getActivity()).connection.localDatabase.isStudied) {
                                    text_Studied();
                                    ((MainActivity) getActivity()).connection.localDatabase.isStudied = false;
                                }
                            } catch (NullPointerException e) {
                                //----
                            }


                            homeFragment_reddot(Color.RED);

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            homeFragment_reddot(Color.GRAY);

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

    public void homeFragment_reddot(final int color) {

        try{

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            //    Log.i("myinfo","reddot");

                    textView_blink.setTextColor(color);


            }
        });

        }catch(NullPointerException e){
            Log.i("myinfo","blink textview yok");
        }
    }

    public void text_studying() {

        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //    Log.i("myinfo","reddot");

                    textView_studying.setText(Integer.toString(((MainActivity)getActivity()).connection.n_studying));


                }
            });

        }catch(NullPointerException e){
            Log.i("myinfo","blink textview yok");
        }
    }

    public void text_Studied() {

        try{

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("myinfo","Studied");
                    int[] data = ((MainActivity)getActivity()).connection.localDatabase.getDataDate();
                    textView_day.setText("Bugün toplam "+((int)data[0]/60)+" saat "+data[0]%60+" dakika ");
                    textView_week.setText("Bu hafta toplam " +data[1]/60+" saat "+data[1]%60+" dakika");
                    textView_weektoday.setText("Bu hafta günde ortalama " +((data[1])/7)/60+" saat "+((data[1])/7)%60+" dakika");

                }
            });

        }catch(NullPointerException e){
            Log.i("myinfo","blink textview yok");
        }
    }


    public void check_chr(){


       getActivity().runOnUiThread(new Runnable() {
           long[] ret = ((MainActivity)getActivity()).connection.localDatabase.checkStudy();

           @Override
           public void run() {
               switch ((int)ret[0]){
                   case 0: study_started = false;
                       add = 0;
                       chr.stop();
                       start_chr.setText("BAŞLA");
                       ((MainActivity)getActivity()).connection.myStudyingStop = 1;

                       break;
                   case 1:
                       start_chr.setText("BİTİR");
                       study_started = true;
                       chr.setBase(ret[1]);
                       chr.start();
                       add = 0;
                       ((MainActivity)getActivity()).connection.myStudyingStart = 1;
                       break;
                   case 2: study_started=false;
                       chr.setBase(SystemClock.elapsedRealtime()-ret[1]);
                       chr.stop();
                       start_chr.setText("Devam");
                       add = ret[1];
                       ((MainActivity)getActivity()).connection.myStudyingStop = 1;
                       break;
               }
           }
       });

    }

    public void start_chr(){




        if(!study_started) {

            chr.setBase(SystemClock.elapsedRealtime()-add);
            chr.start();
            ((MainActivity)getActivity()).connection.localDatabase.startStudy(chr.getBase(),1);
            start_chr.setText("BİTİR");
            study_started = true;
            ((MainActivity)getActivity()).connection.myStudyingStart = 1;

        } else {
            add = 0;
            chr.stop();
            start_chr.setText("BAŞLA");
            study_started=false;
            long time = SystemClock.elapsedRealtime()-chr.getBase();
            String date = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time));
            animationadd.setText("+"+date);
            animationadd.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).connection.myStudyingStop = 1;
            ((MainActivity)getActivity()).connection.localDatabase.saveToDataDate((int)TimeUnit.MILLISECONDS.toMinutes(time),1);

            chr.setBase(SystemClock.elapsedRealtime());


        }


    }

    public void break_chr(){
        if (study_started) {
            chr.stop();
            ((MainActivity)getActivity()).connection.localDatabase.breakAdd(SystemClock.elapsedRealtime()-chr.getBase(),1);
            start_chr.setText("Devam");
            study_started = false;
            add = SystemClock.elapsedRealtime()-chr.getBase();
            ((MainActivity)getActivity()).connection.myStudyingStop = 1;

        }


    }




}
