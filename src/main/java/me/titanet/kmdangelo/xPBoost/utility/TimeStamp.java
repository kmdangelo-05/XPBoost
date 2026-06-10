package me.titanet.kmdangelo.xPBoost.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeStamp {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([wdhms])");

    public static String formattingTime(long time) {
        int weeks = Math.toIntExact(time / 604800);
        int days = Math.toIntExact(time % 604800 / 86400);
        int hours = Math.toIntExact(time % 604800 % 86400 / 3600);
        int minutes = Math.toIntExact(time % 604800 % 86400 % 3600 / 60);
        int seconds = Math.toIntExact(time % 604800 % 86400 % 3600 % 60);

        String duration;

        if (weeks != 0) {
            duration = weeks + "w " + days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
        } else if (days != 0) {
            duration = days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
        } else if (hours != 0) {
            duration = hours + "h " + minutes + "m " + seconds + "s ";
        } else if (minutes != 0) {
            duration = minutes + "m " + seconds + "s ";
        } else {
            duration = seconds + "s ";
        }
        return duration;
    }

    public static String formattingTimeWithoutSpaces(long time) {
        int weeks = Math.toIntExact(time / 604800);
        int days = Math.toIntExact(time % 604800 / 86400);
        int hours = Math.toIntExact(time % 604800 % 86400 / 3600);
        int minutes = Math.toIntExact(time % 604800 % 86400 % 3600 / 60);
        int seconds = Math.toIntExact(time % 604800 % 86400 % 3600 % 60);

        String duration;

        if (weeks != 0) {
            duration = weeks + "w" + days + "d" + hours + "h" + minutes + "m" + seconds + "s";
        } else if (days != 0) {
            duration = days + "d" + hours + "h" + minutes + "m" + seconds + "s";
        } else if (hours != 0) {
            duration = hours + "h" + minutes + "m" + seconds + "s";
        } else if (minutes != 0) {
            duration = minutes + "m" + seconds + "s";
        } else {
            duration = seconds + "s";
        }
        return duration;
    }

    public static long fromFormattedToSeconds(String formattedTime) {
        long seconds = 0;

        Matcher matcher = TIME_PATTERN.matcher(formattedTime);
        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            long multiplier;
            switch (matcher.group(2)) {
                case "w": multiplier = 604800;
                    break;
                case "d": multiplier = 86400;
                    break;
                case "h": multiplier = 3600;
                    break;
                case "m": multiplier = 60;
                    break;
                case "s": multiplier = 1;
                    break;
                default: throw new IllegalArgumentException();
            }
            seconds = seconds + (value * multiplier);

        }

        return seconds;
    }
}
