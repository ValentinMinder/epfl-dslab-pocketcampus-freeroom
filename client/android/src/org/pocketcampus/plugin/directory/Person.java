package org.pocketcampus.plugin.directory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Person {
	String first_name;
	String last_name;
	String mail;
	URL web;
	String phone_number;
	String room;
	List<PersonStatus> status;
	
	public Person(String first_name, String last_name, String mail, String web, String phone_number, String room, List<PersonStatus> status) {

		this.first_name = first_name;
		this.last_name = last_name;
		this.mail = mail;
		try {
			this.web = new URL (randomString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
		this.phone_number = phone_number;
		this.room = room;
		this.status = status;
	}
	
	public Person(String n){
		first_name = n;
		
		try {
			web = new URL (randomString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
		}
		
		status = new ArrayList<PersonStatus>();
		status.add(new PersonStatus("etudiant", randomString() ));
		
	}
	
	

	public String toString(){
		return  first_name + " " + last_name;
	}
	
	public String fullInfoToString(){
		return "Name: " + first_name + " " + last_name + "\n" +
		"Mail: " + mail + "\n" +
		"Phone Numer: " + phone_number + "\n" +
		"Room: " + room + "\n" ;
	}
	
	private String randomString(){
		return "no random value";
	}
}
