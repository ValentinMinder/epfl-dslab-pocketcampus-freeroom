package org.pocketcampus.core;

import java.util.Vector;

public class Informations {
	private Vector<Information> informations_;
	private int priority_;
	
	public void setPriority(int priority) {
		priority_ = priority;
	}
	
	public int getPriority() {
		return priority_;
	}
	
	public void addInformationPiece(Information informationPiece) {
		informations_.add(informationPiece);
	}
	
	public Vector<Information> getInformationPieces() {
		return informations_;
	}
}
