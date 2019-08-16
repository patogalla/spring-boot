package com.patogalla.api.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;

import java.util.Collections;

@Configuration
public class AwsConfig {

    @Bean
    public ClientConfiguration clientConfiguration() {
        return new ClientConfiguration();
    }

    @Bean("amazonSimpleEmailService")
    public AmazonSimpleEmailService sesClient(final AWSCredentialsProvider awsCredentialsProvider, final RegionProvider regionProvider) {
        AmazonSimpleEmailServiceClientBuilder builder = AmazonSimpleEmailServiceClient.builder();
        builder.setCredentials(awsCredentialsProvider);
        builder.setClientConfiguration(clientConfiguration());
        builder.setRegion(regionProvider.getRegion().getName());
        return builder.build();
    }

    @Bean("amazonS3")
    public AmazonS3 s3Client(final AWSCredentialsProvider awsCredentialsProvider, final RegionProvider regionProvider) {
        AmazonS3ClientBuilder builder = AmazonS3Client.builder();
        builder.setCredentials(awsCredentialsProvider);
        builder.setClientConfiguration(clientConfiguration());
        builder.setRegion(regionProvider.getRegion().getName());
        return builder.build();
    }

    @Bean(name = "amazonSqs", destroyMethod = "shutdown")
    public AmazonSQSAsync amazonSQS(final AWSCredentialsProvider awsCredentialsProvider, final RegionProvider regionProvider) {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClient.asyncBuilder();
        builder.setCredentials(awsCredentialsProvider);
        builder.setClientConfiguration(clientConfiguration());
        builder.setRegion(regionProvider.getRegion().getName());
        return new AmazonSQSBufferedAsyncClient(builder.build());
    }

    @Bean(name = "amazonSns", destroyMethod = "shutdown")
    public AmazonSNSAsync amazonSNS(final AWSCredentialsProvider awsCredentialsProvider, final RegionProvider regionProvider) {
        AmazonSNSAsyncClientBuilder clientBuilder = AmazonSNSAsyncClient.asyncBuilder();
        clientBuilder.setCredentials(awsCredentialsProvider);
        clientBuilder.setClientConfiguration(clientConfiguration());
        clientBuilder.setRegion(regionProvider.getRegion().getName());
        return clientBuilder.build();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(final AmazonSQSAsync amazonSqs) {
        final SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setMaxNumberOfMessages(5);
        factory.setWaitTimeOut(15);
        return factory;
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory() {
        final QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        final MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();

        //set strict content type match to false
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.singletonList(new PayloadArgumentResolver(messageConverter)));

        return factory;
    }
}
