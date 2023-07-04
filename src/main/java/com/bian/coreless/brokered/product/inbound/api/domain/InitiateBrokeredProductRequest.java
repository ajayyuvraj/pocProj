package com.bian.coreless.brokered.product.inbound.api.domain;

public class InitiateBrokeredProductRequest {

     private InitiateDataModel data;

	public InitiateDataModel getData() {
		return data;
	}

	public void setData(InitiateDataModel data) {
		this.data = data;
	}
}
