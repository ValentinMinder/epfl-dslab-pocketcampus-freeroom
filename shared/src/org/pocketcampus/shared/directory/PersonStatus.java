package org.pocketcampus.shared.directory;

import java.io.Serializable;

public class PersonStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7092979080492404055L;
	String function;
	String inUnit;
	
	public PersonStatus (String funcion_, String inUnit_)
	{
		this.function = funcion_;
		this.inUnit = inUnit_;
	}

}
