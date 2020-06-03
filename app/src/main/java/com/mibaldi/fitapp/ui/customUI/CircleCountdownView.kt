package com.mibaldi.fitapp.ui.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.mibaldi.domain.toWorkoutString
import com.mibaldi.fitapp.R
import kotlinx.android.synthetic.main.layout_count_down_view.view.*


class CircleCountDownView : FrameLayout {
    private var progressBarView: ProgressBar? = null
    private var progressTextView: TextView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(ctx: Context) {
        val rootView = View.inflate(ctx, R.layout.layout_count_down_view, this)
        progressBarView = rootView.findViewById(R.id.view_progress_bar) as ProgressBar
        progressTextView = rootView.findViewById(R.id.view_progress_text) as TextView
        val makeVertical = RotateAnimation(
            0F,
            -90F,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_SELF,
            0.5f
        )
        makeVertical.fillAfter = true
        progressBarView!!.startAnimation(makeVertical)
    }

    fun setName(name: String){
        tvName.text = name
    }

    fun setProgress(startTime: Int, endTime: Int) {
        progressBarView?.max = endTime
        progressBarView?.secondaryProgress = endTime
        progressBarView?.progress = startTime
        val elapsedTime = endTime - startTime
        progressTextView?.text = elapsedTime.toLong().toWorkoutString(false)
    }
    fun showTraining(training: Long){
        progressTextView?.text = training.toWorkoutString(false)
    }
}