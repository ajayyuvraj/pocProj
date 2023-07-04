package com.bian.coreless.brokered.product.outbound.api.domain;

import java.util.ArrayList;
import java.util.Map;

public class AccountInformationSubModel {
     private String bankingStandard;
     private Map<String, Object> product;
     private Map<String, Object> account;
     private ArrayList<Object> balances;
     private ArrayList<Object> parties;
	public String getBankingStandard() {
		return bankingStandard;
	}
	public void setBankingStandard(String bankingStandard) {
		this.bankingStandard = bankingStandard;
	}
	public Map<String, Object> getProduct() {
		return product;
	}
	public void setProduct(Map<String, Object> product) {
		this.product = product;
	}
	public Map<String, Object> getAccount() {
		return account;
	}
	public void setAccount(Map<String, Object> account) {
		this.account = account;
	}
	public ArrayList<Object> getBalances() {
		return balances;
	}
	public void setBalances(ArrayList<Object> balances) {
		this.balances = balances;
	}
	public ArrayList<Object> getParties() {
		return parties;
	}
	public void setParties(ArrayList<Object> parties) {
		this.parties = parties;
	}
}
