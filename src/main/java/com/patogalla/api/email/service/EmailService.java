package com.patogalla.api.email.service;

import com.patogalla.api.aws.AwsService;
import com.patogalla.api.email.config.EmailConfig;
import com.patogalla.api.utils.template.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final TemplateService templateService;
    private final AwsService awsService;
    private final EmailConfig emailConfig;

    @Autowired
    public EmailService(TemplateService templateService, final AwsService awsService, final EmailConfig emailConfig) {
        this.templateService = templateService;
        this.awsService = awsService;
        this.emailConfig = emailConfig;
    }

    public void sendNotification(String to, String subject, String template, Map<String, Object> params) {
        LOGGER.info("Sending notification by email to : {} , subject: {}, template: {}, params : {}", to, subject, template, params);
        try {
            String emailBody = this.templateService.template(template, params);
            LOGGER.trace("Sending notification body : {}", emailBody);
            awsService.sendEmail(this.emailConfig.getFrom(), to, subject, emailBody);
        } catch (IOException | MessagingException e) {
            LOGGER.error("Error trying to send notification.", e);
        }
        // TODO
    }
}
