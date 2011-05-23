package org.pocketcampus.plugin.camipro.elements;

/**
 * Class used to deserialize data from the camipro server
 * @author Jonas
 */
public class EbankingServer {
	private String PaidNameTo;
	private String AccountNr;
	private String BvrReference;
	private String BvrReadableReference;
	private double TotalAmount1M;
	private double TotalAmount3M;
	private double AverageAmount3M;
	private String Status;
	
	public String getPaidNameTo() {
		return PaidNameTo;
	}
	public String getAccountNr() {
		return AccountNr;
	}
	public String getBvrReference() {
		return BvrReference;
	}
	public String getBvrReadableReference() {
		return BvrReadableReference;
	}
	public double getTotalAmount1M() {
		return TotalAmount1M;
	}
	public double getTotalAmount3M() {
		return TotalAmount3M;
	}
	public double getAverageAmount3M() {
		return AverageAmount3M;
	}
	public String getStatus() {
		return Status;
	}
}
