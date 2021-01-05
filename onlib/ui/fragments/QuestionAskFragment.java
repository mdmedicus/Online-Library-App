package com.md.onlib.ui.fragments;

import android.drm.DrmStore;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.md.onlib.MainActivity;
import com.md.onlib.R;

public class QuestionAskFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_questionask, container, false);

        final EditText title = (EditText) root.findViewById(R.id.questionask_title);
        final EditText content = (EditText) root.findViewById(R.id.questionask_content);


        Button button = (Button) root.findViewById(R.id.questionask_sor);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).online){
                String sTitle = title.getText().toString();
                String sContent = content.getText().toString();

                if(sTitle.length() >= 10 && sTitle.length() <= 100
                        && sContent.length() >= 20 && sContent.length() <= 1000){

                    ((MainActivity)getActivity()).connection.title = sTitle;
                    ((MainActivity)getActivity()).connection.content = sContent;
                    ((MainActivity)getActivity()).connection.wait = true;
                    ((MainActivity)getActivity()).connection.ask_question= true;


                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getParentFragmentManager().popBackStack();

                }else{
                    new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Bunu bilmen iyi olacak:")
                            .setMessage("Başlık 10-100 karakter arasında,  \niçerik 20-1000 karakter arasında olmalı.").show();
                }

                }else{
                    try {
                        new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                                .setMessage("Internet bağlantınızı kontrol edin.").show();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
//
            }

        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
