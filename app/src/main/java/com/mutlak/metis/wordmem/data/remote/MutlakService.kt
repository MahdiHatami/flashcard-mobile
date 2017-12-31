package com.mutlak.metis.wordmem.data.remote


import com.mutlak.metis.wordmem.data.model.Word
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface MutlakService {

  @GET("kelimeEzber/api.php")
  fun getWords(@Query("date") date: String): Single<List<Word>>

  @Multipart
  @POST("kelimeEzber/sendWord.php")
  fun sendWord(@Part file: MultipartBody.Part, @Part("body") result: Word): Single<ResponseBody>

}


