package com.firebrigade.Slack;

public class SlackApp {
    public void sendSlackMessage(String name, String body){
        SlackMessage slackMessage = SlackMessage.builder()
                .channel("devrel-standup")
                .username("Fire-brigade Emergency Notification")
                .text(name + body)
                .icon_emoji(":fire_engine:")
                .build();
        SlackUtils.sendMessage(slackMessage);
    }
}
