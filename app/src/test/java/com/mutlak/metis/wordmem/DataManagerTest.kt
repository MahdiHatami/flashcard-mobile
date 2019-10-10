package com.mutlak.metis.wordmem

import com.mutlak.metis.wordmem.common.TestDataFactory
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.remote.MutlakService
import com.mutlak.metis.wordmem.util.RxSchedulersOverrideRule
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

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
        ?.assertValue(wordListResponse)
  }

}
