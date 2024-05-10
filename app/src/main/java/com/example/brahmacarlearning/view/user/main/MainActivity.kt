package com.example.brahmacarlearning.view.user.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brahmacarlearning.R
import com.example.brahmacarlearning.ViewModelFactory
import com.example.brahmacarlearning.databinding.ActivityMainUserBinding
import com.example.brahmacarlearning.view.user.adapter.BabGitaAdapter
import com.example.brahmacarlearning.view.user.adapter.LoadingStateAdapter
import com.example.brahmacarlearning.view.user.chat.ChatActivity
import com.example.brahmacarlearning.view.user.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainUserBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                getContent()
                val greeting = resources.getString(R.string.greeting, user.email)
                binding.tvEmailUser.text = greeting
            }
        }

        binding.fab.setOnClickListener {
            goToChatBot()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_map -> {
                    logout()
                    true
                }
                else -> {false}
            }
        }

        setupView()

    }

//    private fun getGita() {
//        viewModel.getGita().observe(this) { result ->
//            if(result != null) {
//                when(result) {
//                    is Result.Loading -> {
//                        showLoading(true)
//                    }
//                    is Result.Success -> {
//                        showLoading(false)
//                        val gita = result.data
//                        setupGita(gita)
//                    }
//                    is Result.Error -> {
//                        Log.e("getGita", result.error,)
//                        showToast(result.error)
//                        showLoading(false)
//                    }
//                }
//            }
//
//        }
//    }

//    private fun setupGita(gita: List<BabsItem>) {
//        binding.rvStory.layoutManager = LinearLayoutManager(this)
//        val adapter = BabGitaAdapter(this@MainActivity)
//        binding.rvStory.adapter = adapter
//        adapter.submitList(gita)
//
//    }

    private fun getContent() {

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val adapter = BabGitaAdapter(this@MainActivity)
        binding.rvStory.adapter = adapter.withLoadStateFooter( //adapter taruh diluar observe (kalo taruh diobserve adapter dibuat terus)
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getBabPaging.observe(this) {data ->
            if (data != null) {
                adapter.submitData(lifecycle, data)
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun goToChatBot() {
        startActivity((Intent(this, ChatActivity::class.java)))
        finish()
    }

    private fun logout() {
        viewModel.logout()
    }
}