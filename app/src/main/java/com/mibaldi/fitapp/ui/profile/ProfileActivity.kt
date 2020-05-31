package com.mibaldi.fitapp.ui.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.SeekBar
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.auth.FirebaseUIActivity
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.getRandom
import com.mibaldi.fitapp.ui.common.startActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_setweight.*
import kotlinx.android.synthetic.main.dialog_video.iconExit
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val LOG_TAG = "AudioRecordTest"

class ProfileActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {

    private val viewModel: ProfileViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(profileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.title = Firebase.auth.currentUser?.displayName
        btnExport.setOnClickListener {
            viewModel.exportTrainings()
        }
        btnLogout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    finish()
                    startActivity<FirebaseUIActivity>{}
                }
        }
        btnSetWeight.setOnClickListener {
            Dialog(this).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(false)
                setContentView(R.layout.dialog_setweight)
                var fNumber = 0
                var sNumber = 0
                first_number.value = fNumber
                second_number.value = sNumber
                first_number.minValue = 30
                first_number.maxValue = 120

                first_number.setOnValueChangedListener { picker, oldVal, newVal ->
                    fNumber = newVal
                }

                second_number.minValue = 0
                second_number.maxValue = 9

                second_number.setOnValueChangedListener { picker, oldVal, newVal ->
                    sNumber = newVal
                }

                btnSendWeight.setOnClickListener {
                    viewModel.sendWeight(fNumber,sNumber)
                    cancel()
                }
                iconExit.setOnClickListener {
                    cancel()
                }
                show()
            }
        }
        /*storageRef = Firebase.storage.reference.child(Firebase.auth.uid ?: "admin").child("audiorecordtest.3gp")

        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)*/

        setChart()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun setChart() {
        seekBar.setOnSeekBarChangeListener(this)

        // no description text

        // no description text
        chart.description.isEnabled = false

        // enable touch gestures

        // enable touch gestures
        chart.setTouchEnabled(true)

        chart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)
        chart.isHighlightPerDragEnabled = true

        // set an alternative background color

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE)
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)

        // add data

        // add data
        seekBar.progress = 100

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = chart.legend
        l.isEnabled = false

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(255, 192, 56)
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // one hour

        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                val millis =
                    TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(millis))
            }
        }

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.rgb(255, 192, 56)

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
    }


    private fun setData(count: Int, range: Float) {

        // now in hours
        val now: Long = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
        val values: ArrayList<Entry> = ArrayList()

        // count = hours
        val to = now + count.toFloat()

        // increment by 1 hour
        var x = now.toFloat()
        while (x < to) {
            val y: Float = getRandom(range, 50F)
            values.add(Entry(x, y)) // add one entry per hour
            x++
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.setDrawCircleHole(false)

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        // set data
        chart.data = data
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        tvX.text = seekBar?.progress.toString()

        setData(seekBar?.progress ?: 0, 50F)

        // redraw
        chart.invalidate()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
/*
private lateinit var storageRef: StorageReference
    private var saved: Boolean = false
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
    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }*/
}