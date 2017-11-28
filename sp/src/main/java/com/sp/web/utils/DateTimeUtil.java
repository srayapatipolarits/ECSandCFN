package com.sp.web.utils;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

import com.sp.web.Constants;
import com.sp.web.model.spectrum.TimeFilter;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Methods to calculate biweekly, monthly, and quarterly date frequencies.
 * 
 * @author pruhil
 *
 */
public class DateTimeUtil {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(DateTimeUtil.class);
  
  /**
   * <code>getWeeklyPattenr</code> method will return the list of dates falls between the start date
   * and end date for the requested days of the week. fog eg. if Startdate - 2014/12/01 and End Date
   * is : 2014/12/30 and day of week is
   * 
   * @param startDate
   *          start date
   * @param endDate
   *          end date
   * @param dayOfWeek
   *          of the week.
   * @return the list of date.
   */
  public static List<LocalDate> getWeeklyPatten(LocalDate startDate, LocalDate endDate,
      DayOfWeek dayOfWeek) {
    List<LocalDate> dates = new ArrayList<LocalDate>();
    Period ofWeeks = Period.ofWeeks(1);
    while (true) {
      
      if (startDate.getDayOfWeek() == dayOfWeek && startDate.isBefore(endDate)) {
        dates.add(startDate);
        
      } else {
        LocalDate with = startDate.with(TemporalAdjusters.next(dayOfWeek));
        if (!with.isAfter(endDate)) {
          dates.add(with);
        } else {
          break;
        }
      }
      
      startDate = startDate.plus(ofWeeks);
      ;
    }
    
    return dates;
    
  }
  
  /**
   * <code>getMontlyDates</code> method return the montly dates/
   * 
   * @param startDate
   * @param endDate
   * @param dayOfWeek
   * @param ordinal
   * @return
   */
  public static List<LocalDate> getMontlyDates(LocalDate startDate, LocalDate endDate,
      DayOfWeek dayOfWeek, int ordinal) {
    List<LocalDate> dates = new ArrayList<LocalDate>();
    Period ofMonths = Period.ofMonths(1);
    while (true) {
      LocalDate with = startDate.with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek));
      if (!with.isAfter(endDate)) {
        if (!with.isBefore(LocalDate.now()))
          dates.add(with);
      } else {
        break;
      }
      
      startDate = startDate.plus(ofMonths);
      ;
    }
    LOG.info("MOnthly Dates returend are " + dates);
    return dates;
    
  }
  
  /**
   * <code>getNearestDate</code> method will return the nearest date.
   * 
   * @param dates
   *          list of dates
   * @param date
   *          date for which enares to eb returend
   * @return the locale date.
   */
  public static LocalDate getNearestDate(List<LocalDate> dates, LocalDate date) {
    LocalDate nearestDate = null;
    for (LocalDate ldate : dates) {
      if (date.isBefore(ldate) || date.isEqual(ldate)) {
        nearestDate = ldate;
        break;
      }
    }
    LOG.info("Nearest date returned is " + nearestDate);
    return nearestDate;
  }
  
  /**
   * <code>getLocalDate</code> method return the local date from string input.
   * 
   * @param date
   *          string
   * @return local date.
   */
  public static LocalDate getLocalDate(String date) {
    return getLocalDate(date, MessagesHelper.getMessage(Constants.DATE_FORMAT_DEFAULT));
  }
  
  /**
   * <code>getLocalDate</code> method return the local date from string input.
   * 
   * @param date
   *          string
   * @return local date.
   */
  public static LocalDate getLocalDate(String date, String pattern) {
    
    return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
  }
  
  /**
   * Converts the java util date to local date.
   * 
   * @param date
   *          - date
   * @return the local date
   */
  public static LocalDate getLocalDate(Date date) {
    return ofInstant(ofEpochMilli(date.getTime()), systemDefault()).toLocalDate();
  }
  
  /**
   * Converts the java util date to local date.
   * 
   * @param date
   *          - date
   * @return the local date
   */
  public static LocalDateTime getLocalDateTime(Date date) {
    LocalDate localDate = ofInstant(ofEpochMilli(date.getTime()), systemDefault()).toLocalDate();
    LocalTime localTime = ofInstant(ofEpochMilli(date.getTime()), systemDefault()).toLocalTime();
    return LocalDateTime.of(localDate, localTime);
  }
  
  /**
   * <code>getLocalDateFromMongoId</code> method will return the date from mongo id string.
   * 
   * @param id
   *          string mongoId.
   * @return the local date.
   */
  public static LocalDate getLocalDateFromMongoId(String id) {
    
    ObjectId objectId = new ObjectId(id);
    Date date = objectId.getDate();
    return getLocalDate(date);
  }
  
  /**
   * getTimeFilterDate method will return the TimeFilter to be which the date belongs.
   * 
   * @param localDate
   *          to be checked.
   * @return the timefilter under which the date falls in.
   */
  public static TimeFilter getTimeFilterDate(LocalDate localDate) {
    
    /* Get the current date */
    LocalDate currentDate = LocalDate.now();
    
    LocalDate weekDate = currentDate.minusDays(7);
    
    LocalDate monthDate = currentDate.minusMonths(1);
    
    LocalDate yearOldDate = currentDate.minusYears(1);
    
    /* check if currentDate falls between current date and weekdate */
    if (localDate.getDayOfMonth() == currentDate.getDayOfMonth()
        && localDate.getMonth() == currentDate.getMonth()
        && localDate.getYear() == currentDate.getYear()) {
      return TimeFilter.DAY;
    } else if (localDate.isAfter(weekDate)) {
      return TimeFilter.WEEK;
    } else if (localDate.isAfter(monthDate)) {
      return TimeFilter.MONTH;
    } else if (localDate.isAfter(yearOldDate)) {
      return TimeFilter.YEAR;
    } else {
      return TimeFilter.OLDER;
    }
    
  }
  
  /**
   * getTimeFilterDate method will return the TimeFilter to be which the date belongs.
   * 
   * @param localDate
   *          to be checked.
   * @return the timefilter under which the date falls in.
   */
  public static TimeFilter getTimeFilterDate(LocalDateTime localDate) {
    
    /* Get the current date */
    LocalDateTime currentDate = LocalDateTime.now();
    
    LocalDateTime weekDate = currentDate.minusDays(7);
    
    LocalDateTime monthDate = currentDate.minusMonths(1);
    
    LocalDateTime yearOldDate = currentDate.minusYears(1);
    
    /* check if currentDate falls between current date and weekdate */
    if (localDate.getDayOfMonth() == currentDate.getDayOfMonth()
        && localDate.getMonth() == currentDate.getMonth()
        && localDate.getYear() == currentDate.getYear()) {
      return TimeFilter.DAY;
    } else if (localDate.isAfter(weekDate)) {
      return TimeFilter.WEEK;
    } else if (localDate.isAfter(monthDate)) {
      return TimeFilter.MONTH;
    } else if (localDate.isAfter(yearOldDate)) {
      return TimeFilter.YEAR;
    } else {
      return TimeFilter.OLDER;
    }
    
  }
}
