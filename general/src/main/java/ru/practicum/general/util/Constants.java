package ru.practicum.general.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final Integer PAGE_DEFAULT_FROM = 0;
    public static final Integer PAGE_DEFAULT_SIZE = 10;
    public static final LocalDateTime START_DATE = LocalDateTime.now().minusDays(1000);
    public static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(1000);
}
