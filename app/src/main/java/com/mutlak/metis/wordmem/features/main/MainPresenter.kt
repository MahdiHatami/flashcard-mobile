package com.mutlak.metis.wordmem.features.main

import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.util.rx.scheduler.SchedulerUtils
import javax.inject.Inject

@ConfigPersistent
class MainPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<MainMvpView>() {

    override fun attachView(mvpView: MainMvpView) {
        super.attachView(mvpView)
    }

    fun getPokemon(limit: Int) {
        checkViewAttached()
        view?.showProgress()
        mDataManager.getPokemonList(limit)
                .compose(SchedulerUtils.ioToMain<List<String>>())
                .subscribe({ pokemons ->
                    view?.hideProgress()
                    view?.showPokemon(pokemons)
                }) { throwable ->
                    view?.hideProgress()
                    view?.showError(throwable)
                }
    }

}
