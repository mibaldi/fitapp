package com.mibaldi.fitapp.appData.servermock

import android.content.Context
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.coroutines.resume

class FileLocalDb(private val context: Context) {

    fun loadJSONFromAsset(name: String): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = context.assets.open("$name.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)
