package com.example.brahmacarlearning.data.remote.api

import com.example.brahmacarlearning.data.remote.response.ChatResponse
import com.example.brahmacarlearning.data.remote.response.DetailGitaResponse
import com.example.brahmacarlearning.data.remote.response.GetChatBySessionResponse
import com.example.brahmacarlearning.data.remote.response.GitaResponse
import com.example.brahmacarlearning.data.remote.response.HistoryItem
import com.example.brahmacarlearning.data.remote.response.LoginResponse
import com.example.brahmacarlearning.data.remote.response.RegisterResponse
import com.example.brahmacarlearning.data.remote.response.SessionChatResponse
import com.example.brahmacarlearning.data.remote.response.SessionsItem
import com.example.brahmacarlearning.data.remote.response.SessionsResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

//    @GET("gita")
//    suspend fun  getGita() :GitaResponse

    @GET("gita")
    suspend fun  getGita(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ) :GitaResponse

    @GET("gita/bab/{bab}")
    suspend fun getDetailGita(
        @Path("bab") bab: String
    ) :DetailGitaResponse

    @FormUrlEncoded
    @POST("chat/session/{id}/chat")
    suspend fun chat(
        @Path("id") id: String,
        @Field("message") message: String
    ) : ChatResponse

    @GET("chat/sessions/create")
    suspend fun getIdSessionChat() : SessionChatResponse

    @GET("chat/sessions")
    suspend fun getChatBySession() : SessionsResponse

    @GET("chat/session/{id}")
    suspend fun getChatDetail(
        @Path("id") id: String
    ): GetChatBySessionResponse
}
