package com.mutlak.metis.wordmem.data.remote


import com.mutlak.metis.wordmem.data.model.Pokemon
import com.mutlak.metis.wordmem.data.model.PokemonListResponse
import com.mutlak.metis.wordmem.data.model.Word
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MutlakService {

    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun getPokemon(@Path("name") name: String): Single<Pokemon>

    @GET("kelimeEzber/api.php") abstract
    fun getWords(@Query("date") date: String): Single<List<Word>>
}
