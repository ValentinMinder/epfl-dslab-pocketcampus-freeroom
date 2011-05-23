package org.pocketcampus.shared.plugin.camipro;

import java.util.Date;

/**
 * Class used to transfer a list of transactions from the server to the client
 * @author Jonas
 */
public class TransactionBean {
	
	private String type;
	private String description;
	private Date date;
	private float amount;
	
	public TransactionBean(String type, String description, Date date, float amount) {
		this.type = type;
		this.description = description;
		this.date = date;
		this.amount = amount;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
}
