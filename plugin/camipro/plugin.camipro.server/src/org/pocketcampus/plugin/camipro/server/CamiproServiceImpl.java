package org.pocketcampus.plugin.camipro.server;

import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.camipro.shared.CamiproService;
import org.pocketcampus.plugin.camipro.shared.EbankingBean;
import org.pocketcampus.plugin.camipro.shared.Transaction;

public class CamiproServiceImpl implements CamiproService.Iface {

	@Override
	public double getBalance() throws TException {
		// TODO Auto-generated method stub
		System.out.println("getBalance called");
		return 12;
	}

	@Override
	public List<Transaction> getTransactions() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EbankingBean getEbankingBean() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
