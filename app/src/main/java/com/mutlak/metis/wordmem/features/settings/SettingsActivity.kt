package com.mutlak.metis.wordmem.features.settings

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TimePicker
import android.widget.Toast
import butterknife.BindView
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.data.model.Settings
import com.mutlak.metis.wordmem.features.base.BaseActivity
import com.mutlak.metis.wordmem.util.AlarmReciever
import io.realm.Realm
import java.sql.Time
import java.util.Calendar

class SettingsActivity : BaseActivity() {

  companion object {
    const val TAG = "SettingsActivity"
    const val QUIZ_TYPE_WORD = 1
    const val QUIZ_TYPE_SENTENCE = 2
    const val ALARM_REQUEST_CODE = 11
    val reviewList = mutableListOf(10, 20, 25)
    val quizList = mutableListOf(5, 10, 15)
    val optionList = mutableListOf(4, 5, 6)
  }

  private lateinit var settings: Settings

  @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
  @BindView(R.id.review_number_sp) lateinit var mReviewSpinner: AppCompatSpinner
  @BindView(R.id.quiz_number_sp) lateinit var mQuizSpinner: AppCompatSpinner
  @BindView(R.id.max_answer_sp) lateinit var mAnswersSpinner: AppCompatSpinner
  @BindView(R.id.radio_group) lateinit var quizTypeRadio: RadioGroup
  @BindView(R.id.radio_word) lateinit var wordRadio: RadioButton
  @BindView(R.id.radio_sentence) lateinit var sentenceRadio: RadioButton
  @BindView(R.id.reminder_switch) lateinit var mReminderSwitch: SwitchCompat
  @BindView(R.id.reminder_gear_image) lateinit var mReminderGear: ImageView

  override val layout: Int
    get() = R.layout.activity_settings

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityComponent().inject(this)
    val realm = Realm.getDefaultInstance()

    settings = realm.where(Settings::class.java).findFirst()
    settings = realm.copyFromRealm(settings)

    setSupportActionBar(toolbar)
    if (supportActionBar != null) {
      supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      supportActionBar!!.setDisplayShowHomeEnabled(true)
      supportActionBar!!.setTitle(R.string.action_settings)
    }

    fillSpinner()

    mReminderGear.setOnClickListener { _ -> showTimePickerDialog() }

    mReminderSwitch.isChecked = settings.isReminderActive

    when (settings.quizType) {
      R.id.radio_word -> wordRadio.isChecked = true
      R.id.radio_sentence -> sentenceRadio.isChecked = true
    }

    mReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
      realm.executeTransaction { realm1 ->
        settings.isReminderActive = isChecked
        realm1.insertOrUpdate(settings)
      }
      if (!isChecked) {
        cancelAlarm()
      } else {
//        if (settings.reminderDate != null)
//          setupAlarm(settings.getReminderDate!!)
      }
    }

    mReviewSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = reviewList[position]
        realm.executeTransaction { realm1 ->
          settings.reviewLimit = selectedValue
          realm1.insertOrUpdate(settings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {

      }
    }

    mQuizSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = quizList[position]
        realm.executeTransaction { realm1 ->
          settings.reviewLimit = selectedValue
          realm1.insertOrUpdate(settings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {

      }
    }

    mAnswersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = optionList[position]
        realm.executeTransaction { realm1 ->
          settings.maxAnswers = selectedValue
          realm1.insertOrUpdate(settings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    quizTypeRadio.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.radio_word -> {
          settings.quizType = QUIZ_TYPE_WORD
          realm.executeTransaction { realm1 -> realm1.insertOrUpdate(settings) }
        }
        R.id.radio_sentence -> {
          settings.quizType = QUIZ_TYPE_SENTENCE
          realm.executeTransaction { realm1 -> realm1.insertOrUpdate(settings) }
        }
      }
    }
  }

  private fun fillSpinner() {
    val dataAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, reviewList)
    mReviewSpinner.adapter = dataAdapter

    val quizAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, quizList)
    mQuizSpinner.adapter = quizAdapter

    val optionsAdapter = ArrayAdapter(this, R.layout.custom_spinner_item, optionList)
    mAnswersSpinner.adapter = optionsAdapter

    if (settings.quizType == 0) settings.quizType = QUIZ_TYPE_WORD

    if (settings == null) {
      settings = Settings()
      settings.reviewLimit = reviewList.iterator().next()
      settings.quizLimit = quizList.iterator().next()
      settings.maxAnswers = optionList.iterator().next()
    } else {
      reviewList
          .filter { settings.reviewLimit == it }
          .forEach { mReviewSpinner.setSelection(reviewList.indexOf(it)) }
      quizList
          .filter { settings.quizLimit == it }
          .forEach { mQuizSpinner.setSelection(quizList.indexOf(it)) }

      optionList
          .filter { settings.maxAnswers == it }
          .forEach { mAnswersSpinner.setSelection(optionList.indexOf(it)) }
    }

    if (settings.quizType != 0) {
      when (settings.quizType) {
        QUIZ_TYPE_WORD -> wordRadio.isChecked = true
        QUIZ_TYPE_SENTENCE -> sentenceRadio.isChecked = true
      }
    }
  }

  fun showTimePickerDialog() {
    val newFragment = TimePickerFragment()
    newFragment.show(supportFragmentManager, "timePicker")
  }

  class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    internal var realm = Realm.getDefaultInstance()
    internal var settings: Settings = realm.where(Settings::class.java).findFirst()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
      val hour: Int
      val minute: Int
      val c = Calendar.getInstance()
      if (settings.reminderDate == null) {
        hour = c.get(Calendar.HOUR_OF_DAY)
        minute = c.get(Calendar.MINUTE)
      } else {
        hour = settings.reminderDate.toInt()
        minute = settings.reminderDate.toInt()
      }
      return TimePickerDialog(activity, this, hour, minute,
          DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
      val switchCompat = activity.findViewById<SwitchCompat>(R.id.reminder_switch)
      switchCompat.isChecked = true

      val cal = Calendar.getInstance()
      cal.set(Calendar.HOUR, hour)
      cal.set(Calendar.MINUTE, minute)
      cal.set(Calendar.SECOND, 0)
      cal.set(Calendar.MILLISECOND, 0)

      val time = Time(cal.timeInMillis)

      Log.i(TAG, "onTimeSet: " + time.toString())
      realm.executeTransaction { _ -> settings.reminderDate = time.time }

      val pendingIntent: PendingIntent
      val manager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val alarmIntent = Intent(activity, AlarmReciever::class.java)
      pendingIntent = PendingIntent.getBroadcast(activity, ALARM_REQUEST_CODE, alarmIntent, 0)

      manager.setRepeating(AlarmManager.RTC_WAKEUP, time.time, AlarmManager.INTERVAL_DAY,
          pendingIntent)
      Toast.makeText(activity, "Alarm Set", Toast.LENGTH_SHORT).show()
    }
  }

  private fun setupAlarm(time: Time) {
    Log.i(TAG, "setupAlarm: " + time.toString())
    val pendingIntent: PendingIntent
    val manager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(this, AlarmReciever::class.java)
    pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, 0)

    manager.setRepeating(AlarmManager.RTC_WAKEUP, time.time, AlarmManager.INTERVAL_DAY,
        pendingIntent)
  }

  private fun cancelAlarm() {
    val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, AlarmReciever::class.java)
    val sender = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent, 0)
    am.cancel(sender)
  }

}
