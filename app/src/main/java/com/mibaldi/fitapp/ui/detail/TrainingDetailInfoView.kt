package com.mibaldi.fitapp.ui.detail

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.mibaldi.domain.Training
import com.mibaldi.domain.generateStringDate

class TrainingDetailInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setTraining(training: Training) = with(training) {
        text = buildSpannedString {

            bold { append("Date: ") }
            appendln(generateStringDate(date))

            bold { append("Name: ") }
            appendln(name)

            bold { append("Circuit: ") }
            appendln(circuit)
/*
            bold { append("Original title: ") }
            appendln(originalTitle)

            bold { append("Release date: ") }
            appendln(releaseDate)

            bold { append("Popularity: ") }
            appendln(popularity.toString())

            bold { append("Vote Average: ") }
            append(voteAverage.toString())*/
        }
    }
}