package com.md.onlib.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.md.onlib.ListViewQuestion;
import com.md.onlib.MainActivity;
import com.md.onlib.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BreakFragment extends Fragment {

    private View root;
    TextView sayfa_no;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myinfo", "BreakFragment onCreate");

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_break, container, false);
        ((MainActivity) getActivity()).connection.getQuestionRadio = 0;

        sayfa_no = (TextView) root.findViewById(R.id.sayfa_no);



        Button sorusor = (Button) root.findViewById(R.id.break_sorusor);
        sorusor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_to_questionask);
            }
        });

        Button yenile = (Button) root.findViewById(R.id.refresh_break);
        yenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(0);
            }
        });

        Button get_past = (Button) root.findViewById(R.id.more_past);
        get_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(1);
            }
        });


        RadioGroup rg = (RadioGroup) root.findViewById(R.id.radiogroup_break);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.all:
                        ((MainActivity) getActivity()).connection.getQuestionRadio = 0;break;

                    case R.id.your_asked:
                        ((MainActivity) getActivity()).connection.getQuestionRadio = 1;break;

                    case R.id.your_answered:
                        ((MainActivity) getActivity()).connection.getQuestionRadio = 2;break;

                }

                update(0);
            }
        });






        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("myinfo", "BreakFragment onViewCreated");


    }

    @Override
    public void onStart() {
        super.onStart();
        update(0);


    }

    public void update(final int i) {

        if (((MainActivity) getActivity()).online){
            try {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        try{
                        if (i == 1) {
                            ((MainActivity) getActivity()).connection.askBriefInt += 1;

                        } else {
                            if (((MainActivity) getActivity()).connection.askBriefInt != 0)
                                ((MainActivity) getActivity()).connection.askBriefInt -= 1;
                        }
                        ((MainActivity) getActivity()).connection.wait = true;
                        ((MainActivity) getActivity()).connection.ask_brief = true;


                        while (((MainActivity) getActivity()).connection.wait) {

                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ListView listView = (ListView) root.findViewById(R.id.list_view_q);


                                ListViewQuestion listViewQuestion = new ListViewQuestion(getActivity(), ((MainActivity) getActivity()).connection.brief);


                                listView.setAdapter(listViewQuestion);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        ((MainActivity) getActivity()).connection.brief_position = position;

                                        Navigation.findNavController(view).navigate(R.id.nav_to_question);
                                    }
                                });

                                sayfa_no.setText("Sayfa " + (((MainActivity) getActivity()).connection.askBriefInt + 1));
                            }
                        });

                    }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
    }else{
            try {
                new AlertDialog.Builder(((MainActivity) getActivity())).setTitle("Sunucu ile bağlantı kurulamadı")
                        .setMessage("Internet bağlantınızı kontrol edin.").show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
