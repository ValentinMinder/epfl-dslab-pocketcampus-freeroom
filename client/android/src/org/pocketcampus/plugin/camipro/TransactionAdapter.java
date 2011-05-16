package org.pocketcampus.plugin.camipro;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.camipro.TransactionBean;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TransactionAdapter extends ArrayAdapter<TransactionBean> {

	LayoutInflater li_;
	java.text.DateFormat df_;
	
	public TransactionAdapter(Context context, int textViewResourceId, List<TransactionBean> transactions) {
		super(context, textViewResourceId, transactions);
		li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df_ = DateFormat.getDateFormat(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
        if (v == null) {
            v = li_.inflate(R.layout.camipro_transaction, null);
        }
        TransactionBean t = getItem(position);
        
        TextView tv;

        String date = df_.format(t.getDate());
        tv = (TextView)v.findViewById(R.id.camipro_item_date);
        tv.setText(date);
        
        tv = (TextView)v.findViewById(R.id.camipro_item_description);
        tv.setText(t.getDescription());
        
        tv = (TextView)v.findViewById(R.id.camipro_item_amount);
        tv.setText(Float.toString(t.getAmount()));
        tv.setTextColor(t.getAmount() < 0.0 ? Color.RED : Color.GREEN);
        
        return v;
	}
	
	

}
