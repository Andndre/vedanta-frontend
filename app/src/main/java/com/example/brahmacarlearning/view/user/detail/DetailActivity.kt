package com.example.brahmacarlearning.view.user.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.brahmacarlearning.R
import com.example.brahmacarlearning.ViewModelFactory
import com.example.brahmacarlearning.data.remote.response.DetailGitaResponse
import com.example.brahmacarlearning.data.result.Result
import com.example.brahmacarlearning.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var bab: String? = null
    private  lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bab = intent.getStringExtra(BAB)

        viewModel.getDetailGita(bab.toString()).observe(this) { result ->
            if(result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val detailResponse = result.data
                        setDetailGita(detailResponse)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun setDetailGita(gita: DetailGitaResponse){

        binding.detailPhoto.visibility = View.VISIBLE
        binding.detailEllipse.visibility = View.VISIBLE

        binding.detailName.visibility = View.VISIBLE
        binding.detailName.text = gita.title

        binding.detailDescription.visibility = View.VISIBLE
        binding.detailDescription.text = gita.summary

        binding.tvDetailDate.visibility = View.VISIBLE
        binding.tvDetailDate.text = gita.translationIndo
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val BAB = "bab"
    }
}