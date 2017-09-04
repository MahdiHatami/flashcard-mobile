package com.mutlak.metis.wordmem

import com.mutlak.metis.wordmem.common.*
import com.mutlak.metis.wordmem.data.*
import com.mutlak.metis.wordmem.data.local.*
import com.mutlak.metis.wordmem.features.landing.*
import com.mutlak.metis.wordmem.util.*
import io.reactivex.*
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.*
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class LandingPresenterTest {

  @Mock lateinit var mMockLandingView: LandingMvpView
  @Mock lateinit var mMockDataManager: DataManager
  @Mock lateinit var mMockRepository: WordsRepositoryImpl
  private var mLandingPresenter: LandingPresenter? = null

  @JvmField
  @Rule
  val mOverrideSchedulersRule = RxSchedulersOverrideRule()

  @Before
  fun setUp() {
    mLandingPresenter = LandingPresenter(mMockRepository, mMockDataManager)
    mLandingPresenter?.attachView(mMockLandingView)
  }

  @After
  fun tearDown() {
    mLandingPresenter?.detachView()
  }

  @Test
  fun settingsShouldNotBeNull() {
    val settings = mLandingPresenter?.getSettings()!!

    Assert.assertNotEquals(0, settings.reviewLimit)
    Assert.assertNotEquals(0, settings.maxAnswers)
    Assert.assertNotEquals(0, settings.quizLimit)
    Assert.assertNotEquals(0, settings.quizType)
    Assert.assertNotEquals("", settings.lastFetchDate)
  }

  @Test
  fun shouldUpdateFetchedDateForLoadingWords() {
    val words = TestDataFactory.makeWordList(3)
    val obWords = Single.just(words)
    val date = Date().toString()

    `when`(mMockDataManager.getWords(date)).thenReturn(obWords)

    mLandingPresenter?.loadWords(date)

    verify(mMockLandingView).showBookLoading()
    //verify(mMockLandingView, never()).showErrorMessage();
  }

  @Test
  fun shouldShowNewWordsCount() {
    `when`(mMockRepository.newWordsCount).thenReturn(Integer.parseInt("1"))
    mLandingPresenter?.getNewWordsCount()
    verify(mMockLandingView).showNewCount(anyInt())
  }

  @Test
  fun shouldShowBookmardWordsCount() {
    `when`(mMockRepository.bookMarkWordsCount).thenReturn(Integer.parseInt("1"))
    mLandingPresenter?.getBookmarkWordsCount()
    verify(mMockLandingView).showBookmarkCount(anyInt())
  }

  @Test
  fun shouldShowLearntWordsCount() {
    `when`(mMockRepository.learntWordsCount).thenReturn(Integer.parseInt("1"))
    mLandingPresenter?.getLearntWordsCount()
    verify(mMockLandingView).showLearntCount(anyInt())
  }

}
