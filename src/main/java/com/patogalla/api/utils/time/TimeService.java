package com.patogalla.api.utils.time;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class TimeService {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public boolean isFuture(final LocalDateTime dateTime) {
        return now().isBefore(dateTime);
    }

    public boolean isPast(final LocalDateTime dateTime) {
        return now().isAfter(dateTime);
    }

    public LocalDateTime after(final Duration duration) {
        return now().plus(duration);
    }

    public Date toDate(LocalDateTime in) {
        if (in != null) {
            return Date.from(in.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    public LocalDateTime fromDate(Date in) {
        if (in != null) {
            return LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }

}
