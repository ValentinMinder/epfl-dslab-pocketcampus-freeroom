package org.pocketcampus.platform.android.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pocketcampus.platform.android.ui.dialog.StyledDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogUtils {

	public static interface SingleChoiceHandler<T> {
		void saveSelection(T t);
	}
	
	public static interface MultiChoiceHandler<T> {
		void saveSelection(T t, boolean isChecked);
	}
	
	public static interface TextInputHandler {
		void gotText(String s);
	}
	
	public static <T extends Comparable<? super T>> void showSingleChoiceDialog(Context context, Map<T, ? extends CharSequence> map, CharSequence title, T selected, final SingleChoiceHandler<T> handler) {
		List<T> keysList = new LinkedList<T>(map.keySet());
		Collections.sort(keysList);
		List<? extends CharSequence> valuesList = MapUtils.extractValuesInOrder(map, keysList);
		showSingleChoiceDialog(context, title, selected, handler, keysList, valuesList);
	}
	public static <T extends Comparable<? super T>> void showSingleChoiceDialogSbN(Context context, Map<T, String> map, CharSequence title, T selected, final SingleChoiceHandler<T> handler) {
		Result<T> res = sortByName(map);
		List<T> keysList = res.t;
		List<String> valuesList = res.s;
		showSingleChoiceDialog(context, title, selected, handler, keysList, valuesList);
	}
	private static <T extends Comparable<? super T>> void showSingleChoiceDialog(Context context, CharSequence title, T selected, final SingleChoiceHandler<T> handler, final List<T> keysList, List<? extends CharSequence> valuesList) {
		int selPos = keysList.indexOf(selected);
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setSingleChoiceItems(
						valuesList.toArray(new CharSequence[]{}), selPos, 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								handler.saveSelection(keysList.get(which));
								dialog.dismiss();
							}
						})
				.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	public static <T extends Comparable<? super T>> void showMultiChoiceDialog(Context context, Map<T, ? extends CharSequence> map, CharSequence title, Set<T> selected, final MultiChoiceHandler<T> handler) {
		final List<T> keysList = new LinkedList<T>(map.keySet());
		Collections.sort(keysList);
		List<? extends CharSequence> valuesList = MapUtils.extractValuesInOrder(map, keysList);
		showMultiChoiceDialog(context, title, selected, handler, keysList, valuesList);
	}
	public static <T extends Comparable<? super T>> void showMultiChoiceDialogSbN(Context context, Map<T, String> map, CharSequence title, Set<T> selected, final MultiChoiceHandler<T> handler) {
		Result<T> res = sortByName(map);
		List<T> keysList = res.t;
		List<String> valuesList = res.s;
		showMultiChoiceDialog(context, title, selected, handler, keysList, valuesList);
	}
	private static <T extends Comparable<? super T>> void showMultiChoiceDialog(Context context, CharSequence title, Set<T> selected, final MultiChoiceHandler<T> handler, final List<T> keysList, List<? extends CharSequence> valuesList) {
		boolean[] selPos = new boolean[keysList.size()];
		for(int i = 0; i < keysList.size(); i++) {
			selPos[i] = selected.contains(keysList.get(i));
		}
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setMultiChoiceItems(
						valuesList.toArray(new CharSequence[]{}), selPos, 
						new OnMultiChoiceClickListener() {
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								handler.saveSelection(keysList.get(which), isChecked);
							}
						})
				.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public static View buildDialogTitle(Context con, CharSequence title) {
//		LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View v = inflater.inflate(R.layout.sdk_actionbar_dialog, new LinearLayout(con));
//		TextView tv = (TextView) v.findViewById(R.id.actionbar_title);
//		tv.setText(title);
//		return v;
		return null;
	}

	public static void showInputDialog(Context context, CharSequence title, CharSequence message, String buttonText, final TextInputHandler handler) {
		final EditText input = new EditText(context);
        TextView tv = new TextView(context);
        tv.setText(message);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tv);
        ll.addView(input);
		
        StyledDialog.Builder sdb = new StyledDialog.Builder(context);
        sdb.setTitle(title);
        sdb.setContentView(ll);
        sdb.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
				handler.gotText(input.getText().toString());
				dialogInterface.dismiss();
            }
        });
        sdb.setCanceledOnTouchOutside(true);
        sdb.create().show();
	}
	
	public static void alert(Context c, CharSequence title, CharSequence message) {
        AlertDialog.Builder sdb = new AlertDialog.Builder(c);
        sdb.setTitle(title);
        sdb.setMessage(message);
        sdb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            	dialogInterface.dismiss();
            }
        });
        sdb.create().show();
	}
	
	private static class Complex <T> {
		public T t;
		public String s;
		public Complex(T t, String s) {
			this.t = t;
			this.s = s;
		}
	}
	
	private static class Result <T> {
		public List<T> t;
		public List<String> s;
		public Result(List<T> t, List<String> s) {
			this.t = t;
			this.s = s;
		}
	}
	
	private static <T> Result<T> sortByName(Map<T, String> map) {
		List<Complex<T>> l = new LinkedList<Complex<T>>();
		for(Entry<T, String> e : map.entrySet()) {
			l.add(new Complex<T>(e.getKey(), e.getValue()));
		}
		Collections.sort(l, new Comparator<Complex<T>>(){
			public int compare(Complex<T> arg0, Complex<T> arg1) {
				return arg0.s.compareTo(arg1.s);
			}
		});
		List<T> t = new LinkedList<T>();
		List<String> s = new LinkedList<String>();
		for(Complex<T> c : l) {
			t.add(c.t);
			s.add(c.s);
		}
		return new Result<T>(t, s);
	}

}
