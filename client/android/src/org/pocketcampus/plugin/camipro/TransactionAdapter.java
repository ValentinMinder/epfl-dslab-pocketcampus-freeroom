package org.pocketcampus.plugin.camipro;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.shared.plugin.camipro.TransactionBean;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter for the list of transactions. 
 * 
 * @author Jonas
 *
 */
public class TransactionAdapter extends ArrayAdapter<TransactionBean> {

	private LayoutInflater li_;
	private java.text.DateFormat df_; // Used to format the date
	private Context context_;

	// Colors
	private static int minus_;
	private static int plus_;
	
	/**
	 * Adapter constructor
	 * @param context The Camipro plugin
	 * @param textViewResourceId Layout for a row in the list
	 * @param transactions List of transactions
	 */
	public TransactionAdapter(Context context, int textViewResourceId, List<TransactionBean> transactions) {
		super(context, textViewResourceId, transactions);
		li_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df_ = DateFormat.getDateFormat(context);
		context_ = context;

		minus_ = context_.getResources().getColor(R.color.camipro_minus);
		plus_ = context_.getResources().getColor(R.color.camipro_plus);
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
        tv.setTextColor(t.getAmount() < 0.0 ? minus_ : plus_);
        
        return v;
	}
	
	

}
