package com.mutlak.metis.wordmem.data.model

import io.realm.RealmObject

open class Sentense(
        var id: Int? = 0,
        var name: String? = null
) : RealmObject() {
    fun getTitle(): String? {
        return name
    }
    // The Kotlin compiler generates standard getters and setters.
    // Realm will overload them and code inside them is ignored.
    // So if you prefer you can also just have empty abstract methods.
}
