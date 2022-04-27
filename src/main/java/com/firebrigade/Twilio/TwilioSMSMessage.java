package com.firebrigade.Twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.ArrayList;

public class TwilioSMSMessage {
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public void sendMessage( ArrayList<String> phoneNumbers, String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber.isEmpty()) {
                continue;
            }
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                            "",
                             body)
                    .create();
        }
    }
}
