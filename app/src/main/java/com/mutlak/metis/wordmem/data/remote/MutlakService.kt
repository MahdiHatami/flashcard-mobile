package com.mutlak.metis.wordmem.data.remote


import com.mutlak.metis.wordmem.data.model.*
import io.reactivex.*
import okhttp3.*
import retrofit2.http.*


interface MutlakService {

  @GET("pokemon")
  fun getPokemonList(@Query("limit") limit: Int): Single<PokemonListResponse>

  @GET("pokemon/{name}")
  fun getPokemon(@Path("name") name: String): Single<Pokemon>

  @GET("kelimeEzber/api.php")
  fun getWords(@Query("date") date: String): Single<List<Word>>

  @Multipart
  @POST("kelimeEzber/sendWord.php")
  fun sendWord(@Part file: MultipartBody.Part, @Part("body") result: Word): Single<ResponseBody>

}


