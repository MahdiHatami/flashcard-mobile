package com.mutlak.metis.wordmem.features.main

import com.mutlak.metis.wordmem.features.base.MvpView

interface MainMvpView : MvpView {

    fun showPokemon(pokemon: List<String>)

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

}