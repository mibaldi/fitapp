package com.mibaldi.fitapp.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mibaldi.fitapp.R
import java.lang.Exception
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


inline fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialValue: List<T>,
    crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
) =
    Delegates.observable(initialValue) { _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areContentsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size
        }).dispatchUpdatesTo(this@basicDiffUtil)
    }

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

inline fun <reified T : Activity> Context.intentFor(body: Intent.() -> Unit): Intent =
    Intent(this, T::class.java).apply(body)

inline fun <reified T : Activity> Context.startActivity(body: Intent.() -> Unit) {
    startActivity(intentFor<T>(body))
}
fun ImageView.loadUrl(url: String) {
    Glide.with(context).load(url).into(this)
}

fun ImageView.loadRandomImage(){
    val res = intArrayOf(R.drawable.pic1, R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5,R.drawable.pic6, R.drawable.pic7,R.drawable.pic8,R.drawable.pic9,R.drawable.pic10)
    val rand = SecureRandom()
    val rndInt: Int = rand.nextInt(res.size)
    val bitmap = BitmapFactory.decodeResource(resources, res[rndInt])
    Glide.with(context).load(bitmap).into(this)
}

fun getRandom(range: Float, start: Float): Float {
    return (Math.random() * range).toFloat() + start
}

fun toMilliseconds(list:List<String>):Long{
    when {
        list.size > 1 -> {
            val measure = list[1]
            val time = list[0]
           return  when (measure){
                "m" -> time.toLong() * 60000
                "h" -> time.toLong() * 3600000
                else -> time.toLong() * 1000
            }
        }
        list.isNotEmpty() -> {
            val time = list[0]
            return try {
                time.toLong() * 1000
            } catch (e: Exception){
                0
            }
        }
        else ->return 0
    }
}