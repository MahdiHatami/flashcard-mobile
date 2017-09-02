package com.mutlak.metis.wordmem.features.detail

import com.mutlak.metis.wordmem.data.model.Pokemon
import com.mutlak.metis.wordmem.data.model.Statistic
import com.mutlak.metis.wordmem.features.base.BaseView

interface DetailMvpView : BaseView {

  fun showPokemon(pokemon: Pokemon)

  fun showStat(statistic: Statistic)

  fun showError(error: Throwable)

}
