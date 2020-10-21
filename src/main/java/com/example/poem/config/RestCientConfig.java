package com.example.poem.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @author :  yuhao
 * @date: 2020/10/19
 * @description:
 */
@Configuration
public class RestCientConfig extends AbstractElasticsearchConfiguration {

    @Value("127.0.0.1:9200")
    private String uri;

    @Override
    @Bean("client")
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(uri)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
