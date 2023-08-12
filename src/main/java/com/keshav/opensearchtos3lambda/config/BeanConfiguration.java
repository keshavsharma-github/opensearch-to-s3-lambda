package com.keshav.opensearchtos3lambda.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.InfoResponse;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class BeanConfiguration {

	private final Logger LOGGER = LogManager.getLogger(BeanConfiguration.class);

	@Value("${aws.opensearch.domain.endpoint:search-search-movies-demo-cdrfg7gi2z5vs2wluyx4y2fdge.us-east-1.es.amazonaws.com}")
    private String endpoint; // AWS OpenSearch Endpoint
    @Value("${aws.region:us-east-1}")
    private String region; // AWS region
    @Value("${aws.service:es}")
    private String service;

	@Bean
	public AmazonS3 amazonS3Client() {
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).build();
	}

	@Bean
	public OpenSearchClient openSearchClient() {
		SdkHttpClient httpClient = ApacheHttpClient.builder().build();
		OpenSearchClient client = new OpenSearchClient(
                new AwsSdk2Transport(
                        httpClient,
                        HttpHost.create(endpoint).getHostName(),
                        service,
                        Region.of(region),
                        AwsSdk2TransportOptions.builder().build()));
		try {
			InfoResponse info = client.info();
			LOGGER.info("Distribution {}: {}", info.version().distribution(), info.version().number());
		} catch (OpenSearchException | IOException e) {
			LOGGER.error("Error while creating openSearchClient", e);
		}
		return client;
	}

	@Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}