package com.md.onlib.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.md.onlib.MainActivity;
import com.md.onlib.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class QuestionFragment extends Fragment {

    LayoutInflater inflater;
    LinearLayout sw;
    View root;
    View newRoot;
    View newRootAnswer;
    int answerSize;

    boolean cevapla = false;
    SimpleDateFormat simpleDateFormat, inputDateFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_question, container, false);
        this.inflater = inflater;



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        update();
    }


    public void update(){

        new Thread(){

            @Override
            public void run() {
                super.run();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cevapla = false;
                        if (((MainActivity) getActivity()).online){

                            ((MainActivity)getActivity()).connection.wait = true;
                            ((MainActivity)getActivity()).connection.getquestion= true;

                            simpleDateFormat =new SimpleDateFormat("HH:mm dd-MM-yyyy");
                            simpleDateFormat.setTimeZone(TimeZone.getDefault());
                            inputDateFormat =new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

                            while(((MainActivity)getActivity()).connection.wait){

                            }



                            sw = (LinearLayout) root.findViewById(R.id.linear_layout_scrollview);
                            sw.removeAllViews();

                            newRoot = inflater.inflate(R.layout.questionspot,null);

                            ((TextView)newRoot.findViewById(R.id.q_title)).setText(((MainActivity)getActivity()).connection.questionContent.get(0)[1]);
                            ((TextView)newRoot.findViewById(R.id.q_content)).setText(((MainActivity)getActivity()).connection.questionContent.get(0)[2]);
                            ((TextView)newRoot.findViewById(R.id.q_soran)).setText(((MainActivity)getActivity()).connection.questionContent.get(0)[3]+" sordu");


                            try {
                                ((TextView)newRoot.findViewById(R.id.q_time)).setText(simpleDateFormat.format(inputDateFormat.parse(((MainActivity)getActivity()).connection.questionContent.get(0)[5])));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ((Button)newRoot.findViewById(R.id.questionspot_cevapla)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final View cevaplaView = inflater.inflate(R.layout.question_answer_write, null);
                                    final LinearLayout nsw = (LinearLayout) newRoot.findViewById(R.id.questionspot_inside);
                                    if (!cevapla){
                                        cevapla = true;


                                        nsw.addView(cevaplaView);

                                        ((Button) cevaplaView.findViewById(R.id.answer_tamam)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (((MainActivity) getActivity()).online) {
                                                    String content_send = ((EditText) cevaplaView.findViewById(R.id.answer_edittext)).getText().toString();

                                                    if (content_send.length() >= 2 && content_send.length() <= 1000) {
                                                        ((MainActivity) getActivity()).connection.content = content_send;
                                                        ((MainActivity) getActivity()).connection.wait = true;
                                                        ((MainActivity) getActivity()).connection.give_answer = true;

                                                        nsw.removeView(cevaplaView);
                                                        new Thread() {
                                                            @Override
                                                            public void run() {
                                                                super.run();
                                                                while (((MainActivity) getActivity()).connection.wait) {

                                                                }
                                                                update();
                                                            }
                                                        }.start();
                                                    }
                                                } else {
                                                    try {
                                                        new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                                                                .setMessage("Internet bağlantınızı kontrol edin.").show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                        });


                                    }else{
                                        nsw.removeViewAt(nsw.getChildCount()-1);
                                        cevapla = false;
                                    }
                                }
                            });

                            sw.addView(newRoot);

                            answerSize = ((MainActivity)getActivity()).connection.questionContent.size();
                            for(int i = 1; i < ((MainActivity)getActivity()).connection.questionContent.size(); i++){
                                newRootAnswer = inflater.inflate(R.layout.questionanswerspot,null);

                                ((TextView)newRootAnswer.findViewById(R.id.answer_content)).setText(((MainActivity)getActivity()).connection.questionContent.get(i)[1]);
                                ((TextView)newRootAnswer.findViewById(R.id.answer_cevapladı)).setText(((MainActivity)getActivity()).connection.questionContent.get(i)[2]+" cevapladı");
                                try {
                                    ((TextView)newRootAnswer.findViewById(R.id.answer_time)).setText(simpleDateFormat.format(inputDateFormat.parse(((MainActivity)getActivity()).connection.questionContent.get(i)[3])));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ((TextView)newRootAnswer.findViewById(R.id.textView16)).setText(((MainActivity)getActivity()).connection.questionContent.get(i)[4]);


                                final int finalI = i;


                                ((ImageView)newRootAnswer.findViewById(R.id.givepencil)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if(((MainActivity)getActivity()).online){

                                            if(((MainActivity)getActivity()).connection.questionContent.get(0)[3].equals(((MainActivity)getActivity()).connection.localDatabase.getUsername())){

                                                if(((MainActivity)getActivity()).connection.questionContent.get(0)[6].equals("0")){
                                                    new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sorunun cevabını aldığından emin misin?")
                                                            .setMessage("Verilen kalem geri alınamaz. Bir soru için yalnızca bir cevaba kalem bırakılabilir.")
                                                            .setPositiveButton("Kalem bırak", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    ((MainActivity)getActivity()).connection.givepencil_answerid = ((MainActivity)getActivity()).connection.questionContent.get(finalI)[0];
                                                                    ((MainActivity)getActivity()).connection.wait = true;
                                                                    ((MainActivity)getActivity()).connection.givepencil = true;

                                                                    while(((MainActivity)getActivity()).connection.wait){

                                                                    }

                                                                    update();
                                                                }
                                                            })
                                                            .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .show();
                                                }else{
                                                    new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Bu soru için kalem bırakılmış.")
                                                            .setMessage("Bırakacak kalemimiz kalmadı :(").show();
                                                }




                                            } else{
                                                new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Bu senin sorun muydu?")
                                                        .setMessage("Herkes sadece kendi sorusuna verilen cevaba kalem bırakabilir.")
                                                        .setIcon(android.R.drawable.ic_dialog_alert).show();
                                            }


                                        }else{
                                            new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                                                    .setMessage("Internet bağlantınızı kontrol edin.").show();
                                        }



                                    }
                                });



                                sw.addView(newRootAnswer);
                            }

                        }else{
                            try {
                                new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                                        .setMessage("Internet bağlantınızı kontrol edin.").show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();



    }

    public void kalemvermestart(){

    }
}
