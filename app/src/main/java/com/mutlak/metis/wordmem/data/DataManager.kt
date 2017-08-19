package com.mutlak.metis.wordmem.data

import com.mutlak.metis.wordmem.data.model.Pokemon
import com.mutlak.metis.wordmem.data.model.Word
import com.mutlak.metis.wordmem.data.remote.MutlakService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject
constructor(private val mutlakService: MutlakService) {

    fun getPokemonList(limit: Int): Single<List<String>> {
        return mutlakService.getPokemonList(limit)
                .toObservable()
                .flatMapIterable { (results) -> results }
                .map { (name) -> name }
                .toList()
    }

    fun getPokemon(name: String): Single<Pokemon> {
        return mutlakService.getPokemon(name)
    }

    fun getWords(fetchDate: String): Single<List<Word>> {
        return mutlakService.getWords(fetchDate)
    }


}
