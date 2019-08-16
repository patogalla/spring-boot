package com.patogalla.api.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.patogalla.api.utils.time.TimeService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class AwsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TimeService timeService;
    private final JavaMailSender mailSender;
    private final AmazonS3 s3;
    private final AmazonSNSAsync snsAsync;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public AwsService(final JavaMailSender mailSender,
                      final TimeService timeService,
                      final AmazonS3 s3,
                      @Qualifier("amazonSqs") final AmazonSQSAsync sqsAsync,
                      @Qualifier("amazonSns") final AmazonSNSAsync snsAsync) {
        this.mailSender = Objects.requireNonNull(mailSender);
        this.s3 = Objects.requireNonNull(s3);
        this.timeService = timeService;
        this.queueMessagingTemplate = new QueueMessagingTemplate(Objects.requireNonNull(sqsAsync));
        this.snsAsync = Objects.requireNonNull(snsAsync);
    }

    public void sendEmail(final String from, final String to, final String subject, final String content) throws MessagingException {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();

        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }

    public Optional<URL> createUploadUrl(String bucketName, String key, final LocalDateTime expiresOn) {
        return createUrl(bucketName, key, expiresOn, HttpMethod.PUT);
    }

    public Optional<URL> createDownloadUrl(String bucketName, String key, final LocalDateTime expiresOn) {
        return createUrl(bucketName, key, expiresOn, HttpMethod.GET);
    }

    public Optional<URL> createUploadUrl(final LocalDateTime expiresOn, final String bucket, final String pathSegment, final String... pathSegments) {
        return createUrl(bucket, s3Key(pathSegment, pathSegments), expiresOn, HttpMethod.PUT);
    }

    public Optional<URL> createDownloadUrl(final LocalDateTime expiresOn, final String bucket, final String pathSegment, final String... pathSegments) {
        return createUrl(bucket, s3Key(pathSegment, pathSegments), expiresOn, HttpMethod.GET);
    }

    public Path downloadFile(final Path target, final String bucket, final String key) {
        ObjectMetadata object = s3.getObject(new GetObjectRequest(bucket, key), target.toFile());
        return target;
    }

    public void delete(final String bucket, final String pathSegment, final String... pathSegments) {
        s3.deleteObject(bucket, s3Key(pathSegment, pathSegments));
    }

    public void allowAccessAll(final String bucket, final String key) {
        final AccessControlList acl = s3.getObjectAcl(bucket, key);
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.setObjectAcl(bucket, key, acl);
    }

    public void sendSms(final String sender, final String phone, final String text) {
        final ImmutableMap<String, MessageAttributeValue> attributes = ImmutableMap.of(
                "AWS.SNS.SMS.SenderID", new MessageAttributeValue().withStringValue(sender).withDataType("String"),
                "AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue("Promotional").withDataType("String")
        );

        final PublishResult result = snsAsync.publish(new PublishRequest()
                .withMessage(text)
                .withPhoneNumber(phone)
                .withMessageAttributes(attributes));
        logger.info("SMS {} sent successfully to {}", result.getMessageId(), phone);
    }

    public void sendPushNotification(final String platformApplicationArn, final String deviceToken, final String payload, final String userData) {
        //Might be optimized by storing deviceToken -> endPointArn in the DB
        final CreatePlatformEndpointResult result = snsAsync.createPlatformEndpoint(new CreatePlatformEndpointRequest()
                .withToken(deviceToken)
                .withCustomUserData(userData)
                .withPlatformApplicationArn(platformApplicationArn));

        snsAsync.publish(new PublishRequest()
                .withMessageStructure("json")
                .withMessage(payload)
                .withTargetArn(result.getEndpointArn()));
        logger.info("Notification {} Pushed successfully for {}/{}", payload, userData, deviceToken);
    }

    public <T> void convertAndSend(final String destinationSqsTopic, final T payload) {
        queueMessagingTemplate.convertAndSend(destinationSqsTopic, payload);
    }

    public void uploadFile(final Path filePath, final String bucket, final String pathSegment, final String... pathSegments) {
        s3.putObject(bucket, s3Key(pathSegment, pathSegments), filePath.toFile());
    }

    public void uploadFile(final Path file, final String bucket, final String key) {
        s3.putObject(bucket, key, file.toFile());
    }

    public void uploadFile(final InputStream original, final String bucket, final String pathSegment, final String... pathSegments) {
        s3.putObject(bucket, s3Key(pathSegment, pathSegments), original, null);
    }

    public void uploadFile(final String content, final String bucket, final String pathSegment, final String... pathSegments) {
        s3.putObject(bucket, s3Key(pathSegment, pathSegments), content);
    }

    private String s3Key(final String pathSegment, final String... pathSegments) {
        return Joiner.on("/").skipNulls().join(ImmutableList.builder().add(pathSegment).add((Object[]) pathSegments).build());
    }

    @VisibleForTesting
    Optional<URL> createUrl(final String bucket, final String key, final LocalDateTime expiresOn, final HttpMethod httpMethod) {
        return Optional.ofNullable(s3.generatePresignedUrl(bucket, key, timeService.toDate(expiresOn), httpMethod));
    }

}
