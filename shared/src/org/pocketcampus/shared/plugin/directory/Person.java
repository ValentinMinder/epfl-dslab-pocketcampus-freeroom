package org.pocketcampus.shared.plugin.directory;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Person implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3188824960701486209L;
	public String first_name;
	public String last_name;
	public String mail;
	public String web;
	public String phone_number;
	public String room;
	public String uid;
	//List<PersonStatus> status;
	
	public Person(String first_name, String last_name, String mail, String web, String phone_number, String room, String uid) {

		this.first_name = first_name;
		this.last_name = last_name;
		this.mail = mail;
		this.web =web;
		this.phone_number = phone_number;
		this.room = room;
		this.uid = uid;
	}
	
//	public Person(String n){
//		first_name = n;
//		
//		
//		//status = new ArrayList<PersonStatus>();
//		//status.add(new PersonStatus("etudiant", randomString() ));
//		
//	}
//	
//	public Person(){
//		
//	}
	
	

	public String toString(){
		return  first_name + " " + last_name;
	}
	
	public boolean equals(Object p){
		if(uid.compareTo( ((Person)p ).uid) == 0)
			return true;
		else
			return false;
	}
	
	public String fullInfoToString(){
		return "Name: " + first_name + " " + last_name + "\n" +
		"Mail: " + mail + "\n" +
		"Phone Numer: " + phone_number + "\n" +
		"Room: " + room + "\n" ;
	}
	

	public boolean hasMail() {
		return mail != null;
	}

	public boolean hasPhone() {
		return phone_number != null;
	}

	public boolean hasWeb() {
		return web != null;
	}

	public boolean hasOffice() {
		return room != null;
	}
	
	
}
