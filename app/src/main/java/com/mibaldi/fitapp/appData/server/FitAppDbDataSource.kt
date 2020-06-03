package com.mibaldi.fitapp.appData.server

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
                    listTrainings.addAll(listServerTraining.map { it.toDomainTraining(emptyList()) })
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
                    val training = listTrainings.find { it.id ==trainingID}
                    if (training != null){
                        db.collection("videos").get().addOnSuccessListener {
                            val listServerTags = it.toObjects(ServerTag::class.java)
                            val tags = tagList(training.tags, listServerTags)
                            continuation.resume(Either.Right(training.toDomainTraining(tags)))
                        }.addOnFailureListener {
                            continuation.resume(Either.Right(training.toDomainTraining(emptyList())))
                        }
                    } else {
                        continuation.resume(Either.Left(FitAppError(404,"")))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Left(FitAppError(404,"")))
                }
        }

    }

    override suspend fun sendWeight(weight: Double) :Either<FitAppError,Boolean>{
        val TAG = "sendWeight"
        val instance = Calendar.getInstance()
        val weightModel = hashMapOf(
            "weight" to weight,
            "date" to instance.time
        )

        val uid = Firebase.auth.uid
        val db = Firebase.firestore
        return suspendCancellableCoroutine { continuation ->
            db.collection("$uid-weights").document()
                .set(weightModel)
                .addOnSuccessListener {
                    Log.d(TAG, "weight successfully written!")
                    continuation.resume(Either.Right(true))
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing weight", e)
                    continuation.resume(Either.Left(FitAppError(500,"")))
                }
        }
    }

    override suspend fun getWeights(): Either<FitAppError, List<Weight>> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        return suspendCancellableCoroutine {continuation ->
            val listWeight = arrayListOf<Weight>()
            val db = Firebase.firestore
            db.collection("$uid-weights")
                .get()
                .addOnSuccessListener { result ->
                    val listServerWeight = arrayListOf<ServerWeight>()
                    listServerWeight.addAll(result.toObjects(ServerWeight::class.java))
                    listWeight.addAll(listServerWeight.map { it.toDomainWeight() })
                    continuation.resume(Either.Right(listWeight))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Either.Right(emptyList()))
                }
        }
    }

    private fun tagList(
        listTags: List<String>,
        listServerTags: List<com.mibaldi.fitapp.appData.server.Tag>
    ): List<Tag> {
        val map = listTags.map { tag ->
            val find = listServerTags.find { it.tag == tag }
            if (find != null) {
                Tag(tag, find.name, find.url)
            } else {
                Tag(tag, "", "")
            }
        }.filter { it.name.isNotEmpty() }
        return map
    }

    override suspend fun getVideo(tag: String): String? {
        return suspendCancellableCoroutine { continuation ->
            val db = Firebase.firestore
            db.collection("videos")
                .whereEqualTo("name",tag)
                .get()
                .addOnSuccessListener {
                    continuation.resume(it.first().data["url"] as String)
                }.addOnFailureListener{
                    continuation.resume(null)
                }
        }
    }

    override suspend fun uploadTraining(list: List<Training>): Either<FitAppError, Boolean> {
        val uid = Firebase.auth.uid ?: return Either.Left(FitAppError(401,"Unauthorized"))
        val TAG = "uploadTraining"
       /* val gson = Gson()
        val email = Firebase.auth.currentUser?.email
        val filename = if (email != null && email == "mibaldi2@gmail.com"){
            "trainings"
        } else {
            "trainings-olga"
        }
        val trainings = localDb.loadJSONFromAsset(filename)?:""

        val list= gson.fromJson<List<ServerTraining>>(trainings)*/

        val listServer = list.map { it.toServerTraining()}
        val db = Firebase.firestore
        for (training in listServer){
            val document = db.collection("$uid-trainings").document()
            training.id = document.id
            document
                .set(training)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
        return Either.Right(true)    }

}