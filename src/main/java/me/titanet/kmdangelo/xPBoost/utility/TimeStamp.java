package me.titanet.kmdangelo.xPBoost.utility;

public class TimeStamp {
    public static String fromSecondsToWeeksDaysHoursMinutesSeconds(long time) {
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
}
