package br.com.leoneperdigao.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class LibraryConfiguration {

	@Bean
	public AmazonS3 buildAmazonS3() {
		return AmazonS3ClientBuilder.defaultClient();
	}
}
