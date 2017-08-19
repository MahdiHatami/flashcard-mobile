package com.mutlak.metis.wordmem.data.model

import io.realm.RealmObject

open class Sentense(
    var id: Int? = 0,
    var title: String? = null
) : RealmObject()
