package com.mutlak.metis.wordmem.util

import com.mutlak.metis.wordmem.common.TestDataFactory
import com.mutlak.metis.wordmem.data.DataManager
import com.mutlak.metis.wordmem.data.local.WordsRepositoryImpl
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.data.remote.MutlakService
import com.mutlak.metis.wordmem.features.landing.LandingMvpView
import com.mutlak.metis.wordmem.features.landing.LandingPresenter
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class LandingPresenterTest {
    @Mock lateinit var mMockiLandingView: LandingMvpView
    @Mock lateinit var mMockRepository: WordsRepositoryImpl
    @Mock lateinit var mMockMutlakService: MutlakService
    @Mock lateinit var mMockDataManager: DataManager
    private var mLandingPresenter: LandingPresenter? = null

    @JvmField
    @Rule
    val mOverrideSchedulersRule = RxSchedulersOverrideRule()

    @Before
    fun setUp() {
        mLandingPresenter = LandingPresenter(mMockRepository, mMockDataManager)
        mLandingPresenter?.attachView(mMockiLandingView)
    }

    @After
    fun tearDown() {
        mLandingPresenter?.detachView()
    }

    @Test
    fun settingsShouldNotBeNull() {
        val settings = mLandingPresenter?.getSettings()
        Assert.assertNotEquals(0, settings?.reviewLimit)
        Assert.assertNotEquals(0, settings?.maxAnswers)
        Assert.assertNotEquals(0, settings?.quizLimit)
        Assert.assertNotEquals(0, settings?.quizType)
        Assert.assertNotEquals("", settings?.lastFetchDate)
    }

    @Test
    fun shouldUpdateFetchedDateForLoadingWords() {
        val words = TestDataFactory.makeListRibots(3)
        val obWords = Single.just<List<Word>>(words)
        val date = Date().toString()

        `when`(mMockDataManager.getWords(date))
                .thenReturn(obWords)

        mLandingPresenter?.loadWords(date)

        verify<LandingMvpView>(mMockiLandingView).showProgress()
        //verify(mMockLandingView, never()).showWordsEmpty();
        //verify(mMockLandingView, never()).showErrorMessage();
    }

}
