package org.pocketcampus.shared.plugin.camipro;

/**
 * Class used to transfer ebanking informations from the server to the client
 * @author Jonas
 */
public class EbankingBean {
	private String paidNameTo;
	private String accountNr;
	private String BvrReference;
	private String BvrReferenceReadable;
	private double total1M;
	private double total3M;
	private double average3M;
	
	public EbankingBean(String paidNameTo, String accountNr,
			String bvrReference, String bvrReferenceReadable, double total1m,
			double total3m, double average3m) {

		this.paidNameTo = paidNameTo;
		this.accountNr = accountNr;
		BvrReference = bvrReference;
		BvrReferenceReadable = bvrReferenceReadable;
		total1M = total1m;
		total3M = total3m;
		average3M = average3m;
	}
	
	public String getPaidNameTo() {
		return paidNameTo;
	}

	public String getAccountNr() {
		return accountNr;
	}

	public String getBvrReference() {
		return BvrReference;
	}

	public String getBvrReferenceReadable() {
		return BvrReferenceReadable;
	}

	public double getTotal1M() {
		return total1M;
	}

	public double getTotal3M() {
		return total3M;
	}

	public double getAverage3M() {
		return average3M;
	}
}
