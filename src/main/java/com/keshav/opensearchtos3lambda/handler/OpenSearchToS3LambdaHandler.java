package com.keshav.opensearchtos3lambda.handler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keshav.opensearchtos3lambda.service.OpenSearchService;
import com.keshav.opensearchtos3lambda.service.S3Service;
import com.keshav.opensearchtos3lambda.service.beans.Movie;

public class OpenSearchToS3LambdaHandler implements RequestHandler<Object, String> {

	private final Logger LOGGER = LogManager.getLogger(OpenSearchToS3LambdaHandler.class);
	@Autowired
	private S3Service s3Service;

	@Autowired
	private OpenSearchService openSearchService;

	@Autowired
	private ObjectMapper objectMapper;

	public String handleRequest(Object input, Context context) {
		LOGGER.info("handleRequest input: {}", input);
		try {
			AnnotationConfigApplicationContext annotationAppContext = getAnnotationAppContext();
			List<Movie> data = openSearchService.fetchDataFromOpenSearch();
			s3Service.putObjectTOS3(objectMapper.writeValueAsString(data));
			return "Data successfully pulled from OpenSearch and pushed to S3.";
		} catch (Exception e) {
			LOGGER.error("handleRequest", e);
			return String.format("An error occurred: {}", e.getMessage());
		}
	}

	protected AnnotationConfigApplicationContext getAnnotationAppContext() {
		LOGGER.info("getAnnotationAppContext");
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				"com.keshav.opensearchtos3lambda");
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
		return ctx;
	}
}