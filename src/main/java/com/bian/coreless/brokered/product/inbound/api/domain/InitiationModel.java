package com.bian.coreless.brokered.product.inbound.api.domain;

public class InitiationModel {
     private String aspspPartyId;
     private String tppPartyId;
     private String psuPartyId;
     private String aspspAccountId;
     private String accessToken;
	public String getAspspPartyId() {
		return aspspPartyId;
	}
	public void setAspspPartyId(String aspspPartyId) {
		this.aspspPartyId = aspspPartyId;
	}
	public String getTppPartyId() {
		return tppPartyId;
	}
	public void setTppPartyId(String tppPartyId) {
		this.tppPartyId = tppPartyId;
	}
	public String getPsuPartyId() {
		return psuPartyId;
	}
	public void setPsuPartyId(String psuPartyId) {
		this.psuPartyId = psuPartyId;
	}
	public String getAspspAccountId() {
		return aspspAccountId;
	}
	public void setAspspAccountId(String aspspAccountId) {
		this.aspspAccountId = aspspAccountId;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
