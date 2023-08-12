package com.keshav.opensearchtos3lambda.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.keshav.opensearchtos3lambda.service.beans.Movie;

@Service
public class OpenSearchService {

	private final Logger LOGGER = LogManager.getLogger(OpenSearchService.class);

	@Value("${aws.opensearch.index.name:movies}")
	private String indexName;

	@Autowired
	private OpenSearchClient openSearchClient;

	public List<Movie> fetchDataFromOpenSearch() throws InterruptedException, IOException {
		LOGGER.info("fetchDataFromOpenSearch");
		/*
		 * // create the index CreateIndexRequest createIndexRequest = new
		 * CreateIndexRequest.Builder().index(indexName).build();
		 * 
		 * try { openSearchClient.indices().create(createIndexRequest);
		 * 
		 * // add settings to the index IndexSettings indexSettings = new
		 * IndexSettings.Builder().build(); PutIndicesSettingsRequest putSettingsRequest
		 * = new PutIndicesSettingsRequest.Builder().index(indexName)
		 * .settings(indexSettings).build();
		 * openSearchClient.indices().putSettings(putSettingsRequest); } catch
		 * (OpenSearchException ex) { final String errorType =
		 * Objects.requireNonNull(ex.response().error().type()); if
		 * (!errorType.equals("resource_already_exists_exception")) {
		 * LOGGER.error("Error while creating index", ex); } }
		 * 
		 * // index data Movie movie = new Movie("Bennett Miller", "Moneyball", 2011);
		 * Movie movie2 = new Movie("Ron Howard", "Rush", 2000); IndexRequest<Movie>
		 * indexRequest = new
		 * IndexRequest.Builder<Movie>().index(indexName).id("1").document(movie)
		 * .build(); IndexRequest<Movie> indexRequest2 = new
		 * IndexRequest.Builder<Movie>().index(indexName).id("2").document(movie2)
		 * .build(); openSearchClient.index(indexRequest);
		 * openSearchClient.index(indexRequest2);
		 * 
		 * // wait for the document to index Thread.sleep(3000);
		 */

		// search for the document
		SearchResponse<Movie> searchResponse = openSearchClient.search(s -> s.index(indexName), Movie.class);
		/*
		 * for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
		 * LOGGER.info(searchResponse.hits().hits().get(i).source().toString()); }
		 */
		return searchResponse.documents();
	}

}