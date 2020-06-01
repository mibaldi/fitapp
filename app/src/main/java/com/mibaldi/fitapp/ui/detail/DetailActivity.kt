package com.mibaldi.fitapp.ui.detail

import android.R.attr
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mibaldi.domain.Tag
import com.mibaldi.domain.generateStringDate
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.TagsAdapter
import com.mibaldi.fitapp.ui.common.GlideApp
import com.mibaldi.fitapp.ui.common.loadRandomImage
import kotlinx.android.synthetic.main.activity_detail.*
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
        // WORKING CODE!
        val storage = Firebase.storage
        // Create a reference to a file from a Google Cloud Storage URI
        val gsReference = storage.getReferenceFromUrl(tag.url)
        GlideApp.with(this@DetailActivity)
            .asGif()
            .load(gsReference)
            .listener(object: RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.setLoopCount(2)
                    return false
                }
            })
            .into(ivGif)
       /* Dialog(this).apply {
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
        }*/
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
