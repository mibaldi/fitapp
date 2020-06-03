package com.mibaldi.fitapp.appData.server

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mibaldi.data.source.AuthRemoteDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.*
import com.mibaldi.fitapp.appData.servermock.FileLocalDb
import com.mibaldi.fitapp.appData.toDomainTraining
import com.mibaldi.fitapp.appData.toDomainWeight
import com.mibaldi.fitapp.appData.toServerTraining
import com.mibaldi.fitapp.appData.server.ServerTraining as ServerTraining
import com.mibaldi.fitapp.appData.server.Tag as ServerTag
import com.mibaldi.fitapp.appData.server.Weight as ServerWeight

import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

class AuthDbDataSource: AuthRemoteDataSource {

    override suspend fun getUsers(): Either<FitAppError, List<String>> {
       /* val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        return suspendCancellableCoroutine {continuation ->
            val listTrainings = arrayListOf<Training>()
            val db = Firebase.
            db.collection("$uid-trainings")
                .get()
                .addOnSuccessListener { result ->
                    val listServerTraining = arrayListOf<ServerTraining>()
                    listServerTraining.addAll(result.toObjects(ServerTraining::class.java))
                    listTrainings.addAll(listServerTraining.map { it.toDomainTraining(emptyList()) })
                    continuation.resume(Either.Right(listTrainings))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Right(emptyList()))
                }
        }*/
        return Either.Right(arrayListOf())
    }

    override suspend fun registerUser(): Either<FitAppError, String> {
      /*  val uid = Firebase.auth
        val db = Firebase.firestore
        return suspendCancellableCoroutine { continuation ->
            db.collection("$uid").document()
                .set(weightModel)
                .addOnSuccessListener {
                    Log.d(TAG, "weight successfully written!")
                    continuation.resume(Either.Right(true))
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing weight", e)
                    continuation.resume(Either.Left(FitAppError(500,"")))
                }
        }*/
        return Either.Right("")
    }

}