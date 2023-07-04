package com.bian.coreless.brokered.product.outbound.api.domain;

public class InternalBrokeredProductRequest {
	private String accessToken;
    private String accountIdentifier;
    private String brokeredProductId;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getAccountIdentifier() {
		return accountIdentifier;
	}
	public void setAccountIdentifier(String accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}
	public String getBrokeredProductId() {
		return brokeredProductId;
	}
	public void setBrokeredProductId(String brokeredProductId) {
		this.brokeredProductId = brokeredProductId;
	}
}
