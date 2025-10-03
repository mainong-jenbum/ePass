package com.jenbumapps.e_passbordumsa.utility.pico.util;


import com.jenbumapps.core.model.time.LocalDate;
import com.jenbumapps.core.model.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Converter {

    public static String formatDate(Date chosenDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM, yyyy", Locale.ENGLISH); // Set your date format
        return sdf.format(chosenDate);
    }

    public static String formatDate(LocalDate chosenDate) {

        Calendar cal = chosenDate.getDate();
        return formatDate(LocalDateTime.of(cal));
    }

    public static String formatDate(LocalDateTime dateTime) {
        if(dateTime != null && dateTime.getDate() != null && dateTime.getDate().getDate() != null) {
            return formatDate(dateTime.getDate().getDate().getTime());
        } else {
            return "";
        }
    }


    public static String formatTime(Calendar time){

        if( time == null) return "";

        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        int sec = time.get(Calendar.SECOND);
        boolean pm = false;

        if(hour>12){
            pm = true;
            hour = hour - 12;
        }

        return pm ? hour +":"+min+":"+sec+" PM" : hour +":"+min+":"+sec+" AM";
    }

    public static String formatTime(LocalDateTime time){

        if(time != null && time.getTime() != null) {
            Calendar cal = time.getDate().getDate();

            return formatTime(cal);
        }

        return "";

    }

    public static String formatDateTime(LocalDateTime dateTime){

        if(dateTime == null) return "";

        String date = formatDate(dateTime);
        String time = formatTime(dateTime);

        return date +" at "+ time;
    }
}
