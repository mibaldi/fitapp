package com.mibaldi.fitapp.appData.servermock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat

class FitAppDbDataSourceMock(private val localDb:FileLocalDb): RemoteDataSource{
    override suspend fun getTrainings(): Either<FitAppError,List<Training>> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val loadJSONFromAsset = localDb.loadJSONFromAsset("trainings")?:""
        return Either.Right(gson.fromJson<List<Training>>(loadJSONFromAsset))
    }



    override suspend fun findById(id: Int): Either<FitAppError, Training> {

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val loadJSONFromAsset = localDb.loadJSONFromAsset("trainings")?:""
        val element = gson.fromJson<List<Training>>(loadJSONFromAsset).find { it.id == id }
        return if (element != null){
            Either.Right (element)
        } else {
            Either.Left(FitAppError(404,""))
        }
    }
}

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
