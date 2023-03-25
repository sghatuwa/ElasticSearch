package com.test.report.esQueryReport;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.io.IOException;

public class ESQueryBuilder {

    public void buildQuery(ElasticsearchClient client){
        SearchRequest.Builder builder =new SearchRequest.Builder();
        builder
                .size(10);
        try {
            SearchResponse searchResponse = client.search(builder.build(), Void.class);
            System.out.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
