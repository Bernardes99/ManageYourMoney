package com.example.manageyourmoney;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;

// So that we can use a custom listView
public class CardListAdapter extends BaseAdapter {

    Context context;

    ArrayList<CardObject> cardObjects;

    public CardListAdapter(Context context, ArrayList<CardObject> cardObjects){
        this.context=context;
        this.cardObjects=cardObjects;
    }

    @Override
    public int getCount() {
        return this.cardObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.cardObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView =inflater.inflate(R.layout.listview_adapter1, null);
        TextView nameCard = (TextView)convertView.findViewById(R.id.list_name_card);
        TextView balance = (TextView)convertView.findViewById(R.id.list_balance);
        TextView balance_quantity = (TextView)convertView.findViewById(R.id.list_balance_quantity);


        String name = this.cardObjects.get(position).getCardName();
        double balance_double = this.cardObjects.get(position).getBalance();
        String balance_q = "";

        DecimalFormat df = new DecimalFormat("0.00");

        if (balance_double>0)
        {
            balance_q = "+" + df.format(balance_double);
        }
        else {
            balance_q = df.format(balance_double);
        }
        balance_q+="€";

        String card_or_hand_balance="";
        if (this.cardObjects.get(position).type)
        {
            card_or_hand_balance="Card Balance";
        }
        else {
            card_or_hand_balance="Money in hand";
        }

        nameCard.setText(name);
        balance.setText(card_or_hand_balance);
        balance_quantity.setText(balance_q);

        return convertView;
    }
}