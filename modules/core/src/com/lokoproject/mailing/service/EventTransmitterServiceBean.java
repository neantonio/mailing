package com.lokoproject.mailing.service;

import com.lokoproject.mailing.core.EventTransmitter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(EventTransmitterService.NAME)
public class EventTransmitterServiceBean implements EventTransmitterService {

    @Inject
    private EventTransmitter eventTransmitter;

    @Override
    public void setUrlOfWebModule(String url){
        if(!url.contains("localhost")){
            eventTransmitter.setUrl(url);
        }
        else {
            eventTransmitter.setUrl(url.replace("localhost","127.0.0.1"));
        }
    }

}