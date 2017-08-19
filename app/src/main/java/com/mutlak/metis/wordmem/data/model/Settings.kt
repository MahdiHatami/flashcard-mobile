package com.mutlak.metis.wordmem.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Settings(
        @PrimaryKey private var id: Int = 1,
        var reviewLimit: Int = 0,
        var quizLimit: Int = 0,
        var maxAnswers: Int = 0,
        var lastFetchDate: String? = null,
        var quizType: Int = 0,
        var reminderDate: Long = 0,
        var isReminderActive: Boolean = false
) : RealmObject() {
//    var getReminderDate: Time? = null
//        get() {
//            if (reminderDate != null)
//                return Time(reminderDate)
//            return null
//        }
}
