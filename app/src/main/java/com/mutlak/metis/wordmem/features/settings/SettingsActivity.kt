package com.mutlak.metis.wordmem.features.settings

import android.app.*
import android.content.*
import android.os.*
import android.support.v4.app.DialogFragment
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.text.format.*
import android.util.*
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import butterknife.*
import com.mutlak.metis.wordmem.R
import com.mutlak.metis.wordmem.R.id
import com.mutlak.metis.wordmem.R.string
import com.mutlak.metis.wordmem.data.model.*
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.util.*
import io.realm.*
import java.sql.Time
import java.util.*

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

  private var mSettings: Settings? = null

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

    setupToolbar()
  }

  override fun onResume() {
    val realm = Realm.getDefaultInstance()

    mSettings = realm.where(Settings::class.java).findFirst()
    mSettings = realm.copyFromRealm(mSettings)
    if (mSettings == null) {
      mSettings = Settings()
      mSettings?.reviewLimit = reviewList.iterator().next()
      mSettings?.quizLimit = quizList.iterator().next()
      mSettings?.maxAnswers = optionList.iterator().next()
    }

    fillSpinner()

    setupQuizSettings(realm)

    setupReminder(realm)

    setupReview(realm)

    super.onResume()
  }

  private fun setupToolbar() {
    setSupportActionBar(toolbar)
    if (supportActionBar != null) {
      supportActionBar!!.setDisplayHomeAsUpEnabled(true)
      supportActionBar!!.setDisplayShowHomeEnabled(true)
      supportActionBar!!.setTitle(string.action_settings)
    }
  }

  private fun setupReview(realm: Realm) {
    mReviewSpinner.onItemSelectedListener = object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = reviewList[position]
        realm.executeTransaction { realm1 ->
          mSettings?.reviewLimit = selectedValue
          realm1.insertOrUpdate(mSettings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {

      }
    }
  }

  private fun setupQuizSettings(realm: Realm) {
    when (mSettings?.quizType) {
      id.radio_word -> wordRadio.isChecked = true
      id.radio_sentence -> sentenceRadio.isChecked = true
    }

    quizTypeRadio.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        id.radio_word -> {
          mSettings?.quizType = QUIZ_TYPE_WORD
          realm.executeTransaction { realm1 -> realm1.insertOrUpdate(mSettings) }
        }
        id.radio_sentence -> {
          mSettings?.quizType = QUIZ_TYPE_SENTENCE
          realm.executeTransaction { realm1 -> realm1.insertOrUpdate(mSettings) }
        }
      }
    }
    mQuizSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = quizList[position]
        realm.executeTransaction { realm1 ->
          mSettings?.quizLimit = selectedValue
          realm1.insertOrUpdate(mSettings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {

      }
    }

    mAnswersSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val selectedValue = optionList[position]
        realm.executeTransaction { realm1 ->
          mSettings?.maxAnswers = selectedValue
          realm1.insertOrUpdate(mSettings)
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>) {}
    }

  }

  private fun setupReminder(realm: Realm) {
    mReminderGear.setOnClickListener { showTimePickerDialog() }

    mReminderSwitch.isChecked = mSettings?.isReminderActive!!

    mReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
      realm.executeTransaction {
        mSettings?.isReminderActive = isChecked
        it.insertOrUpdate(mSettings)
      }
      if (!isChecked) {
        cancelAlarm()
      } else {
        if (mSettings?.reminderDate != null) {
          val time = mSettings?.reminderDate!!
          setupAlarm(Time(time))
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

    if (mSettings?.quizType == 0) mSettings?.quizType = QUIZ_TYPE_WORD

    reviewList
        .filter { mSettings?.reviewLimit == it }
        .forEach { mReviewSpinner.setSelection(reviewList.indexOf(it)) }
    quizList
        .filter { mSettings?.quizLimit == it }
        .forEach { mQuizSpinner.setSelection(quizList.indexOf(it)) }

    optionList
        .filter { mSettings?.maxAnswers == it }
        .forEach { mAnswersSpinner.setSelection(optionList.indexOf(it)) }

    if (mSettings?.quizType != 0) {
      when (mSettings?.quizType) {
        QUIZ_TYPE_WORD -> wordRadio.isChecked = true
        QUIZ_TYPE_SENTENCE -> sentenceRadio.isChecked = true
      }
    }
  }

  private fun showTimePickerDialog() {
    val newFragment = TimePickerFragment()
    newFragment.show(supportFragmentManager, "timePicker")
  }

  class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    val realm: Realm = Realm.getDefaultInstance()
    private var result: Settings = realm.where(Settings::class.java).findFirst()
    val settings: Settings = realm.copyFromRealm(result)


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
      val hour: Int
      val minute: Int
      val c = Calendar.getInstance()
      if (settings.reminderDate == 0L) {
        hour = c.get(Calendar.HOUR_OF_DAY)
        minute = c.get(Calendar.MINUTE)
      } else {
        val time = Time(settings.reminderDate)
        hour = time.hours
        minute = time.minutes
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

      settings.reminderDate = time.time
      realm.executeTransaction { it.insertOrUpdate(settings) }

      val pendingIntent: PendingIntent
      val manager: AlarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val alarmIntent = Intent(activity, AlarmReciever::class.java)
      pendingIntent = PendingIntent.getBroadcast(activity, ALARM_REQUEST_CODE, alarmIntent, 0)

      manager.setRepeating(AlarmManager.RTC_WAKEUP, time.time, AlarmManager.INTERVAL_DAY,
          pendingIntent)

    }
  }

  fun setupAlarm(time: Time) {
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

  override fun onDestroy() {
    super.onDestroy()
  }

}
