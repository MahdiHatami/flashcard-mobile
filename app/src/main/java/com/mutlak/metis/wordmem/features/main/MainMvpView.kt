package com.mutlak.metis.wordmem.features.main

import com.mutlak.metis.wordmem.features.base.BaseView

interface MainMvpView : BaseView {

    fun showPokemon(pokemon: List<String>)

    fun showError(error: Throwable)

}
