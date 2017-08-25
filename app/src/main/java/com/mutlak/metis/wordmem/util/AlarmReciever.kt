package com.mutlak.metis.wordmem.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.NotificationCompat
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.landing.LanActivity
import com.mutlak.metis.wordmem.features.result.ResultActivity
import com.mutlak.metis.wordmem.features.review.ReviewActivity


class AlarmReciever : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val mNotificationUtils = NotificationUtils(context)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val mBuilder: Notification.Builder = mNotificationUtils.getAndroidChannelNotification(
          context.getString(R.string.notification_review_title),
          context.getString(R.string.notification_review_content))
      val resultIntent = Intent(context, ReviewActivity::class.java)
      resultIntent.putExtra(LanActivity.REVIEW_TYPE, LanActivity.REVIEW_TYPE_NEW)

      val stackBuilder = TaskStackBuilder.create(context)
      stackBuilder.addParentStack(ResultActivity::class.java)
      stackBuilder.addNextIntent(resultIntent)
      val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
      mBuilder.setContentIntent(resultPendingIntent)
      mNotificationUtils.manager.notify(REVIEW_NOTIFICAION_ID, mBuilder.build())
    } else {
      val mBuilder = NotificationCompat.Builder(context).setSmallIcon(
          R.drawable.ic_notificaiton)
          .setContentTitle(context.getString(R.string.notification_review_title))
          .setContentText(
              context.getString(R.string.notification_review_content)) as NotificationCompat.Builder

      val resultIntent = Intent(context, ReviewActivity::class.java)
      resultIntent.putExtra(LanActivity.REVIEW_TYPE, LanActivity.REVIEW_TYPE_NEW)

      val stackBuilder = TaskStackBuilder.create(context)
      stackBuilder.addParentStack(ResultActivity::class.java)
      stackBuilder.addNextIntent(resultIntent)
      val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
      mBuilder.setContentIntent(resultPendingIntent)
      mBuilder.setDefaults(Notification.DEFAULT_SOUND)
      val mNotificationManager = context.getSystemService(
          Context.NOTIFICATION_SERVICE) as NotificationManager
      mNotificationManager.notify(REVIEW_NOTIFICAION_ID, mBuilder.build())
    }
  }

  companion object {
    var REVIEW_NOTIFICAION_ID = 99
  }
}
