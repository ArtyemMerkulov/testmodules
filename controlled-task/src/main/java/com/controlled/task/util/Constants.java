package com.controlled.task.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class Constants {

    private Constants() {
    }

    public static final class Time {

        private Time() {
        }

        public static final ZoneOffset SYSTEM_ZONE_OFFSET = ZoneId.systemDefault()
                                                                  .getRules()
                                                                  .getOffset(LocalDateTime.now());

    }

}
