/**
 * Suggestions List Adapter
 * 
 * @author Oriane
 * 
 */

package org.pocketcampus.plugin.food;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.MealTag;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * This class is used to make each section of a list of sandwiches.
 * 
 */
public class SuggestionsListSection extends BaseAdapter implements Filterable {
	private LayoutInflater mInflater_;
	private Suggestions sActivity_;
	private Context activityContext_;

	private Vector<MealTag> tags_;
	private Vector<MealTag> likes_ = new Vector<MealTag>();
	private Vector<MealTag> dislikes_ = new Vector<MealTag>();
	private MealTag likeTag_;
	private MealTag dislikeTag_;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            context of the application the list view is in
	 * @param sandwiches
	 *            all sandwiches represented in the list section.
	 */
	public SuggestionsListSection(Vector<MealTag> tags, Suggestions suggestions, Context context) {
		mInflater_ = LayoutInflater.from(suggestions.getApplicationContext());
		this.tags_ = tags;
		this.sActivity_ = suggestions;
		this.activityContext_ = context;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		convertView = mInflater_.inflate(R.layout.food_suggestions_list_item, null);

		TextView tagName = (TextView) convertView.findViewById(R.id.food_suggestions_tag_name);
		final CheckBox likeBox = (CheckBox) convertView.findViewById(R.id.food_suggestions_like_box);
		final CheckBox dislikeBox = (CheckBox) convertView.findViewById(R.id.food_suggestions_dislike_box);

		tagName.setText(write(tags_.get(position)));

		likeBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckBox c = (CheckBox)v;
				likeTag_ = tags_.get(position);

				if(c.isChecked()){
					if(dislikeBox.isChecked()){
						dislikeBox.setChecked(false);
					}					
					addLikeSuggestion();
				}else{
					removeLikeSuggestion();
				}
			}
		});

		dislikeBox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckBox c = (CheckBox)v;
				dislikeTag_ = tags_.get(position);

				if(c.isChecked()){
					if(likeBox.isChecked()){
						likeBox.setChecked(false);
					}
					addDislikeSuggestion();
				}else{
					removeDislikeSuggestion();
				}
			}
		});

		/* when you click with the dpad center on the sandwich description
		 * Not working yet
		 */
		convertView.setOnClickListener(new OnItemClickListener(position));

		return convertView;
	}

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {}
	}

	public Filter getFilter() {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public int getCount() {
		return tags_.size();
	}

	public Object getItem(int position) {
		return tags_.get(position);
	}

	public Vector<MealTag> getLikeTags(){
		return likes_;
	}

	public Vector<MealTag> getDislikeTags(){
		return dislikes_;
	}

	private void addSuggestion(MealTag tag, Vector<MealTag> tags){
		if(!tags.contains(tag)){
			tags.add(tag);
		}
	}

	private void addLikeSuggestion(){
		addSuggestion(likeTag_, likes_);
	}

	private void addDislikeSuggestion(){
		addSuggestion(dislikeTag_, dislikes_);
	}

	private void removeSuggestion(MealTag tag, Vector<MealTag> tags){
		if(tags.contains(tag)){
			tags.remove(tag);
		}
	}

	private void removeLikeSuggestion(){
		removeSuggestion(likeTag_, likes_);
	}

	private void removeDislikeSuggestion(){
		removeSuggestion(dislikeTag_, dislikes_);
	}

	private String write(MealTag tag){
		Resources r = activityContext_.getResources();

		String string = "";

		switch (tag){
		case MEAT :
			string = r.getString(R.string.food_suggestions_meat).concat("\n");
			break;
		case FISH :
			string = r.getString(R.string.food_suggestions_fish).concat("\n");
			break;
		case VEGETARIAN :
			string = r.getString(R.string.food_suggestions_vege).concat("\n");
			break;
		case PASTA :
			string = r.getString(R.string.food_suggestions_pasta).concat("\n");
			break;
		case RICE :
			string = r.getString(R.string.food_suggestions_rice).concat("\n");
			break;
		case PORC :
			string = r.getString(R.string.food_suggestions_porc).concat("\n");
			break;
		case CHICKEN :
			string = r.getString(R.string.food_suggestions_chicken).concat("\n");
			break;
		case BEEF :
			string = r.getString(R.string.food_suggestions_beef).concat("\n");
			break;
		case HORSE :
			string = r.getString(R.string.food_suggestions_horse).concat("\n");
			break;
		default :
			string = r.getString(R.string.food_suggestions_notag).concat("\n");
		}

		return string;
	}
}