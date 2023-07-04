package com.bian.coreless.brokered.product.inbound.api.domain;

public class BrokeredProductModel {
     private String brokeredProductId;
     private String psuPartyId;
     private String tppPartyId;
     private String aspspPartyId;
     private String consentedAccount;
	public String getBrokeredProductId() {
		return brokeredProductId;
	}
	public void setBrokeredProductId(String brokeredProductId) {
		this.brokeredProductId = brokeredProductId;
	}
	public String getPsuPartyId() {
		return psuPartyId;
	}
	public void setPsuPartyId(String psuPartyId) {
		this.psuPartyId = psuPartyId;
	}
	public String getTppPartyId() {
		return tppPartyId;
	}
	public void setTppPartyId(String tppPartyId) {
		this.tppPartyId = tppPartyId;
	}
	public String getAspspPartyId() {
		return aspspPartyId;
	}
	public void setAspspPartyId(String aspspPartyId) {
		this.aspspPartyId = aspspPartyId;
	}
	public String getConsentedAccount() {
		return consentedAccount;
	}
	public void setConsentedAccount(String consentedAccount) {
		this.consentedAccount = consentedAccount;
	}
}
