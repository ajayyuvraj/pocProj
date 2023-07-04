package com.bian.coreless.brokered.product.outbound.api.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;

import com.bian.coreless.brokered.product.outbound.api.domain.InternalBrokeredProductRequest;
import com.bian.coreless.brokered.product.util.ApiUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceProviderApiService {

	@Value("${coreless.api.baseUrl}")
    private String baseUrl;
	
	@Value("${coreless.api.serviceProvider.baseUrl}")
    private String spBaseUrl;
	
	@Value("${coreless.api.cert.pass}")
    private String keyPhrase;
	
	@Autowired
	CloseableHttpClient httpClientIntance;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	ApiUtil apiUtil;

	@Value("${my.resource.path:#{null}}")
	private Resource myResource;

	ObjectMapper mapper = new ObjectMapper();

	public ServiceProviderApiService() {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	private static final Logger log = LoggerFactory.getLogger(ServiceProviderApiService.class);


	@Async
	public String fetchAccountInformationService(InternalBrokeredProductRequest internalBrokeredProductRequest) throws IOException {
		String requestJson = mapper.writeValueAsString(internalBrokeredProductRequest);
		String status = "FAILURE";
		try {
			String url = baseUrl+"/brokered-product/accountInformation/retrieve";

			HttpPost post = apiUtil.intializeHttpPost(url);

			post.setEntity(new StringEntity(requestJson));
			int statusCode = 0;

			try (CloseableHttpResponse response = httpClientIntance.execute(post)) {
				statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					status = "SUCCESS";
				}

			} catch (Exception e) {
				throw e;
			} finally {
				post.releaseConnection();
			}
		} catch (IOException e) {
			throw e;
		}

		return status;
	}

	public Map<String, Object> getAccountProductDetail(InternalBrokeredProductRequest internalBrokeredProductRequest) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
		Map<String, Object> mapFromString = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(this.getClass().getClassLoader().getResourceAsStream("bianClient.jks"), keyPhrase.toCharArray());

			SSLContext sslContext;
			CloseableHttpClient httpclient = null;
			try {
				sslContext = SSLContexts.custom()
						.loadKeyMaterial(keyStore , keyPassphrase.toCharArray())
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				
				SSLConnectionSocketFactory sslConnectionFactory =
						new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				
				httpclient = HttpClients.custom()
						.setSSLSocketFactory(sslConnectionFactory)            .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
				
			} catch (KeyManagementException e1) {
				System.out.println("KeyManagementException = "+e1.getMessage());
			} catch (UnrecoverableKeyException e1) {
				System.out.println("UnrecoverableKeyException = "+e1.getMessage());
			} catch (NoSuchAlgorithmException e1) {
				System.out.println("NoSuchAlgorithmException = "+e1.getMessage());
			} catch (KeyStoreException e1) {
				System.out.println("KeyStoreException = "+e1.getMessage());

			}

			String url = spBaseUrl
					+ "/obie/open-banking/v3.1/aisp/accounts"
					+ "/"+internalBrokeredProductRequest.getAccountIdentifier()+"/product";
			List<NameValuePair> getParams = new ArrayList<>();
			HttpGet request = apiUtil.intializeHttpGet(url, getParams);
			request.addHeader("Authorization",
					"Bearer "
							+ internalBrokeredProductRequest.getAccessToken());
			request.addHeader("x-fapi-financial-id", "dummy-fapi");
			request.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response = httpclient.execute(request)) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200)
				{
					try {
					    mapFromString = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
					        });
					} catch (IOException e) {
						System.out.println("Error Msg = "+e.getMessage());
					}
				}
				

			} catch (Exception e) {
				System.out.println("Error Msg = "+e.getMessage());
			} finally {
				request.releaseConnection();
			}
			return mapFromString;
		} catch (IOException e) {
			return mapFromString;

		}
	}
	
	public Map<String, Object> getAccountDetail(InternalBrokeredProductRequest internalBrokeredProductRequest) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
		Map<String, Object> mapFromString = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(this.getClass().getClassLoader().getResourceAsStream("bianClient.jks"), keyPhrase.toCharArray());

			SSLContext sslContext;
			CloseableHttpClient httpclient = null;
			try {
				sslContext = SSLContexts.custom()
						.loadKeyMaterial(keyStore , keyPassphrase.toCharArray())
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				
				SSLConnectionSocketFactory sslConnectionFactory =
						new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				
				httpclient = HttpClients.custom()
						.setSSLSocketFactory(sslConnectionFactory)            .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
				
			} catch (KeyManagementException e1) {
				System.out.println("KeyManagementException = "+e1.getMessage());
			} catch (UnrecoverableKeyException e1) {
				System.out.println("UnrecoverableKeyException = "+e1.getMessage());
			} catch (NoSuchAlgorithmException e1) {
				System.out.println("NoSuchAlgorithmException = "+e1.getMessage());
			} catch (KeyStoreException e1) {
				System.out.println("KeyStoreException = "+e1.getMessage());

			}

			String url = spBaseUrl
					+ "/obie/open-banking/v3.1/aisp/accounts"
					+ "/"+internalBrokeredProductRequest.getAccountIdentifier();
			List<NameValuePair> getParams = new ArrayList<>();
			HttpGet request = apiUtil.intializeHttpGet(url, getParams);
			request.addHeader("Authorization",
					"Bearer "
							+ internalBrokeredProductRequest.getAccessToken());
			request.addHeader("x-fapi-financial-id", "dummy-fapi");
			request.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response = httpclient.execute(request)) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200)
				{
					try {
					    mapFromString = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
					        });
					} catch (IOException e) {
						System.out.println("Error Msg = "+e.getMessage());
					}
				}
				

			} catch (Exception e) {
				System.out.println("Error Msg = "+e.getMessage());
			} finally {
				request.releaseConnection();
			}
			return mapFromString;
		} catch (IOException e) {
			return mapFromString;

		}
	}
	
	public Map<String, Object> getAccountBalances(InternalBrokeredProductRequest internalBrokeredProductRequest) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
		Map<String, Object> mapFromString = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(this.getClass().getClassLoader().getResourceAsStream("bianClient.jks"), keyPhrase.toCharArray());

			SSLContext sslContext;
			CloseableHttpClient httpclient = null;
			try {
				sslContext = SSLContexts.custom()
						.loadKeyMaterial(keyStore , keyPassphrase.toCharArray())
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				
				SSLConnectionSocketFactory sslConnectionFactory =
						new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				
				httpclient = HttpClients.custom()
						.setSSLSocketFactory(sslConnectionFactory)            .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
				
			} catch (KeyManagementException e1) {
				System.out.println("KeyManagementException = "+e1.getMessage());
			} catch (UnrecoverableKeyException e1) {
				System.out.println("UnrecoverableKeyException = "+e1.getMessage());
			} catch (NoSuchAlgorithmException e1) {
				System.out.println("NoSuchAlgorithmException = "+e1.getMessage());
			} catch (KeyStoreException e1) {
				System.out.println("KeyStoreException = "+e1.getMessage());

			}

			String url = spBaseUrl
					+ "/obie/open-banking/v3.1/aisp/accounts"
					+ "/"+internalBrokeredProductRequest.getAccountIdentifier()
					+ "/balances";
			List<NameValuePair> getParams = new ArrayList<>();
			HttpGet request = apiUtil.intializeHttpGet(url, getParams);
			request.addHeader("Authorization",
					"Bearer "
							+ internalBrokeredProductRequest.getAccessToken());
			request.addHeader("x-fapi-financial-id", "dummy-fapi");
			request.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response = httpclient.execute(request)) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200)
				{
					try {
					    mapFromString = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
					        });
					} catch (IOException e) {
						System.out.println("Error Msg = "+e.getMessage());
					}
				}
				

			} catch (Exception e) {
				System.out.println("Error Msg = "+e.getMessage());
			} finally {
				request.releaseConnection();
			}
			return mapFromString;
		} catch (IOException e) {
			return mapFromString;

		}
	}
	
	public Map<String, Object> getAccountParty(InternalBrokeredProductRequest internalBrokeredProductRequest) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
		Map<String, Object> mapFromString = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(this.getClass().getClassLoader().getResourceAsStream("bianClient.jks"), keyPhrase.toCharArray());

			SSLContext sslContext;
			CloseableHttpClient httpclient = null;
			try {
				sslContext = SSLContexts.custom()
						.loadKeyMaterial(keyStore , keyPassphrase.toCharArray())
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				
				SSLConnectionSocketFactory sslConnectionFactory =
						new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				
				httpclient = HttpClients.custom()
						.setSSLSocketFactory(sslConnectionFactory)            .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
				
			} catch (KeyManagementException e1) {
				System.out.println("KeyManagementException = "+e1.getMessage());
			} catch (UnrecoverableKeyException e1) {
				System.out.println("UnrecoverableKeyException = "+e1.getMessage());
			} catch (NoSuchAlgorithmException e1) {
				System.out.println("NoSuchAlgorithmException = "+e1.getMessage());
			} catch (KeyStoreException e1) {
				System.out.println("KeyStoreException = "+e1.getMessage());

			}

			String url = spBaseUrl
					+ "/obie/open-banking/v3.1/aisp/accounts"
					+ "/"+internalBrokeredProductRequest.getAccountIdentifier()
					+ "/parties";
			List<NameValuePair> getParams = new ArrayList<>();
			HttpGet request = apiUtil.intializeHttpGet(url, getParams);
			request.addHeader("Authorization",
					"Bearer "
							+ internalBrokeredProductRequest.getAccessToken());
			request.addHeader("x-fapi-financial-id", "dummy-fapi");
			request.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response = httpclient.execute(request)) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200)
				{
					try {
					    mapFromString = mapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
					        });
					} catch (IOException e) {
						System.out.println("Error Msg = "+e.getMessage());
					}
				}
				

			} catch (Exception e) {
				System.out.println("Error Msg = "+e.getMessage());
			} finally {
				request.releaseConnection();
			}
			return mapFromString;
		} catch (IOException e) {
			return mapFromString;

		}
	}

}
