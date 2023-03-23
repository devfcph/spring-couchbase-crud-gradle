package com.streaming.couchbase.samplecrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class, proxyBeanMethods = false)
public class SampleCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleCrudApplication.class, args);
	}

}
