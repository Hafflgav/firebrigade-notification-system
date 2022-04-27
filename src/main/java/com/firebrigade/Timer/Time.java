package com.firebrigade.Timer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Time {
    public LocalDateTime getTime(){
        return LocalDateTime.now();
    }

    public long calculateTimeDifference(LocalDateTime startingTime, LocalDateTime endingTime){
        long minutes = ChronoUnit.MINUTES.between(startingTime, endingTime);
        return minutes;
    }
}
