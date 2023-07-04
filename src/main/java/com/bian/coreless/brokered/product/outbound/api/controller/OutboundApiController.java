package com.bian.coreless.brokered.product.outbound.api.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bian.coreless.brokered.product.outbound.api.domain.AccountInformationDataModel;
import com.bian.coreless.brokered.product.outbound.api.domain.AccountInformationResponse;
import com.bian.coreless.brokered.product.outbound.api.domain.AccountInformationSubModel;
import com.bian.coreless.brokered.product.outbound.api.domain.BrokeredProductErrorTopic;
import com.bian.coreless.brokered.product.outbound.api.domain.BrokeredProductKafka;
import com.bian.coreless.brokered.product.outbound.api.domain.BrokeredProductRecordDetail;
import com.bian.coreless.brokered.product.outbound.api.domain.InternalBrokeredProductRequest;
import com.bian.coreless.brokered.product.outbound.api.model.AccountInformation;
import com.bian.coreless.brokered.product.outbound.api.repository.AccountInformationRepository;
import com.bian.coreless.brokered.product.outbound.api.service.ServiceProviderApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class OutboundApiController {
	
    Logger logger = LoggerFactory.getLogger(OutboundApiController.class);

	@Autowired
	private ServiceProviderApiService serviceProviderApiService;
	
	@Autowired
    private KafkaTemplate<String, BrokeredProductKafka> kafkaTemplate;
	
	@Autowired
    private KafkaTemplate<String, BrokeredProductErrorTopic> kafkaErrorTemplate;
	
	@Autowired
	private AccountInformationRepository accountInformationRepository;
	
	@PostMapping("/brokered-product/accountInformation/retrieve")
	public ResponseEntity<Object> fetchAccountInformation(@RequestBody InternalBrokeredProductRequest internalBrokeredProductRequest){
			AccountInformation accountInformation = new AccountInformation();
			accountInformation.setBrokeredProductId(internalBrokeredProductRequest.getBrokeredProductId());
			try 
			{
				accountInformation = getAccountProductDetailWrapper(accountInformation,internalBrokeredProductRequest);
				accountInformation = getAccountDetailWrapper(accountInformation,internalBrokeredProductRequest);
				accountInformation = getAccountBalancesWrapper(accountInformation,internalBrokeredProductRequest);
				accountInformation = getAccountPartyWrapper(accountInformation,internalBrokeredProductRequest);
				accountInformationRepository.save(accountInformation);
				
				BrokeredProductKafka brokeredProductKafka = new BrokeredProductKafka();
				BrokeredProductRecordDetail brokeredProductRecordDetail = new BrokeredProductRecordDetail();
				brokeredProductRecordDetail.setId(internalBrokeredProductRequest.getBrokeredProductId());
				brokeredProductRecordDetail.setAccountId(internalBrokeredProductRequest.getAccountIdentifier());
				brokeredProductKafka.setBrokeredProduct(brokeredProductRecordDetail);
//				kafkaTemplate.send("BrokeredProduct",brokeredProductKafka);
			}
			catch (Exception e) {
				BrokeredProductErrorTopic brokeredProductErrorTopic = new BrokeredProductErrorTopic();
				brokeredProductErrorTopic.setErrorMsg(e.getMessage());
				brokeredProductErrorTopic.setBrokeredProductId(internalBrokeredProductRequest.getBrokeredProductId());
//				kafkaErrorTemplate.send("BrokeredProductErrorTopic",brokeredProductErrorTopic);
			}
			
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	@GetMapping("/brokered-product/{brokeredProductId}/accountInformation")
	public ResponseEntity<Object> getAccountInformation(@PathVariable String brokeredProductId) {
		AccountInformation accountInformation = accountInformationRepository.findItemByBrokeredProductId(brokeredProductId);
		AccountInformationResponse accountInformationResponse = new AccountInformationResponse();
		if(accountInformation != null)
		{
			AccountInformationSubModel accountInformationSubModel = new AccountInformationSubModel();
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<Map<String, Object>> productMap = null;
			try {
				productMap = mapper.readValue(accountInformation.getProduct(),
						new TypeReference<ArrayList<Map<String, Object>>>(){});
			} catch (JsonMappingException e) {
				logger.debug(e.getMessage());
			} catch (JsonProcessingException e) {
				logger.debug(e.getMessage());
			}
			
			Map<String, Object> productTempMap = productMap.get(0);
			Map<String, Object> productResultMap = new HashMap<>();
			productResultMap.put("accountId", productTempMap.get("AccountId"));
			productResultMap.put("id", productTempMap.get("ProductId"));
			productResultMap.put("name", productTempMap.get("ProductName"));
			productResultMap.put("type", productTempMap.get("ProductType"));
			productResultMap.put("marketingStateId", productTempMap.get("MarketingStateId"));
			accountInformationSubModel.setProduct(productResultMap);

			ArrayList<Map<String, Object>> accountMap = null;
			try {
				accountMap = mapper.readValue(accountInformation.getAccount(),
						new TypeReference<ArrayList<Map<String, Object>>>(){});
			} catch (JsonMappingException e) {
				logger.debug(e.getMessage());
			} catch (JsonProcessingException e) {
				logger.debug(e.getMessage());
			}
			accountInformationSubModel.setAccount(accountMap.get(0));
			
			
			ArrayList<Object> balancesMap = null;
			try {
				balancesMap = mapper.readValue(accountInformation.getBalances(),
						new TypeReference<ArrayList<Object>>(){});
			} catch (JsonMappingException e) {
				logger.debug(e.getMessage());
			} catch (JsonProcessingException e) {
				logger.debug(e.getMessage());
			}
			accountInformationSubModel.setBalances(balancesMap);

			ArrayList<Object> partiesMap = null;
			try {
				partiesMap = mapper.readValue(accountInformation.getParties(),
						new TypeReference<ArrayList<Object>>(){});
			} catch (JsonMappingException e) {
				logger.debug(e.getMessage());
			} catch (JsonProcessingException e) {
				logger.debug(e.getMessage());
			}
			accountInformationSubModel.setParties(partiesMap);


			AccountInformationDataModel accountInformationDataModel = new AccountInformationDataModel();
			accountInformationDataModel.setAccountInformation(accountInformationSubModel);
			accountInformationResponse.setData(accountInformationDataModel);
		}
		return new ResponseEntity<>(accountInformationResponse, HttpStatus.OK);
	}
	
	
	private AccountInformation getAccountProductDetailWrapper(AccountInformation accountInformation,InternalBrokeredProductRequest internalBrokeredProductRequest) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, URISyntaxException, IOException
	{
		Map<String, Object> dataResponse = serviceProviderApiService.getAccountProductDetail(internalBrokeredProductRequest);
		
		if(dataResponse != null)
		{
			Object productData = dataResponse.get("Data");
	        ObjectMapper oMapper = new ObjectMapper();
	        Map<String, Object> map = oMapper.convertValue(productData, Map.class);
			String json = new ObjectMapper().writeValueAsString(map.get("Product"));
			accountInformation.setProduct(json);
		}
		
		return accountInformation;
	}
	
	private AccountInformation getAccountDetailWrapper(AccountInformation accountInformation,InternalBrokeredProductRequest internalBrokeredProductRequest) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, URISyntaxException, IOException
	{
		Map<String, Object> accountResponse = serviceProviderApiService.getAccountDetail(internalBrokeredProductRequest);
		if(accountResponse != null)
		{
			Object accountData = accountResponse.get("Data");
	        ObjectMapper oMapper = new ObjectMapper();
	        Map<String, Object> map = oMapper.convertValue(accountData, Map.class);
			String json = new ObjectMapper().writeValueAsString(map.get("Account"));
			accountInformation.setAccount(json);
		}
		return accountInformation;
	}
	
	private AccountInformation getAccountBalancesWrapper(AccountInformation accountInformation,InternalBrokeredProductRequest internalBrokeredProductRequest) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, URISyntaxException, IOException
	{
		Map<String, Object> balanceResponse = serviceProviderApiService.getAccountBalances(internalBrokeredProductRequest);
		if(balanceResponse != null)
		{
			Object balanceData = balanceResponse.get("Data");
	        ObjectMapper oMapper = new ObjectMapper();
	        Map<String, Object> map = oMapper.convertValue(balanceData, Map.class);
			String json = new ObjectMapper().writeValueAsString(map.get("Balance"));
			accountInformation.setBalances(json);
		}
		return accountInformation;
	}
	
	private AccountInformation getAccountPartyWrapper(AccountInformation accountInformation,InternalBrokeredProductRequest internalBrokeredProductRequest) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, URISyntaxException, IOException
	{
		Map<String, Object> partyResponse = serviceProviderApiService.getAccountParty(internalBrokeredProductRequest);
		if(partyResponse != null)
		{
			Object partyData = partyResponse.get("Data");
	        ObjectMapper oMapper = new ObjectMapper();
	        Map<String, Object> map = oMapper.convertValue(partyData, Map.class);
			String json = new ObjectMapper().writeValueAsString(map.get("Party"));
			accountInformation.setParties(json);
		}
		return accountInformation;
	}
	
}
