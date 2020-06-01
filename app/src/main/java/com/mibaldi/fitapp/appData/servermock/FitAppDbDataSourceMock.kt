package com.mibaldi.fitapp.appData.servermock

import android.content.Context
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

import java.text.SimpleDateFormat

class FitAppDbDataSourceMock(private val localDb:FileLocalDb): RemoteDataSource{

    override suspend fun getTrainings(): Either<FitAppError,List<Training>> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        return suspendCancellableCoroutine {continuation ->
            val listTrainings = arrayListOf<Training>()
            val db = Firebase.firestore
            db.collection("$uid-trainings")
                .get()
                .addOnSuccessListener { result ->
                    listTrainings.addAll(result.toObjects(Training::class.java))
                    continuation.resume(Either.Right(listTrainings))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Right(emptyList()))
                }
        }
    }

    override suspend fun findById(trainingID: String): Either<FitAppError, Training> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))

        return suspendCancellableCoroutine {continuation ->
            val listTrainings = arrayListOf<Training>()
            val db = Firebase.firestore
            db.collection("$uid-trainings")
                .get()
                .addOnSuccessListener { result ->
                    val training = result.find { it.data["id"] == trainingID  }?.toObject(Training::class.java)
                    if (training != null){
                        continuation.resume(Either.Right(training))
                    } else {
                        continuation.resume(Either.Left(FitAppError(404,"")))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Left(FitAppError(404,"")))
                }
        }

    }
    override suspend fun uploadTraining(): Either<FitAppError,Boolean>{
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        val TAG = "uploadTraining"
        val gson = Gson()
        val trainings = localDb.loadJSONFromAsset("trainings")?:""

        val list= gson.fromJson<List<Training>>(trainings)

        val db = Firebase.firestore
        for (training in list){
            db.collection("$uid-trainings").document(training.id.toString())
                .set(training)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
        return Either.Right(true)
    }

    override suspend fun getVideo(tag: String): String? {
        return null
    }

    override suspend fun sendWeight(weight: Double): Either<FitAppError, Boolean> {
        return Either.Right(true)
    }

    override suspend fun getWeights(): Either<FitAppError, List<Weight>> {
        return Either.Right(emptyList())
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
