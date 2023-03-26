package com.test.report.esQueryReport;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AverageAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ESQueryBuilder {

    public void buildQuery(ElasticsearchClient client){
        SearchRequest.Builder builder =new SearchRequest.Builder();
        Map<String, Aggregation> map = new HashMap<>();
        Aggregation subAggregation = new Aggregation.Builder()
                .avg(new AverageAggregation.Builder().field("revenue").build())
                .build();
        Aggregation aggregation = new Aggregation.Builder()
                .terms(new TermsAggregation.Builder().field("director.keyword").size(15).build())
                .aggregations(new HashMap<>() {{
                    put("avg_renevue", subAggregation);
                }})
                .build();
        map.put("agg_director", aggregation);
        builder
                .aggregations(map)
                .size(10);
        try {
            System.out.println(builder.build());
            SearchResponse searchResponse = client.search(builder.build(), Void.class);
            System.out.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
