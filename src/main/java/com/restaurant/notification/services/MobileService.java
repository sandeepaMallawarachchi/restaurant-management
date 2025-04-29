package com.restaurant.notification.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MobileService {

    @Value("${twilio.mobile_number}")
    private String fromNumber;


    public void sendMobile (String To, String message) {

        Message.creator(
                new PhoneNumber(To),
                new PhoneNumber(fromNumber),
                message
        ).create();

    }

}
