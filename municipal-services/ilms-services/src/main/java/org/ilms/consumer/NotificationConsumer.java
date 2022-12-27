package org.ilms.consumer;

import java.util.HashMap;
import org.ilms.configs.ILMSConfiguration;
import org.ilms.service.NotificationService;
import org.ilms.util.ILMSConstants;
import org.ilms.web.model.CaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationConsumer {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ILMSConfiguration configs;

    @Autowired
    private NotificationService notifService;

    @KafkaListener (topics = {"${persister.save.ilms.case.topic}" })
    public void listen(final HashMap<String, Object> record, @Header (KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {

            if (topic.equalsIgnoreCase(configs.getCreateCaseTopic())) {

                CaseRequest request = mapper.convertValue(record, CaseRequest.class);
                notifService.process(topic, request);

            }
        } catch (final Exception e) {

            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e);
        }
    }
}

