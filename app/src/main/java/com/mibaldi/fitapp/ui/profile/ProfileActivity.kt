package com.mibaldi.fitapp.ui.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.base.BaseActivity
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.io.IOException


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val LOG_TAG = "AudioRecordTest"

class ProfileActivity : BaseActivity() {
    private lateinit var storageRef: StorageReference
    private var saved: Boolean = false
    private val viewModel: ProfileViewModel by lifecycleScope.viewModel(this)

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var fileName: String = ""

    private var recordButton: RecordButton? = null

    private var recorder: MediaRecorder? = null
    private var playButton: PlayButton? = null
    private var uploadButton: UploadButton? = null
    private var playFirebaseButton: PlayButtonFirebase? = null
    private var player: MediaPlayer? = null
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
    private fun uploadAudio(){
        continueAcrossRestarts()
    }


    // [START storage_upload_lifecycle]
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // If there's an upload in progress, save the reference so you can query it later
        storageRef?.let {
            outState.putString("reference", it.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // If there was an upload in progress, get its reference and create a new StorageReference
        val stringRef = savedInstanceState.getString("reference") ?: return

        storageRef = Firebase.storage.reference.child(Firebase.auth.uid ?: "admin")


        // Find all UploadTasks under this StorageReference (in this example, there should be one)

        val tasks = storageRef.activeUploadTasks

        if (tasks.size > 0) {
            // Get the task monitoring the upload
            val task = tasks[0]

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this) {
                // Success!
                // ...
            }
        }
    }
    // [END storage_upload_lifecycle]

    fun continueAcrossRestarts() {
        val localFile: Uri = Uri.parse("file://$fileName")
        var sessionUri: Uri? = null
        var uploadTask: UploadTask

        // [START save_before_restart]
        uploadTask = storageRef.putFile(localFile)
        uploadTask.addOnFailureListener {
            Log.d("Failure",it.toString())
        }.addOnSuccessListener {
            Log.d("Success",it.uploadSessionUri.toString())
        }
    }



    internal inner class RecordButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {

        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "Stop recording"
                false -> "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {
        var mStartPlaying = true
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when (mStartPlaying) {
                true -> "Stop playing"
                false -> "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }
    internal inner class UploadButton(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {
        var clicker: OnClickListener = OnClickListener {
            uploadAudio()
        }

        init {
            text = "Upload"
            setOnClickListener(clicker)
        }
    }
    internal inner class PlayButtonFirebase(ctx: Context) : androidx.appcompat.widget.AppCompatButton(ctx) {
        var clicker: OnClickListener = OnClickListener {
            storageRef.downloadUrl.addOnCompleteListener {
                try {
                    val player = MediaPlayer()
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    player.setDataSource(it.result.toString())
                    player.prepare()
                    player.start()
                } catch (e: Exception) {
                    // TODO: handle exception
                }
            }

        }

        init {
            text = "Play Firebase Audio"
            setOnClickListener(clicker)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        storageRef = Firebase.storage.reference.child(Firebase.auth.uid ?: "admin").child("audiorecordtest.3gp")

        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        recordButton = RecordButton(this)
        playButton = PlayButton(this)
        playFirebaseButton = PlayButtonFirebase(this)
        uploadButton = UploadButton(this)
        val ll = LinearLayout(this).apply {
            addView(recordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(playButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(uploadButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(playFirebaseButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
        }
        setContentView(ll)
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}