package com.bian.coreless.brokered.product.inbound.api.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bian.coreless.brokered.product.inbound.api.domain.BrokeredProductDataModel;
import com.bian.coreless.brokered.product.inbound.api.domain.BrokeredProductModel;
import com.bian.coreless.brokered.product.inbound.api.domain.BrokeredProductResponse;
import com.bian.coreless.brokered.product.inbound.api.domain.InitiateBrokeredProductRequest;
import com.bian.coreless.brokered.product.inbound.api.domain.InitiationModel;
import com.bian.coreless.brokered.product.inbound.api.model.BrokeredProductInformation;
import com.bian.coreless.brokered.product.inbound.api.repository.BrokeredProductInformationRepository;
import com.bian.coreless.brokered.product.outbound.api.domain.InternalBrokeredProductRequest;
import com.bian.coreless.brokered.product.outbound.api.service.ServiceProviderApiService;


@RestController
public class InboundApiController {
	
	@Autowired
	private BrokeredProductInformationRepository brokeredProductInformationRepository;
	
	@Autowired
	private ServiceProviderApiService serviceProviderApiService;
	
	@PostMapping("/brokered-product/initiate")
	public ResponseEntity<Object> initiateBrokeredProductCreation(@RequestBody InitiateBrokeredProductRequest initiateBrokeredProductRequest) {
		InitiationModel initiation = initiateBrokeredProductRequest.getData().getBrokeredProduct();
		BrokeredProductInformation brokeredProductInformation = new BrokeredProductInformation();
        UUID uuid = UUID.randomUUID();
		brokeredProductInformation.setBrokeredProductId(uuid.toString());
		brokeredProductInformation.setConsentedAccount(initiation.getAspspAccountId());
		brokeredProductInformation.setAccessToken(initiation.getAccessToken());
		brokeredProductInformation.setAspspPartyId(initiation.getAspspPartyId());
		brokeredProductInformation.setTppPartyId(initiation.getTppPartyId());
		brokeredProductInformation.setPsuPartyId(initiation.getPsuPartyId());
		brokeredProductInformation.setStatus("NEW");
		brokeredProductInformationRepository.save(brokeredProductInformation);
		
		try {
			InternalBrokeredProductRequest internalBrokeredProductRequest = new InternalBrokeredProductRequest();
			internalBrokeredProductRequest.setAccessToken(initiation.getAccessToken());
			internalBrokeredProductRequest.setAccountIdentifier(initiation.getAspspAccountId());
			internalBrokeredProductRequest.setBrokeredProductId(uuid.toString());
			serviceProviderApiService.fetchAccountInformationService(internalBrokeredProductRequest);
		} catch (IOException e) {
			new ResponseEntity<>("FAILURE", HttpStatus.OK);
		}
		
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	@GetMapping("/brokered-product/{brokeredProductId}")
	public ResponseEntity<Object> getBrokeredProductInfo(@PathVariable String brokeredProductId) {
		BrokeredProductInformation brokeredProductInformation = brokeredProductInformationRepository.findItemByBrokeredProductId(brokeredProductId);
		BrokeredProductResponse brokeredProductResponse = new BrokeredProductResponse();
		if(brokeredProductInformation != null)
		{
			BrokeredProductModel brokeredProductModel = new BrokeredProductModel();
			brokeredProductModel.setBrokeredProductId(brokeredProductInformation.getBrokeredProductId());
			brokeredProductModel.setConsentedAccount(brokeredProductInformation.getConsentedAccount());
			brokeredProductModel.setPsuPartyId(brokeredProductInformation.getPsuPartyId());
			brokeredProductModel.setTppPartyId(brokeredProductInformation.getTppPartyId());
			brokeredProductModel.setAspspPartyId(brokeredProductInformation.getAspspPartyId());
			BrokeredProductDataModel brokeredProductDataModel = new BrokeredProductDataModel();
			brokeredProductDataModel.setBrokeredProduct(brokeredProductModel);
			brokeredProductResponse.setData(brokeredProductDataModel);
		}
		return new ResponseEntity<>(brokeredProductResponse, HttpStatus.OK);
	}
}
