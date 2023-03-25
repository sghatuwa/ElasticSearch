package com.test.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

public class ESManager {
    private static ElasticsearchClient client = null;
    private static ElasticsearchAsyncClient asyncClient = null;

    public ElasticsearchClient initClient() {
        try {
            Path trustStorePath = Paths.get("/Users/suresh/Documents/elasticsearch-8.5.3/config/certs/elastic-certificates.p12");
            Path keyStorePath = Paths.get("/Users/suresh/Documents/elasticsearch-8.5.3/config/certs/elastic-certificates.p12");
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            try (InputStream is = Files.newInputStream(trustStorePath)) {
                trustStore.load(is, "1234".toCharArray());
            }
            try (InputStream is = Files.newInputStream(keyStorePath)) {
                keyStore.load(is, "1234".toCharArray());
            }
            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null)
                    .loadKeyMaterial(keyStore, "1234".toCharArray());
            final SSLContext sslContext = sslBuilder.build();

            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "*eilAqwrt-Vh6w4YUBxs"));

            RestClientBuilder builder = RestClient.builder(
                            new HttpHost("localhost", 9200, "https"))
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                                    .setSSLContext(sslContext)
                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                        }
                    });

            RestClient restClient = builder.build();

            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

            // And create the API client
            client = new ElasticsearchClient(transport);
            asyncClient = new ElasticsearchAsyncClient(transport);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ES Error");
        }
        return client;
    }
}
