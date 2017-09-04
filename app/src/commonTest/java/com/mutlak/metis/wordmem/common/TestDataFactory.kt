package com.mutlak.metis.wordmem.common

import com.mutlak.metis.wordmem.data.model.*
import java.util.*

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
object TestDataFactory {

  private val sRandom = Random()

  fun randomUuid(): String {
    return UUID.randomUUID().toString()
  }

  fun makeWord(id: String): Pokemon {
    return Pokemon(id, randomUuid() + id, makeSprites(), makeStatisticList(3))
  }

  fun makeWordList(count: Int): List<Word> {
    return (0 until count).map { makeWord() }
  }

  fun makePokemonNameList(pokemonList: List<NamedResource>): List<String> {
    val names = pokemonList.map { it.name }
    return names
  }

  fun makeStatistic(): Statistic {
    val statistic = Statistic()
    statistic.baseStat = sRandom.nextInt()
    statistic.stat = makeNamedResource(randomUuid())
    return statistic
  }

  fun makeStatisticList(count: Int): List<Statistic> {
    val statisticList = ArrayList<Statistic>()
    for (i in 0..count - 1) {
      statisticList.add(makeStatistic())
    }
    return statisticList
  }

  fun makeSprites(): Sprites {
    val sprites = Sprites()
    sprites.frontDefault = randomUuid()
    return sprites
  }

  private fun makeNamedResource(unique: String): NamedResource {
    return NamedResource(randomUuid() + unique, randomUuid())
  }

  fun makeNamedResourceList(count: Int): List<NamedResource> {
    return (0 until count).map { makeNamedResource(it.toString()) }
  }

  fun makeListRibots(number: Int): List<Word> {
    val words = arrayListOf<Word>()
    var i = 0
    while (i < number) {
      words.add(makeWord())
      i++
    }
    return words
  }

  private fun makeWord(): Word {
    return Word(english = "abandon",
        meaning = "to leave someone, especially someone you are responsible for",
        turkish = "terk etmek", type = "v")
  }
}
