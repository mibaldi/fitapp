package com.mibaldi.fitapp.ui.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibaldi.domain.Tag
import com.mibaldi.domain.generateStringDate
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.TagsAdapter
import com.mibaldi.fitapp.ui.common.loadRandomImage
import com.mibaldi.fitapp.ui.common.loadUrl
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialog_video.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import org.koin.core.parameter.parametersOf


class DetailActivity : AppCompatActivity() {
    companion object {
        const val TRAINING = "DetailActivity:training"
    }
    private val viewModel: DetailViewModel by lifecycleScope.viewModel(this) {
        parametersOf(intent.getIntExtra(TRAINING,-1))
    }
    private lateinit var tagsAdapter: TagsAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(trainingDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        viewModel.model.observe(this, Observer(::updateUi))
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tagsAdapter = TagsAdapter(::onTagClicked)
        rvTags.layoutManager = layoutManager
        rvTags.adapter = tagsAdapter
    }

    private fun onTagClicked(tag: Tag) {
        Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setContentView(R.layout.dialog_video)
            val youTubePlayerView =
                findViewById<YouTubePlayerView>(R.id.youtube_player_view)
            lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val videoId = Uri.parse(tag.url).getQueryParameter("v") ?: ""
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
            iconExit.setOnClickListener {
                cancel()
            }
            show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun updateUi(model: DetailViewModel.UiModel) {
        when (model){
            is DetailViewModel.UiModel.Content -> {
                with(model.training){
                    supportActionBar?.title = name
                    trainingDetailImage.loadRandomImage()
                    etName.setText(name)
                    etDate.setText(generateStringDate(date))
                    etCircuit.setText(circuit)
                    tagsAdapter.tags = tags

                    val icon = if (name.contains("0",true))
                        R.drawable.ic_favorite_on else R.drawable.ic_favorite_off
                    trainingDetailFavorite.setImageDrawable(getDrawable(icon))
                }
            }
            is DetailViewModel.UiModel.Error -> {
                makeToast(model.error)
                finish()
            }
        }
    }

    private fun makeToast(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
    }
}
