package com.mibaldi.fitapp.appData.server

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.fitapp.appData.servermock.FileLocalDb
import com.mibaldi.fitapp.appData.servermock.fromJson
import com.mibaldi.fitapp.appData.toDomainTraining
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.server.Training as ServerTraining

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FitAppDbDataSource(private val localDb: FileLocalDb): RemoteDataSource {
    override suspend fun getTrainings(): Either<FitAppError,List<Training>> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        return suspendCancellableCoroutine {continuation ->
            val listTrainings = arrayListOf<Training>()
            val db = Firebase.firestore
            db.collection("$uid-trainings")
                .get()
                .addOnSuccessListener { result ->
                    val listServerTraining = arrayListOf<ServerTraining>()
                    listServerTraining.addAll(result.toObjects(ServerTraining::class.java))
                    listTrainings.addAll(listServerTraining.map { it.toDomainTraining() })
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
            val listTrainings = arrayListOf<ServerTraining>()
            val db = Firebase.firestore
            db.collection("$uid-trainings")
                .get()
                .addOnSuccessListener { result ->
                    listTrainings.addAll(result.toObjects(ServerTraining::class.java))

                    val training =listTrainings.find { it.id ==trainingID.toInt() }
                    if (training != null){
                        continuation.resume(Either.Right(training.toDomainTraining()))
                    } else {
                        continuation.resume(Either.Left(FitAppError(404,"")))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Left(FitAppError(404,"")))
                }
        }

    }

    override suspend fun uploadTraining(): Either<FitAppError, Boolean> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        val TAG = "uploadTraining"
        val gson = Gson()
        val email = Firebase.auth.currentUser?.email
        val filename = if (email != null && email == "mibaldi2@gmail.com"){
            "trainings"
        } else {
            "trainings-olga"
        }
        val trainings = localDb.loadJSONFromAsset(filename)?:""

        val list= gson.fromJson<List<ServerTraining>>(trainings)

        val db = Firebase.firestore
        for (training in list){
            db.collection("$uid-trainings").document(training.id.toString())
                .set(training)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
        return Either.Right(true)    }

}