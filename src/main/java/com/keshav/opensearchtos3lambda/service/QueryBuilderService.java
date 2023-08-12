package com.keshav.opensearchtos3lambda.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.util.StringUtils;
import com.keshav.opensearchtos3lambda.config.FieldMappingsConfig;

@Component
public class QueryBuilderService {
	private final Logger LOGGER = LogManager.getLogger(QueryBuilderService.class);

	@Autowired
    private FieldMappingsConfig fieldMappings;

    @SuppressWarnings("unchecked")
	public String buildQuery(Map<String, Object> queryConfig) {
    	LOGGER.info("buildQuery queryConfig: {}",queryConfig);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for (Map.Entry<String, Object> entry : queryConfig.entrySet()) {
            String fieldName = entry.getKey();
            Map<String, String> fieldConfig = (Map<String, String>) entry.getValue();

            String operator = fieldConfig.get("operator");
            String fieldValue = fieldConfig.get("fieldValue");

            String fieldType = fieldMappings.getMappings().get(fieldName); // Get field type from loaded YAML
            if(!StringUtils.isNullOrEmpty(fieldType)) {
            	boolQuery.must(QueryBuilders.wrapperQuery(buildQuery(fieldName, fieldValue, fieldType, operator)));
            }
        }
        return boolQuery.toString();
    }

    public String buildQuery(String fieldName, String fieldValue, String fieldType, String operator) {
        switch (operator) {
            case "term":
                return buildTermQuery(fieldName, fieldValue, fieldType);
            case "range":
                return buildRangeQuery(fieldName, fieldValue, fieldType);
            default:
                throw new IllegalArgumentException("Unsupported query operator: " + operator);
        }
    }

	/* Builds Term Query with fieldName and value using fieldType */
    private String buildTermQuery(String fieldName, String fieldValue, String fieldType) {
        switch (fieldType) {
            case "string":
                return QueryBuilders.termQuery(fieldName, fieldValue).toString();
            case "integer":
                return QueryBuilders.termQuery(fieldName, Integer.parseInt(fieldValue)).toString();
            case "double":
                return QueryBuilders.termQuery(fieldName, Double.parseDouble(fieldValue)).toString();
            case "boolean":
                return QueryBuilders.termQuery(fieldName, Boolean.parseBoolean(fieldValue)).toString();
            default:
                throw new IllegalArgumentException("Unsupported field type: " + fieldType);
        }
    }

	/*
	 * Date Format must be in "YYYY-MM-DD" Format example "2023-08-01" This method
	 * will add range query from provided date to current date
	 */
    private String buildRangeQuery(String fieldName, String fieldValue, String fieldType) {
        LocalDate dateValue = LocalDate.parse(fieldValue, DateTimeFormatter.ISO_DATE);
        switch (fieldType) {
            case "date":
                return QueryBuilders.rangeQuery(fieldName)
                        .from(dateValue.atStartOfDay())
                        .to(LocalDate.now())
                        .format("strict_date_optional_time")
                        .toString();
            default:
                throw new IllegalArgumentException("Unsupported field type for range query: " + fieldType);
        }
    }
}

