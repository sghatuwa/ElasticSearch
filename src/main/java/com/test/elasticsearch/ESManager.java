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
            /*Path trustStorePath = Paths.get("/home/anish/es-training/elasticsearch-8.6.1/config/certs/http.p12");
            Path keyStorePath = Paths.get("/home/anish/es-training/elasticsearch-8.6.1/config/certs/http.p12");
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            try (InputStream is = Files.newInputStream(trustStorePath)) {
                trustStore.load(is, "elastic".toCharArray());
            }
            try (InputStream is = Files.newInputStream(keyStorePath)) {
                keyStore.load(is, "elastic".toCharArray());
            }
            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null)
                    .loadKeyMaterial(keyStore, "elastic".toCharArray());
            final SSLContext sslContext = sslBuilder.build();*/

            /*
                // run this command line to generate cacerts for java for default certificate of ES in local
                openssl s_client -showcerts -connect localhost:9200 </dev/null | openssl x509 -outform PEM > elasticsearch.crt
                keytool -import -file elasticsearch.crt -alias elasticsearch -keystore cacerts
             */

            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream("/home/user/es-training/elasticsearch-8.6.1/bin/cacerts"), "changeit".toCharArray()); // Replace with your actual truststore file path and password
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(truststore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

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
