package com.example.brahmacarlearning.view.user.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.brahmacarlearning.R
import com.example.brahmacarlearning.ViewModelFactory
import com.example.brahmacarlearning.data.local.pref.UserModel
import com.example.brahmacarlearning.databinding.ActivityLoginUserBinding
import com.example.brahmacarlearning.view.custom.CustomEmailEditText
import com.example.brahmacarlearning.view.custom.CustomPasswordEditText
import com.example.brahmacarlearning.data.result.Result
import com.example.brahmacarlearning.view.user.main.MainActivity
import com.example.brahmacarlearning.view.user.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginUserBinding

    private lateinit var passwordEditText: CustomPasswordEditText
    private lateinit var loginButton: Button
    private lateinit var emailEditText: CustomEmailEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEditText = binding.edLoginEmail
        loginButton = binding.loginButton
        passwordEditText = binding.edLoginPassword


        //validasi email
        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

        })

        //validasi password
        setMyButtonEnable()

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

        })

        binding.textViewClickable.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        setupView()
        setupAction()
    }

    private fun setMyButtonEnable() {
        val password = passwordEditText.text
        val email = emailEditText.text
        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()

        loginButton.isEnabled = password != null && password.toString().length >= 8 && email != null && validEmail
        if (!loginButton.isEnabled){
            binding.loginButton.alpha = 0.3f
        } else {
            binding.loginButton.alpha = 1f
        }
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val user = UserModel(
                                token = result.data.token,
                                email = email,
                                isLogin = true
                            )
                            viewModel.saveSession(user)
                            alertDialog()
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog() {
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.congrats)
            val loginSucceed = getString(R.string.login_succeed)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(loginSucceed)
            setPositiveButton(next) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}