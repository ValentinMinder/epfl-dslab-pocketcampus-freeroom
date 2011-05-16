package org.pocketcampus.shared.plugin.camipro;

import java.util.Date;

/**
 * Class used to transfer a list of transactions from the server to the client
 *  
 * @author Jonas
 *
 */
public class TransactionBean {
	
	private String type_;
	private String description_;
	private Date date_;
	private float amount_;
	
	public TransactionBean(String type, String description, Date date, float amount) {
		this.type_ = type;
		this.description_ = description;
		this.date_ = date;
		this.amount_ = amount;
	}
	
	public String getType() {
		return type_;
	}
	public void setType(String type) {
		this.type_ = type;
	}
	public String getDescription() {
		return description_;
	}
	public void setDescription(String description) {
		this.description_ = description;
	}
	public Date getDate() {
		return date_;
	}
	public void setDate(Date date) {
		this.date_ = date;
	}
	public float getAmount() {
		return amount_;
	}
	public void setAmount(float amount) {
		this.amount_ = amount;
	}
	
	
}
