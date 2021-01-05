package com.md.onlib;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewQuestion extends BaseAdapter {

    private ArrayList<String[]> questionsTitle;

    private Context context;
    private LayoutInflater layoutInflater;

    public ListViewQuestion(Context context, ArrayList<String[]> questionsTitle){
        Log.i("myinfo", "BListViewQuestion constructor");
        this.context = context;
        this.questionsTitle = questionsTitle;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return questionsTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return questionsTitle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i("myinfo", "BListViewQuestion getView");
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.listview_question, parent, false);
        }
        Log.i("myinfo", "listviewadapter getView");



        ((TextView) convertView.findViewById(R.id.questionTitle)).setText(questionsTitle.get(position)[1]);
        ((TextView) convertView.findViewById(R.id.username_ask)).setText(questionsTitle.get(position)[2] + " sordu");
        ((TextView) convertView.findViewById(R.id.answersize)).setText(questionsTitle.get(position)[3] + " cevap");

        return convertView;
    }
}
