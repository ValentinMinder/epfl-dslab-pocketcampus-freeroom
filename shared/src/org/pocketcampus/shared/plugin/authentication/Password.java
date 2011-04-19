package org.pocketcampus.shared.plugin.authentication;

import java.io.Serializable;

public class Password implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8136759257313615887L;

	private String salt = "Hf_O--:-->_c#Q7G_7<XEM^__~Y8q`.b";
	
	private String password;
	private Encryption state;
	
	public Password(String password, Encryption state) {
		this.password = password;
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see org.pocketcampus.shared.accounts.interfaces.IPassword#equals(java.lang.String)
	 */
	public boolean equals(String s) {
		return this.password.equals(s);
	}
	
	public int hashCode() {
		return this.password.hashCode() + this.state.hashCode() + this.salt.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.pocketcampus.shared.accounts.interfaces.IPassword#getPassword()
	 */
	public String getPassword() {
		return this.password;
	}

	/* (non-Javadoc)
	 * @see org.pocketcampus.shared.accounts.interfaces.IPassword#getState()
	 */
	public Encryption getState() {
		return this.state;
	}
	
	public String toString() {
		return "*******";
	}
}
