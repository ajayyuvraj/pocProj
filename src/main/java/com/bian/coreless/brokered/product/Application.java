package com.bian.coreless.brokered.product;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.bian.coreless.brokered.product.outbound.api.domain.BrokeredProductErrorTopic;
import com.bian.coreless.brokered.product.outbound.api.domain.BrokeredProductKafka;
import com.bian.coreless.brokered.product.outbound.api.service.ServiceProviderApiService;
import com.bian.coreless.brokered.product.util.ApiUtil;

@SpringBootApplication
@EnableAsync
public class Application {
	
	@Value("${coreless.kafka.BOOTSTRAP_SERVERS_CONFIG}")
    private String bootstapServerConfig;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
	@Bean
	public CloseableHttpClient httpClientIntance() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultHeaders(Arrays.asList(new BasicHeader(HttpHeaders.ACCEPT, "*/*"),
				new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
				new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")));
		return builder.build();
	}


	@Bean
	public ServiceProviderApiService serviceProviderApiService()
	{
		return new ServiceProviderApiService();
	}

	@Bean
	public ApiUtil apiUtil()
	{
		return new ApiUtil();
	}

	@Bean
	public ProducerFactory<String, BrokeredProductKafka>
	producerFactory()
	{
		Map<String, Object> config = new HashMap<>();

		config.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstapServerConfig);

		config.put(
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);

		config.put(
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				JsonSerializer.class);

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, BrokeredProductKafka> kafkaTemplate()
	{
		return new KafkaTemplate<>(producerFactory());
	}
	
	@Bean
	public ProducerFactory<String, BrokeredProductErrorTopic> errorProducerFactory()
	{
		Map<String, Object> config = new HashMap<>();

		config.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstapServerConfig);

		config.put(
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);

		config.put(
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				JsonSerializer.class);

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, BrokeredProductErrorTopic> kafkaErrorTemplate()
	{
		return new KafkaTemplate<>(errorProducerFactory());
	}

}
