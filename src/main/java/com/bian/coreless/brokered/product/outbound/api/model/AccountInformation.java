package com.bian.coreless.brokered.product.outbound.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("bp_r_account_info")
public class AccountInformation {
	 @Id
     private String id;
     private String brokeredProductId;
     private String product;
     private String account;
     private String balances;
     private String parties;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBrokeredProductId() {
		return brokeredProductId;
	}
	public void setBrokeredProductId(String brokeredProductId) {
		this.brokeredProductId = brokeredProductId;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getBalances() {
		return balances;
	}
	public void setBalances(String balances) {
		this.balances = balances;
	}
	public String getParties() {
		return parties;
	}
	public void setParties(String parties) {
		this.parties = parties;
	}
}
