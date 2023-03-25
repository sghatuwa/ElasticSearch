package com.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.test.elasticsearch.ESManager;
import com.test.report.esQueryReport.ESQueryBuilder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ElasticsearchClient client = new ESManager().initClient();
        try {
//            System.out.println(client.sear);
            System.out.println(client.cluster().health().status().jsonValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ESQueryBuilder().buildQuery(client);
    }
}
