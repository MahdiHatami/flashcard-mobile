package com.mutlak.metis.wordmem.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.features.landing.LandingActivity
import com.mutlak.metis.wordmem.features.result.ResultActivity
import com.mutlak.metis.wordmem.features.review.ReviewActivity


class AlarmReciever : BroadcastReceiver() {

  companion object {
    var REVIEW_NOTIFICAION_ID = 99
  }

  override fun onReceive(context: Context, intent: Intent) {
    val mNotificationUtils = NotificationUtils(context)
    val resultIntent = Intent(context, ReviewActivity::class.java)
    resultIntent.putExtra(LandingActivity.REVIEW_TYPE, LandingActivity.REVIEW_TYPE_NEW)

    val stackBuilder = TaskStackBuilder.create(context)
    stackBuilder.addParentStack(ResultActivity::class.java)
    stackBuilder.addNextIntent(resultIntent)

    val resultPendingIntent = stackBuilder.getPendingIntent(0,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val mBuilder = NotificationCompat.Builder(context).setSmallIcon(
        R.drawable.ic_notificaiton)
        .setContentTitle(context.getString(R.string.notification_review_title))
        .setContentText(
            context.getString(R.string.notification_review_content)) as NotificationCompat.Builder

    mBuilder.setContentIntent(resultPendingIntent)
    mBuilder.setDefaults(Notification.DEFAULT_SOUND)

    when {
      android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O -> {
        mNotificationUtils.manager.notify(REVIEW_NOTIFICAION_ID, mBuilder.build())
      }
      else -> {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(REVIEW_NOTIFICAION_ID, mBuilder.build())
      }
    }
  }
}
