package com.lokoproject.mailing.listener;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lokoproject.mailing.conditions.OrCondition;
import com.lokoproject.mailing.service.MailingService;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeDetachEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.lokoproject.mailing.entity.Mailing;
import com.haulmont.cuba.core.listener.BeforeAttachEntityListener;

import javax.inject.Inject;


@Component("mailing_MailingEntityListener")
public class MailingEntityListener implements BeforeDetachEntityListener<Mailing>, BeforeAttachEntityListener<Mailing> {

    @Inject
    private MailingService mailingService;


    @Override
    public void onBeforeDetach(Mailing entity, EntityManager entityManager) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            if((entity.getConsolidationConditionJson()==null)||("".equals(entity.getConsolidationConditionJson()))) return;
            OrCondition condition=mapper.readValue(entity.getConsolidationConditionJson(), OrCondition.class);
            entity.setConsolidationCondition(condition);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBeforeAttach(Mailing entity) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE);
        try {
            if(entity.getConsolidationCondition()!=null) {
                String conditionJson = mapper.writeValueAsString(entity.getConsolidationCondition());
                entity.setConsolidationConditionJson(conditionJson);
            }
            mailingService.updateMailing(entity);
        } catch (JsonProcessingException ignored) {
        }
    }


}