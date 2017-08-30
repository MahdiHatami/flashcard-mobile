package com.mutlak.metis.wordmem.features.quiz

import com.mutlak.metis.wordmem.data.local.*
import com.mutlak.metis.wordmem.data.model.*
import com.mutlak.metis.wordmem.features.base.*
import com.mutlak.metis.wordmem.injection.*
import java.util.*
import javax.inject.*


@ConfigPersistent
class QuizPresenter @Inject constructor(
    private val repo: WordsRepositoryImpl) : BasePresenter<QuizView>() {
  private var wordList: List<Word>? = null

  override fun attachView(view: QuizView) {
    super.attachView(view)
  }

  override fun detachView() {
    super.detachView()
  }

  fun setupTest() {
    val settings = repo.settings
    wordList = repo.getQuizWords(settings.quizLimit)
    val allWords = repo.allWords

    val questions = ArrayList<Question>()

    for (word in wordList!!) {
      val question = Question(word)
      question.answers = generateAnswers(word, settings, allWords)

      questions.add(question)
    }

    if (questions.size > 0) {
      val session = ExamSession()
      session.questions = questions
      session.time = 120

      view?.initQuestionPager(session)
    } else {
      view?.redirectBack()
    }
  }

  private fun generateAnswers(correctAnswer: Word, settings: Settings,
      allWords: List<Word>): List<Answer> {

    val answers = ArrayList<Answer>()
    answers.add(addCorrectAnswer(correctAnswer))

    for (j in 0..settings.maxAnswers.minus(2)) {
      answers.add(getWrongAnswer(answers, allWords))
    }
    val seed = System.nanoTime()
    Collections.shuffle(answers, Random(seed))
    return answers
  }

  private fun getWrongAnswer(answers: List<Answer>, allWords: List<Word>): Answer {
    val random = Random()
    val answer = Answer()

    val i = random.nextInt(allWords.size)

    val a = answers.none { it.word?.id == allWords[i].id }
    if (a) {
      answer.word = allWords[i]
    } else {
      return getWrongAnswer(answers, allWords)
    }

    return answer
  }

  private fun addCorrectAnswer(word: Word): Answer {
    val answer = Answer()
    for (j in wordList!!.indices) {
      if (wordList!![j].id == word.id) {
        answer.word = word
        answer.isCorrect = true
        break
      }
    }
    return answer
  }
}
