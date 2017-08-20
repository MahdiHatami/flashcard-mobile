package com.mutlak.metis.wordmem.features.detail

import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.model.Pokemon
import com.mutlak.metis.wordmem.features.base.BasePresenter
import com.mutlak.metis.wordmem.injection.ConfigPersistent
import com.mutlak.metis.wordmem.util.rx.scheduler.SchedulerUtils
import javax.inject.Inject

@ConfigPersistent
class DetailPresenter @Inject
constructor(private val mDataManager: DataManager) : BasePresenter<DetailMvpView>() {

    override fun attachView(view: DetailMvpView) {
        super.attachView(view)
    }

    fun getPokemon(name: String) {
        checkViewAttached()
        view?.showProgress()
        mDataManager.getPokemon(name)
                .compose<Pokemon>(SchedulerUtils.ioToMain<Pokemon>())
                .subscribe({ pokemon ->
                    // It should be always checked if BaseView (Fragment or Activity) is attached.
                    // Calling showProgress() on a not-attached fragment will throw a NPE
                    // It is possible to ask isAdded() in the fragment, but it's better to ask in the presenter
                    view?.hideProgress()
                    view?.showPokemon(pokemon)
                    for (statistic in pokemon.stats) {
                        view?.showStat(statistic)
                    }
                }) { throwable ->
                    view?.hideProgress()
                    view?.showError(throwable)
                }
    }
}