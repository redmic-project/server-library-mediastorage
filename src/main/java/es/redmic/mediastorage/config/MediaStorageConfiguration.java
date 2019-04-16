package es.redmic.mediastorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import es.redmic.mediastorage.service.MediaStorageS3Service;

@Configuration
public class MediaStorageConfiguration {

	@Value("${aws.access.key}")
	private String accessKey;
	@Value("${aws.secret.key}")
	private String secretKey;
	@Value("${aws.region}")
	private String region;

	@Bean(name = "mediaStorageService")
	public MediaStorageS3Service mediaStorageS3Service() {
		return new MediaStorageS3Service();
	}

	@Bean
	public AmazonS3 amazonS3Client(AWSCredentials awsCredentials) {
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(Regions.fromName(region))
				.build();

		return amazonS3Client;
	}

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
}
