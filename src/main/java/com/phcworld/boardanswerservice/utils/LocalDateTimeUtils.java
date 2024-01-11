package com.phcworld.boardanswerservice.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtils {
	
	public static final int MINUTES = 60;
	public static final int HOUR_OF_DAY = 24;

	public static String getTime(LocalDateTime inputDate) {
		if(inputDate == null) {
			return "";
		}
		if(LocalDateTime.now().getYear() - inputDate.getYear() > 0) {
			return inputDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		}
		long differenceMinutes = Duration.between(inputDate, LocalDateTime.now()).toMinutes();
		
		long changedHour = differenceMinutes / MINUTES;
		if (changedHour < HOUR_OF_DAY) {
			if (changedHour < 1) {
				if (differenceMinutes == 0) {
					return "방금전";
				}
				return differenceMinutes + "분 전";
			}
			return changedHour + "시간 전";
		}
		
		/*long duration = Duration.between(inputDate, LocalDateTime.now()).toDays();
		if (duration < 1) {
			return inputDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		}*/
		return inputDate.format(DateTimeFormatter.ofPattern("MM.dd"));
	}

}
