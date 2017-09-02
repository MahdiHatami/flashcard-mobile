package com.mutlak.metis.wordmem.util

import android.app.*
import android.content.*
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.NotificationCompat
import com.mutlak.metis.wordmem.*
import com.mutlak.metis.wordmem.features.landing.*
import com.mutlak.metis.wordmem.features.result.*
import com.mutlak.metis.wordmem.features.review.*


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
