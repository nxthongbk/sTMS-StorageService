package com.scity.storage.config;

import com.scity.storage.interceptor.RequestHeaders;
import com.scity.storage.interceptor.SessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonBeanInitConfig {

  @Autowired
  private RequestHeaders requestHeaders;
//  @Autowired
//  private CassandraOperations cassandraOperations;
//  @Autowired
//  private ElasticsearchClient elasticsearchClient;
//  @Autowired
//  private RestHighLevelClient restHighLevelClient;

  @Bean
  public SessionHelper sessionHelper() {
    return new SessionHelper(requestHeaders);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.build();
//    restTemplate.getInterceptors().add(new RestTemplateRequestInterceptor());
    return restTemplate;
  }

//  @Bean
//  CacheService cacheService() {
//    return new CacheService();
//  }

//  @Bean
//  public CassandraSearchEngine cassandraSearchEngine() {
//    return new CassandraSearchEngine(cassandraOperations,
//        new ElasticsearchTemplate(elasticsearchClient), restHighLevelClient);
//  }
}
