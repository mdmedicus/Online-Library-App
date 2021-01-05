package com.md.onlib.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.md.onlib.MainActivity;
import com.md.onlib.R;

public class PastFragment extends Fragment {

    ImageView[] graph = new ImageView[7];
    TextView[] textview_past = new TextView[7];
    TextView[] info = new TextView[3];

    TextView[] haftalar = new TextView[4];

    TextView hedef;

    Button güncelle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_past, container, false);
        graph[0] = (ImageView) root.findViewById(R.id.imageView_past1);
        graph[1] = (ImageView) root.findViewById(R.id.imageView_past2);
        graph[2] = (ImageView) root.findViewById(R.id.imageView_past3);
        graph[3] = (ImageView) root.findViewById(R.id.imageView_past4);
        graph[4] = (ImageView) root.findViewById(R.id.imageView_past5);
        graph[5] = (ImageView) root.findViewById(R.id.imageView_past6);
        graph[6] = (ImageView) root.findViewById(R.id.imageView_past7);

        textview_past[0] = (TextView) root.findViewById(R.id.textView_past1);
        textview_past[1] = (TextView) root.findViewById(R.id.textView_past2);
        textview_past[2] = (TextView) root.findViewById(R.id.textView_past3);
        textview_past[3] = (TextView) root.findViewById(R.id.textView_past4);
        textview_past[4] = (TextView) root.findViewById(R.id.textView_past5);
        textview_past[5] = (TextView) root.findViewById(R.id.textView_past6);
        textview_past[6] = (TextView) root.findViewById(R.id.textView_past7);

        info[0] = (TextView) root.findViewById(R.id.past_info1);
        info[1] = (TextView) root.findViewById(R.id.past_info2);
        info[2] = (TextView) root.findViewById(R.id.past_info3);

        haftalar[0] = (TextView) root.findViewById(R.id.hafta1);
        haftalar[1] = (TextView) root.findViewById(R.id.hafta2);
        haftalar[2] = (TextView) root.findViewById(R.id.hafta3);
        haftalar[3] = (TextView) root.findViewById(R.id.hafta4);


        hedef = (TextView) root.findViewById(R.id.past_hedef);

        güncelle = (Button) root.findViewById(R.id.değiştir);




        güncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker np = new NumberPicker(((MainActivity) getActivity()));
                np.setMaxValue(18);
                np.setMinValue(1);

                new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Günlük Çalışma hedefinizi saat olarak seçiniz.")
                        .setView(np)
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) getActivity()).connection.localDatabase.setTarget(np.getValue());
                                animation();
                            }
                        })
                        .show();
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        animation();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void animation(){



        new Thread(){
            @Override
            public void run() {
                super.run();

                int target = ((MainActivity)getActivity()).connection.localDatabase.getTarget();
                int[] pastMonth = ((MainActivity)getActivity()).connection.localDatabase.getPastMonth();

                Güncelle(target, pastMonth);

                int[] past = ((MainActivity) getActivity()).connection.localDatabase.getPast();

                int enBüyük = 0;
                for (int i = 0; i < past.length; i++) {
                    if(past[i] > enBüyük){
                        enBüyük = past[i];
                    }
                }


                if (enBüyük != 0){

                    float ratio = (float)2f/enBüyük;




                    updateText(past);
                for (int i = 0; i < 90; i++) {
                    RunUı(past, i, ratio);
                    try {
                        Thread.sleep(17);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            }
        }.start();
    }

    public void RunUı(final int[] past, final int sayi, final float ratio){

        try {
            final float scale = getContext().getResources().getDisplayMetrics().density;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    for (int i = 0; i < past.length; i++) {
                        graph[i].requestLayout();
                        graph[i].getLayoutParams().height = (int) (sayi * past[i] * (ratio * scale));
                    }
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void updateText(final int[] past){


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < past.length; i++) {
                    textview_past[i].setText(""+past[i]+" dk");
                }

            }
        });
    }

    public void Güncelle(final int target, final int[] pastMonth){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                //hedef
                hedef.setText(Html.fromHtml("Günlük çalışma hedefin <font color='#3BCD24'>"+target+"</font> saat"));
                int t = 0, tpl = 0;
                for(int i = 0; i < pastMonth.length; i++){
                    if(pastMonth[i] >= target*60)t++;
                    tpl += pastMonth[i];
                }
                info[0].setText(Html.fromHtml("Bu ay günlük hedefine ulaştığın gün sayısı <font color='#3BCD24'>"+t+"</font>"));
                info[1].setText(Html.fromHtml("Bu ay toplamda <font color='#3BCD24'>"+tpl/60+"</font> saat <font color='#3BCD24'>"+tpl%60+"</font> dakika ders çalıştın."));

                //ilk haftanın puanı
                float[] hafta = new float[4];

                for(int h = 0; h < 4; h++){
                    t= 0; tpl = 0;
                    for(int i = h*7; i < h*7+7; i++){
                        if(pastMonth[i]>target*60)t++;
                        tpl += pastMonth[i];
                    }

                    if(t > 0){
                        int toplam = tpl/60;
                        float mean = (float)toplam/7;
                        float varyant =0;
                        for(int i = h*7; i < h*7+7; i++){
                            varyant += Math.pow((float)((float)pastMonth[i]/60-mean),2);
                        }

                        float sdeviation = (float)Math.sqrt((float)varyant);//aslında 7 ye bölünmeli

                        if(sdeviation <= 1){
                            hafta[h] = 1.6f;
                        }else if(sdeviation > 1 && sdeviation <= 2){
                            hafta[h] = 0.8f;
                        }else if(sdeviation > 2 && sdeviation <= 3){
                            hafta[h] = 0.4f;
                        }else if(sdeviation > 3 && sdeviation <= 4){
                            hafta[h] = 0.2f;
                        }else{
                            hafta[h] = 0f;
                        }

                        hafta[h] += (float)t*1.2f;
                        hafta[h] = (float)Math.round(hafta[h]*10)/10;

                    }else{
                        hafta[h] = 0f;
                    }


                    if(hafta[h] < 2){
                        haftalar[h].setText(Html.fromHtml("<font color='#FC0000'> "+hafta[h]+" </font>"));
                    }else if(hafta[h] < 4){
                        haftalar[h].setText(Html.fromHtml("<font color='#FC7E00'> "+hafta[h]+" </font>"));
                    }else if(hafta[h] < 6){
                        haftalar[h].setText(Html.fromHtml("<font color='#FCED00'> "+hafta[h]+" </font>"));
                    }else if(hafta[h] < 8){
                        haftalar[h].setText(Html.fromHtml("<font color='#86FC00'> "+hafta[h]+" </font>"));
                    }else{
                        haftalar[h].setText(Html.fromHtml("<font color='#12AC02'> "+hafta[h]+" </font>"));
                    }

                }


            }
        });


    }

}
