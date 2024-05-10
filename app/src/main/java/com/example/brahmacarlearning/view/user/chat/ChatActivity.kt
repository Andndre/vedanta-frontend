package com.example.brahmacarlearning.view.user.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brahmacarlearning.R
import com.example.brahmacarlearning.ViewModelFactory
import com.example.brahmacarlearning.data.local.pref.ChatModel
import com.example.brahmacarlearning.data.remote.response.HistoryItem
import com.example.brahmacarlearning.data.remote.response.SessionsItem
import com.example.brahmacarlearning.data.result.Result
import com.example.brahmacarlearning.databinding.ActivityChatBinding
import com.example.brahmacarlearning.view.user.adapter.ChatAdapter
import com.example.brahmacarlearning.view.user.adapter.DrawerAdapter
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var sessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupRecyclerViewChat()
//        setupDrawerRecyclerView()
        getSessionsChat()
        setupChatting()
        chatting()
//        observeChatResult()
        binding.sendButton.setOnClickListener { chatting() }

        sessionId = intent.getStringExtra(ID)

        if (sessionId != null) {
            viewModel.detailChatSessionId = sessionId
            viewModel.fetchHistoryChat(sessionId.toString())
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.newChat.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }

    }

    private fun displayChatHistory(chatHistory: List<ChatModel>) {
        val adapter = ChatAdapter(this@ChatActivity)
        binding.chatRecyclerView.adapter = adapter
        adapter.submitList(chatHistory)
        showLoading(false)
    }

    private fun setupRecyclerViewChat() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ChatAdapter(this@ChatActivity)
        binding.chatRecyclerView.adapter = adapter

        viewModel.listMessage.observe(this) { messages ->
            adapter.submitList(ArrayList(messages))
            binding.chatRecyclerView.smoothScrollToPosition(messages.size)
            showLoading(false)
        }
    }

//    private fun setupDrawerRecyclerView() {
//        val drawerRecyclerView = binding.navView.findViewById<RecyclerView>(R.id.nav_header_recyclerView) // Ganti dengan ID RecyclerView Anda di dalam drawer
//        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Inisialisasi DrawerAdapter
//        val drawerAdapter = DrawerAdapter(this@ChatActivity)
//        drawerRecyclerView.adapter = drawerAdapter
//
//        // Mengamati LiveData dari ViewModel dan mengupdate adapter ketika data berubah
//        viewModel.listChatBySession.observe(this) { drawerItems ->
//            drawerAdapter.submitList(drawerItems)
//        }
//    }

//    private fun setupDrawerRecyclerView() {
//        val drawerRecyclerView = binding.drawerRecyclerView// Ganti dengan ID RecyclerView Anda di dalam drawer
//        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Inisialisasi DrawerAdapter
//        val drawerAdapter = DrawerAdapter(this@ChatActivity)
//        drawerRecyclerView.adapter = drawerAdapter
//
//        // Mengamati LiveData dari ViewModel dan mengupdate adapter ketika data berubah
//        viewModel.listChatBySession.observe(this) { drawerItems ->
//            drawerAdapter.submitList(drawerItems)
//        }
//    }

    private fun getSessionsChat() {
        viewModel.getSessions().observe(this) { result ->
            if(result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val session = result.data
                        setupSession(session)
                    }
                    is Result.Error -> {
                        Log.e("getGita", result.error,)
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }

        }
    }

    private fun setupSession(sessionChat: List<SessionsItem>) {
        binding.drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = DrawerAdapter(this@ChatActivity)
        binding.drawerRecyclerView.adapter = adapter
        adapter.submitList(sessionChat)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupChatting() {
        val botMessageDefault = ChatModel(
            sender = "Ganesh Bot",
            message = "Selamat Datang Di Ganesh Bot, Silahkan Tanyakan Seputar Agama Hindu yang Belum Kamu Ketahui",
            date = getCurrentTime()
        )

        viewModel.addMessage(newMessage = botMessageDefault)
        binding.date.text = getCurrentDate()
    }

    private fun chatting() {
        val inputMessage = binding.inputEditText.text.toString()
        if (inputMessage.isNotEmpty()) {
            viewModel.sendMessage(inputMessage) // Perhatikan bahwa sendMessage sekarang tidak langsung mengembalikan LiveData
            binding.inputEditText.setText("")
        }
    }

//    private fun observeChatResult() {
//        viewModel.chatResult.observe(this) { result ->
//            when (result) {
//                is Result.Loading -> showLoading(true)
//                is Result.Success -> {
//                    val botMessage = ChatModel(
//                        sender = "Ganesh Bot",
//                        message = result.data.response,
//                        date = getCurrentTime()
//                    )
//                    viewModel.addMessage(newMessage = botMessage)
//                    showLoading(false)
//                }
//                is Result.Error -> {
//                    showLoading(false)
//                }
//            }
//        }
//    }

    private fun getCurrentTime(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    private fun getCurrentDate(): String = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val ID = "session_id"
    }
}