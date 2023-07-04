package com.bian.coreless.brokered.product.inbound.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("bp_r_brokered_product_info")
public class BrokeredProductInformation {
	 @Id
     private String id;
     private String brokeredProductId;
     private String psuPartyId;
     private String tppPartyId;
     private String aspspPartyId;
     private String consentedAccount;
     private String accessToken;
     private String status;
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
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
