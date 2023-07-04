package com.bian.coreless.brokered.product.outbound.api.domain;

public class BrokeredProductErrorTopic {

	private String errorMsg;
	private String brokeredProductId;
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getBrokeredProductId() {
		return brokeredProductId;
	}
	public void setBrokeredProductId(String brokeredProductId) {
		this.brokeredProductId = brokeredProductId;
	}
    
}
