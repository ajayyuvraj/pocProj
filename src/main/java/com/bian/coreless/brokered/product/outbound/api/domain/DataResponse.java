package com.bian.coreless.brokered.product.outbound.api.domain;

import java.util.Map;

public class DataResponse {
	private Map<String, Object> Data;

	public Map<String, Object> getData() {
		return Data;
	}

	public void setData(Map<String, Object> data) {
		Data = data;
	}
}
