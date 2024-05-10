package com.example.brahmacarlearning.view.user.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brahmacarlearning.data.local.pref.ChatModel
import com.example.brahmacarlearning.data.local.pref.DrawerModel
import com.example.brahmacarlearning.data.local.pref.SessionPreferences
import com.example.brahmacarlearning.data.remote.response.ChatResponse
import com.example.brahmacarlearning.repository.Repository
import kotlinx.coroutines.launch
import com.example.brahmacarlearning.data.result.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel(private val repository: Repository, private val sessionPrefs: SessionPreferences): ViewModel() {

//    private val _listMessage = MutableLiveData<List<ChatModel>>(emptyList())
//    val listMessage: LiveData<List<ChatModel>> = _listMessage
//
//    fun add(newMessage: ChatModel) {
//        val updatedList = ArrayList(_listMessage.value ?: emptyList())
//        updatedList.add(newMessage)
//        _listMessage.value = updatedList
//    }
//
//    fun getIdSessionChat() = repository.getIdSessionChat()
//
//    fun getMessage(id: String, message: String) = repository.chat(id, message)


    private val _listMessage = MutableLiveData<List<ChatModel>>(emptyList())
    val listMessage: LiveData<List<ChatModel>> = _listMessage

    private val _listChatBySession = MutableLiveData<List<DrawerModel>>(emptyList())
    val listChatBySession: LiveData<List<DrawerModel>> = _listChatBySession

    private val _sessionId = MutableLiveData<String>()
    val sessionId: LiveData<String> = _sessionId

    private val _chatResult = MutableLiveData<Result<ChatResponse>>()
    val chatResult: LiveData<Result<ChatResponse>> = _chatResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

//        init {
//            fetchSessionChat()
//        }

//    fun addChatBySession(newDrawerModel: DrawerModel) {
//        // Mengambil list saat ini dan membuat salinan mutable darinya
//        val currentList = _listChatBySession.value?.toMutableList() ?: mutableListOf()
//
//        // Mencari index dari DrawerModel dengan id yang sama di list saat ini
//        val index = currentList.indexOfFirst { it.sessionId == newDrawerModel.sessionId }
//
//        if (index != -1) {
//            currentList[index] = newDrawerModel
//        } else {
//            currentList.add(newDrawerModel)
//        }
//
//        // Meng-update LiveData dengan list yang sudah dimodifikasi
//        _listChatBySession.value = currentList
//    }

    fun addMessage(newMessage: ChatModel) {
        val updatedList = ArrayList(_listMessage.value ?: emptyList())
        updatedList.add(newMessage)
        _listMessage.value = updatedList
    }

//    private fun fetchSessionChat() {
//        val currentSessionId = _sessionId.value
//        if (currentSessionId.isNullOrEmpty()) {
//            viewModelScope.launch {
//                repository.getIdSessionChat().collect { result ->
//                    when (result) {
//                        is Result.Success -> {
//                            val sessionId = result.data.sessionId
//                            _sessionId.value = sessionId
//
//                            val title = "Chat Session"
//
////                            addChatBySession(DrawerModel(
////                                sessionId = sessionId,
////                                title = title
////                            ))
//                        }
//                        else -> {}
//                    }
//                }
//            }
//        }
//    }

    fun getSessions() = repository.getSessionsChat()
    fun fetchHistoryChat(sessionId: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            repository.getDetailChat(sessionId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _isLoading.postValue(false)
                        val historyChatModels = result.data.map { historyItem ->
                            ChatModel(
                                sender = if (historyItem.role == "user") "Me" else "Ganesh Bot",
                                message = historyItem.parts,
                                date = convertTimestampToDate()
                            )
                        }
                        _listMessage.value = historyChatModels
                    }

                    else -> {}
                }
            }
        }
    }

    fun convertTimestampToDate(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

//    var detailChatSessionId: String? = null
//
//    fun sendMessage(message: String)
//    {
//        if (message.isBlank()) return
//
//        // Immediately add the user's message to the chat list
//        // Update UI accordingly...
//        addMessage(ChatModel(sender = "Me", message = message, date = convertTimestampToDate()))
//        // Check for an existing session ID in preferences or detail chat
//        val savedSessionId = sessionPrefs.getSessionId() ?: detailChatSessionId
//
//        if (savedSessionId.isNullOrEmpty()) {
//            requestNewSessionIdAndSendMessage(message)
//        } else {
//            // If a session ID exists, use it to send the message
//            sendChatMessage(savedSessionId, message)
//        }
//    }
//
//    private fun requestNewSessionIdAndSendMessage(message: String) {
//        viewModelScope.launch {
//            repository.getIdSessionChat().collect { result ->
//                when (result) {
//                    is Result.Success -> {
//                        val newSessionId = result.data.sessionId
//
//                        sessionPrefs.saveSessionId(newSessionId)
//
//                        // Now that we have a session ID, send the message
//                        sendChatMessage(newSessionId, message)
//                    }
//
//                    is Result.Error -> {
//                        // Handle error
//                    }
//
//                    is Result.Loading -> {
//                        // Optionally handle loading state
//                    }
//                }
//            }
//        }
//    }
//
//    private fun sendChatMessage(sessionId: String, message: String) {
//        viewModelScope.launch {
//            repository.chat(sessionId, message).collect { result ->
//                when (result) {
//                    is Result.Success -> {
//                        result.data.response?.let { botResponse ->
//                            addMessage(
//                                ChatModel(
//                                    sender = "Ganesh Bot",
//                                    message = botResponse,
//                                    date = convertTimestampToDate()
//                                )
//                            )
//                        }
//                    }
//
//                    is Result.Error -> {
//                        // Handle error
//                    }
//
//                    is Result.Loading -> {
//                        // Optionally handle loading state
//                    }
//                }
//            }
//        }
//    }
//}




    var detailChatSessionId: String? = null

    fun sendMessage(message: String) {
        val sessionIdValue = _sessionId.value ?: detailChatSessionId
        if (message.isBlank()) return

        addMessage(ChatModel(sender = "Me", message = message, date = convertTimestampToDate()))

        if (sessionIdValue.isNullOrEmpty()) {
            requestNewSessionId(message)
        } else {
            sendChatMessage(sessionIdValue, message)
        }
    }

    private fun requestNewSessionId(message: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            repository.getIdSessionChat().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _isLoading.postValue(false)
                        _sessionId.postValue(result.data.sessionId)
                        sendChatMessage(result.data.sessionId, message)
                    }

                    is Result.Error -> {
                    }

                    is Result.Loading -> {
                    }
                }
            }
        }
    }

    private fun sendChatMessage(sessionId: String, message: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            repository.chat(sessionId, message).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _isLoading.postValue(false)
                        result.data.response?.let { botResponse ->
                            addMessage(
                                ChatModel(
                                    sender = "Ganesh Bot",
                                    message = botResponse,
                                    date = convertTimestampToDate()
                                )
                            )
                        }
                    }
                    is Result.Error -> {

                    }

                    is Result.Loading -> {

                    }
                }
            }
        }
    }
}

