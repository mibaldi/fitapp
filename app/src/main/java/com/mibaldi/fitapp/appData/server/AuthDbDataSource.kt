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
    val TAG ="AUTH"
    override suspend fun getUsers(): Either<FitAppError, List<User>> {
       val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        return suspendCancellableCoroutine {continuation ->
            val db = Firebase.firestore
            db.collection("admin").document("users").collection("info")
                .get()
                .addOnSuccessListener { result ->
                    val userList = result.toObjects(ServerUser::class.java).map { User(it.id,it.email) }
                    continuation.resume(Either.Right(userList))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Right(emptyList()))
                }
        }
    }

    override suspend fun registerUser(): Either<FitAppError, String> {
        val firebaseUser = Firebase.auth.currentUser

        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))

        val user = hashMapOf(
            "id" to uid,
            "email" to firebaseUser?.email
        )
        val db = Firebase.firestore
        return suspendCancellableCoroutine { continuation ->
            db.collection("admin").document("users").collection("info").document(uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "user successfully written!")
                    continuation.resume(Either.Right("${firebaseUser?.uid}"))
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing user", e)
                    continuation.resume(Either.Left(FitAppError(500,"")))
                }
        }
    }

}