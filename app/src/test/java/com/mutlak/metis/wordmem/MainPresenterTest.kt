package com.mutlak.metis.wordmem

import com.mutlak.metis.wordmem.common.TestDataFactory
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.features.main.MainMvpView
import com.mutlak.metis.wordmem.features.main.MainPresenter
import com.mutlak.metis.wordmem.util.RxSchedulersOverrideRule
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainPresenterTest {

    @Mock lateinit var mMockMainMvpView: MainMvpView
    @Mock lateinit var mMockDataManager: DataManager
    private var mMainPresenter: MainPresenter? = null

    @JvmField
    @Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun setUp() {
        mMainPresenter = MainPresenter(mMockDataManager)
        mMainPresenter?.attachView(mMockMainMvpView)
    }

    @After
    fun tearDown() {
        mMainPresenter?.detachView()
    }

    @Test
    @Throws(Exception::class)
    fun getPokemonReturnsPokemonNames() {
        val pokemonList = TestDataFactory.makePokemonNamesList(10)
        `when`(mMockDataManager.getPokemonList(10))
                .thenReturn(Single.just(pokemonList))

        mMainPresenter?.getPokemon(10)

        verify<MainMvpView>(mMockMainMvpView, times(2)).showProgress()
        verify<MainMvpView>(mMockMainMvpView).showPokemon(pokemonList)
        verify<MainMvpView>(mMockMainMvpView, never()).showError(RuntimeException())

    }

    @Test
    @Throws(Exception::class)
    fun getPokemonReturnsError() {
        `when`(mMockDataManager.getPokemonList(10))
                .thenReturn(Single.error<List<String>>(RuntimeException()))

        mMainPresenter?.getPokemon(10)

        verify<MainMvpView>(mMockMainMvpView, times(2)).hideProgress()
//        verify<MainMvpView>(mMockMainMvpView).showError(RuntimeException())
        verify<MainMvpView>(mMockMainMvpView, never()).showPokemon(ArgumentMatchers.anyList<String>())
    }

}