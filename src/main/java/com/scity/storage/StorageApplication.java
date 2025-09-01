package com.scity.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.scity.storage.*"})
public class StorageApplication {

  public static void main(String[] args) {
    System.setProperty("spring.devtools.restart.enabled", "false"); // Work around for cache
    SpringApplication.run(StorageApplication.class, args);
  }
}
