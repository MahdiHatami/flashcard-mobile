package com.mutlak.metis.wordmem.util


import java.text.*
import java.util.*

object TimeUtil {

  val API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

  private val SECOND_MILLIS = 1000
  private val TEN_SEC = 10 * 1000
  private val MINUTE_MILLIS = 60 * SECOND_MILLIS
  private val HOUR_MILLIS = 60 * MINUTE_MILLIS
  private val DAY_MILLIS = 24 * HOUR_MILLIS
  private val TEN_NOW = "Az önce"
  private val JUST_NOW = "Az önce"
  private val A_MINUTE_AGO = "Bir dakika önce"
  private val AN_HOUR_AGO = "1 Saat önce"
  val YESTERDAY = "Dün"
  private val DAYS_AGO = " Gün önce"
  private val HOURS_AGO = " Saat önce"
  private val MINUTES_AGO = " Dakika önce"

  fun getTimeAgo(time: Long): String? {
    var time = time
    if (time < 1000000000000L) {
      // if timestamp given in seconds, convert to millis
      time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
      return null
    }

    val diff = now - time
    return if (diff < TEN_SEC) {
      TEN_NOW
    } else if (diff < MINUTE_MILLIS) {
      JUST_NOW
    } else if (diff < 2 * MINUTE_MILLIS) {
      A_MINUTE_AGO
    } else if (diff < 50 * MINUTE_MILLIS) {
      (diff / MINUTE_MILLIS).toString() + MINUTES_AGO
    } else if (diff < 90 * MINUTE_MILLIS) {
      AN_HOUR_AGO
    } else if (diff < 24 * HOUR_MILLIS) {
      (diff / HOUR_MILLIS).toString() + HOURS_AGO
    } else if (diff < 48 * HOUR_MILLIS) {
      YESTERDAY
    } else {
      (diff / DAY_MILLIS).toString() + DAYS_AGO
    }
  }

  fun convertToDateObject(date: String?): Date? {
    if (date == null || date.isEmpty()) return null
    val formatter = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
    try {
      return formatter.parse(date.replace("T", " "))
    } catch (e: ParseException) {
      e.printStackTrace()
    }

    return null
  }
}
