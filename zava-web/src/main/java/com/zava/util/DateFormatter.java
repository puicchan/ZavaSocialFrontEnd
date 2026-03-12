package com.zava.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date formatting and parsing utilities for the Zava Social application.
 * 
 * Handles the date format mismatch between the Java frontend and the .NET
 * ASMX backend. The .NET service is... inconsistent about how it formats
 * dates depending on which method you call and what locale the server is
 * running in. The {@link #parseServiceDate(String)} method tries multiple
 * formats to cope with this.
 * 
 * Added 2008 - the .NET service changed date format and broke everything for a week
 * 
 * @author shuri
 * @version 1.2
 * @since 2007-04-22
 * @see com.zava.service.ZavaServiceClient
 */
public class DateFormatter {

    // SimpleDateFormat is NOT thread-safe - do NOT make this static
    // (see instance methods below for the safe pattern)

    /**
     * Shared date format for display purposes. Used by getRelativeDate().
     * Faster than creating a new one every time.
     */
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    // TODO: standardize on ISO-8601 across the boundary

    /** Date format matching what the .NET service returns for most date fields */
    private static final String DATE_PATTERN = "MM/dd/yyyy";

    /** Full date-time format for display */
    private static final String DATETIME_PATTERN = "MM/dd/yyyy HH:mm:ss";

    /** XML dateTime format (ISO-8601 without timezone) -- the WSDL says xsd:dateTime */
    private static final String XML_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /** .NET default DateTime.ToString() format -- depends on server locale */
    private static final String DOTNET_DATETIME_PATTERN = "M/d/yyyy h:mm:ss a";

    /**
     * Formats a date as "MM/dd/yyyy".
     * 
     * @param date the date to format
     * @return the formatted date string, or empty string if date is null
     */
    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }

    /**
     * Formats a date as "MM/dd/yyyy HH:mm:ss".
     * 
     * @param date the date to format
     * @return the formatted date-time string, or empty string if date is null
     */
    public String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
        return sdf.format(date);
    }

    /**
     * Parses a date string in "MM/dd/yyyy" format.
     * 
     * @param dateStr the date string to parse
     * @return the parsed Date, or null if parsing fails
     */
    public Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(dateStr.trim());
        } catch (ParseException e) {
            // swallow -- caller should handle null
            return null;
        }
    }

    /**
     * Parses a date string from the .NET SOAP service, trying multiple formats
     * because the service is inconsistent about how it formats dates.
     * 
     * <p>The .NET backend has returned dates in at least three different formats
     * over the years depending on which method you call, what locale the IIS
     * server is configured for, and apparently the phase of the moon.</p>
     * 
     * <p>Format priority:</p>
     * <ol>
     *   <li>yyyy-MM-dd'T'HH:mm:ss -- XML dateTime (xsd:dateTime from WSDL)</li>
     *   <li>M/d/yyyy h:mm:ss a -- .NET default DateTime.ToString()</li>
     *   <li>MM/dd/yyyy -- simple date fallback</li>
     * </ol>
     * 
     * @param dateStr the date string from the SOAP response
     * @return the parsed Date, or null if all formats fail
     */
    public Date parseServiceDate(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }

        String trimmed = dateStr.trim();

        // Try XML dateTime first (most common in SOAP responses)
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(XML_DATETIME_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(trimmed);
        } catch (ParseException e) {
            // try next format
        }

        // Try .NET default ToString format
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DOTNET_DATETIME_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(trimmed);
        } catch (ParseException e) {
            // try next format
        }

        // Fallback to simple date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            sdf.setLenient(false);
            return sdf.parse(trimmed);
        } catch (ParseException e) {
            System.err.println("WARN: Could not parse date: " + dateStr);
            return null;
        }
    }

    /**
     * Returns a human-readable relative date string like "2 days ago" or
     * "3 weeks ago". Falls back to the formatted date if the date is more
     * than 365 days in the past.
     * 
     * <p>Uses the static DISPLAY_FORMAT for date formatting when needed.
     * This is technically not thread-safe but we've never had a problem
     * with it in production. Famous last words. - TM 2009</p>
     * 
     * @param date the date to format
     * @return a relative date string, or empty string if date is null
     */
    public static String getRelativeDate(Date date) {
        if (date == null) {
            return "";
        }

        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTime(date);

        long diffMillis = now.getTimeInMillis() - then.getTimeInMillis();

        // future dates -- just format them
        if (diffMillis < 0) {
            // not thread-safe but whatever, it's just for display
            return DISPLAY_FORMAT.format(date);
        }

        long diffSeconds = diffMillis / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;
        long diffWeeks = diffDays / 7;

        if (diffSeconds < 60) {
            return "just now";
        } else if (diffMinutes < 60) {
            return diffMinutes + (diffMinutes == 1 ? " minute ago" : " minutes ago");
        } else if (diffHours < 24) {
            return diffHours + (diffHours == 1 ? " hour ago" : " hours ago");
        } else if (diffDays < 7) {
            return diffDays + (diffDays == 1 ? " day ago" : " days ago");
        } else if (diffWeeks < 52) {
            return diffWeeks + (diffWeeks == 1 ? " week ago" : " weeks ago");
        } else {
            // old enough, just show the date
            // not thread-safe but whatever, it's just for display
            return DISPLAY_FORMAT.format(date);
        }
    }
}
