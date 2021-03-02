package com.example.manageyourmoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionListAdapter extends BaseAdapter {

    Context context;

    ArrayList<TransactionObject> transactionObjects;

    public TransactionListAdapter(Context context, ArrayList<TransactionObject> transactionObjects){
        this.context = context;
        this.transactionObjects = transactionObjects;
    }






    @Override
    public int getCount() {
        return this.transactionObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.transactionObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView =inflater.inflate(R.layout.listview_adapter_transactions, null);
        TextView tdate = (TextView)convertView.findViewById(R.id.list_transaction_date);
        TextView tcategory = (TextView)convertView.findViewById(R.id.list_transaction_category);
        TextView tquantity = (TextView)convertView.findViewById(R.id.list_transaction_quantity);

        // DATE
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_d = this.transactionObjects.get(position).getDate();
        String date_s = simpleDateFormat.format(date_d);

        // CATEGORY
        String category = this.transactionObjects.get(position).getCategory();

        // QUANTITY
        double quantity_d = this.transactionObjects.get(position).getQuantity();
        DecimalFormat df = new DecimalFormat("0.00");
        String quantity_s = df.format(quantity_d);
        quantity_s+="€";


        // SET TEXTVIEWS
        tdate.setText(date_s);
        tcategory.setText(category);
        tquantity.setText(quantity_s);


        return convertView;
    }
}
