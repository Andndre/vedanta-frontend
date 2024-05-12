package com.example.brahmacarlearning.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.brahmacarlearning.data.BabRemoteMediator
import com.example.brahmacarlearning.data.local.database.BabDatabase
import com.example.brahmacarlearning.data.local.pref.ChatModel
import com.example.brahmacarlearning.data.local.pref.UserModel
import com.example.brahmacarlearning.data.local.pref.UserPreference
import com.example.brahmacarlearning.data.remote.api.ApiConfig
import com.example.brahmacarlearning.data.remote.api.ApiService
import com.example.brahmacarlearning.data.remote.response.BabsItem
import com.example.brahmacarlearning.data.remote.response.ChatResponse
import com.example.brahmacarlearning.data.remote.response.DetailGitaResponse
import com.example.brahmacarlearning.data.remote.response.ErrorResponse
import com.example.brahmacarlearning.data.remote.response.GetChatBySessionResponse
import com.example.brahmacarlearning.data.remote.response.GitaResponse
import com.example.brahmacarlearning.data.remote.response.HistoryItem
import com.example.brahmacarlearning.data.remote.response.LoginResponse
import com.example.brahmacarlearning.data.remote.response.RegisterResponse
import com.example.brahmacarlearning.data.remote.response.SessionChatResponse
import com.example.brahmacarlearning.data.remote.response.SessionsItem
import com.example.brahmacarlearning.data.remote.response.SessionsResponse
import com.example.brahmacarlearning.data.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val babDatabase: BabDatabase
){

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            if (!registerResponse.error) {
                emit(Result.Success(registerResponse))
            } else {
                emit(Result.Error(registerResponse.message ?: "Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            if (!loginResponse.error) {
                val user = UserModel(
                    email = email,
                    token = loginResponse.token,
                    isLogin = true
                )
                ApiConfig.token = loginResponse.token
                userPreference.saveSession(user)
                emit(Result.Success(loginResponse))
            } else {
                emit(Result.Error("Error : ${loginResponse.error}" ))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }

//    fun getIdSessionChat() : LiveData<Result<SessionChatResponse>> = liveData {
//        emit(Result.Loading)
//        val user = runBlocking { userPreference.getSession().first() }
//        val response = ApiConfig.getApiService(user.token)
//        try {
//            val sessionChatResponse = response.getIdSessionChat()
//            if (sessionChatResponse != null) {
//                emit(Result.Success(sessionChatResponse))
//            } else {
//                emit(Result.Error("Error"))
//            }
//        } catch (e: HttpException) {
//            emit(Result.Error("Network error: ${e.message()}"))
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//            val errorMessage = errorBody.message
//            emit(Result.Error("Get Session Chat Failed: $errorMessage"))
//        } catch (e: Exception) {
//            // General Exception handling to catch other possible errors.
//            emit(Result.Error("An unexpected error occurred: ${e.localizedMessage}"))
//        }
//    }
//
//    fun chat(id: String, message: String): LiveData<Result<ChatResponse>> = liveData {
//        emit(Result.Loading)
//        val user = runBlocking { userPreference.getSession().first() }
//        val response = ApiConfig.getApiService(user.token)
//        try {
//            val chatResponse = response.chat(id, message)
//            if (!chatResponse.error) {
//                emit(Result.Success(chatResponse))
//            } else {
//                emit(Result.Error("Error from server"))
//            }
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//            val errorMessage = errorBody.message
//            emit(Result.Error("API call failed: $errorMessage"))
//        } catch (e: Exception) {
//            emit(Result.Error("An unexpected error occurred: ${e.message}"))
//        }
//    }

//    fun getIdSessionChat(): LiveData<Result<SessionChatResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val user = userPreference.getSession().first()
//            val sessionChatResponse = ApiConfig.getApiService(user.token).getIdSessionChat()
//            emit(Result.Success(sessionChatResponse))
//        } catch (e: HttpException) {
//            val errorMessage = e.response()?.errorBody()?.string()?.let {
//                Gson().fromJson(it, ErrorResponse::class.java).message
//            } ?: e.localizedMessage
//            emit(Result.Error("Get Session Chat Failed: $errorMessage"))
//        } catch (e: Exception) {
//            emit(Result.Error("An unexpected error occurred: ${e.localizedMessage}"))
//        }
//    }

    fun parseDetailChatJson(jsonString: String): List<HistoryItem> {
        val gson = Gson()
        val getChatBySessionResponse: GetChatBySessionResponse = gson.fromJson(jsonString, GetChatBySessionResponse::class.java)
        return getChatBySessionResponse.session.history
    }

    fun getDetailChat(id: String): Flow<Result<List<HistoryItem>>> = flow {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first()
            val response = ApiConfig.getApiService(user.token).getChatDetail(id)
            if (!response.error) {
                // Asumsikan response memiliki struktur sesuai dengan JSON yang Anda bagikan
                val historyItems = response.session.history // Ambil list history dari response
                emit(Result.Success(historyItems))
            } else {
                emit(Result.Error("Error fetching chat details"))
            }
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } ?: e.localizedMessage ?: "Unknown HTTP error"
            emit(Result.Error("Get Chat Detail Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    fun getSessionsChat() :LiveData<Result<List<SessionsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first()
            val response = ApiConfig.getApiService(user.token)
            val sessionsResponse = response.getChatBySession()
            val sessionList = parseSessionsJson(Gson().toJson(sessionsResponse))
            emit(Result.Success(sessionList))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } ?: e.localizedMessage
            emit(Result.Error("Get Session Chat Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.localizedMessage}"))
        }
    }

    fun parseSessionsJson(jsonString: String): List<SessionsItem> {
        val gson = Gson()
        val sessionsResponse: SessionsResponse = gson.fromJson(jsonString, SessionsResponse::class.java)
        return sessionsResponse.sessions
    }

    //    fun parseGitaJson(jsonString: String): List<BabsItem> {
//        val gson = Gson()
//        // Gunakan tipe Array<GitaResponseItem> untuk deserialisasi langsung array JSON ke List<GitaResponseItem>
//        val gitaArray: Array<GitaResponseItem> = gson.fromJson(jsonString, Array<GitaResponseItem>::class.java)
//        return gitaArray.toList() // Konversi array ke list
//    }


    suspend fun getIdSessionChat(): Flow<Result<SessionChatResponse>> = flow {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first() // Ensure this is a Flow and first() is used correctly
            val sessionChatResponse = ApiConfig.getApiService(user.token).getIdSessionChat()
            emit(Result.Success(sessionChatResponse))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } ?: e.localizedMessage
            emit(Result.Error("Get Session Chat Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun chat(id: String, message: String): Flow<Result<ChatResponse>> = flow {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first() // Again, ensure getSession() returns a Flow
            val chatResponse = ApiConfig.getApiService(user.token).chat(id, message)
            if (!chatResponse.error) {
                emit(Result.Success(chatResponse))
            } else {
                emit(Result.Error("Error")) // Assuming there's an errorMessage field
            }
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java).message
            } ?: e.localizedMessage
            emit(Result.Error("API call failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }



//    fun parseGitaJson(jsonString: String): List<BabsItem> {
//        val gson = Gson()
//        // Gunakan tipe Array<GitaResponseItem> untuk deserialisasi langsung array JSON ke List<GitaResponseItem>
//        val gitaArray: Array<GitaResponseItem> = gson.fromJson(jsonString, Array<GitaResponseItem>::class.java)
//        return gitaArray.toList() // Konversi array ke list
//    }
//
//    fun getGita(): LiveData<Result<List<GitaResponseItem>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val user = userPreference.getSession().first()
//            val response = ApiConfig.getApiService(user.token)
//            val gitaResponseJson = Gson().toJson(response.getGita())
//            val gitaList = parseGitaJson(gitaResponseJson)
//            emit(Result.Success(gitaList))
//        } catch (e: Exception) {
//            emit(Result.Error(e.message ?: "Error occurred"))
//        }
//    }

    fun parseGitaJson(jsonString: String): List<BabsItem> {
        val gson = Gson()
        val gitaResponse: GitaResponse = gson.fromJson(jsonString, GitaResponse::class.java)
        return gitaResponse.babs
    }

//    fun getGita(): LiveData<Result<List<BabsItem>>> = liveData {
//        emit(Result.Loading)
//        try {
//            val user = userPreference.getSession().first()
//            val response = ApiConfig.getApiService(user.token)
//            val gitaResponse = response.getGita()
//            val gitaList = parseGitaJson(Gson().toJson(gitaResponse))
//            emit(Result.Success(gitaList))
//        } catch (e: Exception) {
//            emit(Result.Error(e.message ?: "Error occurred"))
//        }
//    }

    fun getBabPaging(): LiveData<PagingData<BabsItem>> {
        val user = runBlocking { userPreference.getSession().first() }
        val response = ApiConfig.getApiService(user.token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = BabRemoteMediator(babDatabase, response),
            pagingSourceFactory = {
                babDatabase.babDao().getAllBab()
            }
        ).liveData
    }

    fun getDetailGita(bab: String) : LiveData<Result<DetailGitaResponse>> = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val detailGitaResponse = response.getDetailGita(bab)
            if(detailGitaResponse !== null) {
                emit(Result.Success(detailGitaResponse))
            } else {
                emit(Result.Error("Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            Log.d("Detail: ", errorMessage.toString())
            emit(Result.Error("Loading Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            babDatabase: BabDatabase
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService, babDatabase)
            }.also { instance = it }
    }
}
