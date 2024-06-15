package com.phcworld.boardanswerservice.service.port;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SystemUuidHolder implements UuidHolder{

    @Override
    public String random() {
        return UUID.randomUUID().toString();
    }
}
