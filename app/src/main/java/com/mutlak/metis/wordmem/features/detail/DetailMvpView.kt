package com.mutlak.metis.wordmem.features.detail

import com.mutlak.metis.wordmem.data.model.Pokemon
import com.mutlak.metis.wordmem.data.model.Statistic
import com.mutlak.metis.wordmem.features.base.MvpView

interface DetailMvpView : MvpView {

    fun showPokemon(pokemon: Pokemon)

    fun showStat(statistic: Statistic)

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

}