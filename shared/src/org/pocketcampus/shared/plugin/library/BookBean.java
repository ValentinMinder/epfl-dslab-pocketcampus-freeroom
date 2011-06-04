package org.pocketcampus.shared.plugin.library;
import java.util.ArrayList;


public class BookBean {
	boolean detailsLoaded_ = false;
	
	String title_;
	String author_;
	int year_;
	int docNumber_;
	ArrayList<String> librairies_;
	
	public BookBean(String title, String author, int year, int docNumber, ArrayList<String> librairies) {
		title_ = title;
		author_ = author;
		year_ = year;
		docNumber_ = docNumber;
		librairies_ = librairies;
	}
	
	public CharSequence getTitle() {
		return title_;
	}
	
	@Override
	public String toString() {
		String desc = "";
		desc += "\"" + title_ + "\"";
		desc += " ["+docNumber_+"]";
		desc += " by " + author_;
		desc += " ("+year_+")";
		desc += " avail. in " + librairies_.size() + " lib\n";
		
		return desc;
	}

	public CharSequence getAuthor() {
		return author_;
	}

	public CharSequence getYear() {
		return Integer.toString(year_);
	}

	public int getDocNumber() {
		return docNumber_;
	}
}
