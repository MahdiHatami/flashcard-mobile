package com.mutlak.metis.wordmem

import com.mutlak.metis.wordmem.common.*
import com.mutlak.metis.wordmem.data.*
import com.mutlak.metis.wordmem.data.remote.*
import com.mutlak.metis.wordmem.util.*
import io.reactivex.*
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.*

@RunWith(MockitoJUnitRunner::class)
class DataManagerTest {

    @Rule @JvmField val mOverrideSchedulersRule = RxSchedulersOverrideRule()
    @Mock lateinit var mMockMutlakService: MutlakService

    private var mDataManager: DataManager? = null

    @Before
    fun setUp() {
        mDataManager = DataManager(mMockMutlakService)
    }

    @Test
    fun getWordsListCompletesAndEmitsPokemonList() {
        val wordList = TestDataFactory.makeWordList(5)
        val wordListResponse = wordList

        `when`(mMockMutlakService.getWords(anyString()))
                .thenReturn(Single.just(wordListResponse))

        mDataManager?.getWords(anyString())
                ?.test()
                ?.assertComplete()
                ?.assertValue(TestDataFactory.makeWordList(5))
    }

    @Test
    fun getPokemonCompletesAndEmitsPokemon() {
        val name = "charmander"
        val pokemon = TestDataFactory.makeWord(name)
        `when`(mMockMutlakService.getPokemon(anyString()))
                .thenReturn(Single.just(pokemon))

        mDataManager?.getPokemon(name)
                ?.test()
                ?.assertComplete()
                ?.assertValue(pokemon)
    }
}
