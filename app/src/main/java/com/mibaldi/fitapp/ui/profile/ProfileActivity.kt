package com.mibaldi.fitapp.ui.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.domain.Workout
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.appData.servermock.fromJson
import com.mibaldi.fitapp.ui.auth.FirebaseUIActivity
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.getFile
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.common.toMilliseconds
import com.mibaldi.fitapp.ui.customUI.MyMarkerView
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_setweight.*
import kotlinx.android.synthetic.main.dialog_video.iconExit
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val LOG_TAG = "AudioRecordTest"
const val ACTIVITY_CHOOSE_FILE: Int = 203

class ProfileActivity : BaseActivity(),OnChartValueSelectedListener {
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

            selectCSVFile()
            //viewModel.exportTrainings()
        }
        /*btnLogout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    finish()
                    startActivity<FirebaseUIActivity>{}
                }
        }*/
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
        viewModel.weights.observe(this,Observer(::updateWeights))
    }

    private fun selectCSVFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), ACTIVITY_CHOOSE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getFile(this,requestCode,resultCode,data!!) {
            proImportCSV(File(it))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun proImportCSV(from: File){
        val reader = CSVReader(FileReader(from.absolutePath))
        val readAll = reader.readAll()

        generateTrainings2(readAll)
    }

    private fun generateTrainings2(readAll: List<Array<String>>) {
        Log.d(LOG_TAG,readAll.toString())
        val trainingMap = mutableMapOf<String,MutableList<Training>>()
        val withoutHeader = readAll.drop(1)
        var dayOfWeekString = ""
        var date = ""
        val regex = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)".toRegex()
        withoutHeader.forEach {fila ->
            val cols = fila[0].split(";")
            when (val first = cols[0]){
                "Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"->{
                    date = cols[1]
                    trainingMap[date] = mutableListOf()
                    dayOfWeekString = first
                }
                else -> {
                    val list = trainingMap[dayOfWeekString]?.toMutableList() ?: mutableListOf()
                    val nextDayOfWeek = getDate(date,dayOfWeekString)
                    val name = first
                    val circuit = cols[1]
                    val tiempoEntrenamientoList = cols[2].split(regex)
                    val relaxList = cols[3].split(regex)
                    val descansoList = cols[4].split(regex)
                    val repeticiones = cols[5].split("-")
                    val series = cols[6]

                    val tiempoEntrenamiento = toMilliseconds(tiempoEntrenamientoList)
                    val relax = toMilliseconds(relaxList)
                    val descanso = toMilliseconds(descansoList)


                    val workoutList = repeticiones.map {
                        Workout(name=first,entrenamiento = tiempoEntrenamiento,
                            relajamiento = relax,
                            descanso = descanso,
                            repeticiones = if (it.isNotEmpty()){ it.toInt()} else {0},
                            series = if (series.isNotEmpty()){ series.toInt()} else {0}
                        )
                    }
                    val tags = cols[7].split(",")
                    val tagList = tags.map { Tag(it, "", "") }.filter { it.tag.isNotEmpty() }
                    list.add( Training("",name,nextDayOfWeek,circuit, tagList, workoutList))
                    trainingMap[dayOfWeekString] = list
                }
            }
        }
        viewModel.exportTrainings(trainingMap.values.flatten())
    }


    private fun getDayOfWeek(dayOfWeek:String) :Int{
        return when(dayOfWeek){
            "Lunes"->2
            "Martes"->3
            "Miercoles"->4
            "Jueves"->5
            "Viernes"->6
            "Sabado"->7
            "Domingo"-> 2
            else -> 0
        }
    }

    private fun getDate(dateString: String,dayOfWeek: String): Date{
        return if (dateString.isNotEmpty()){
            SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(dateString)
                ?: nextDayOfWeek(getDayOfWeek(dayOfWeek))
        } else {
            nextDayOfWeek(getDayOfWeek(dayOfWeek))
        }
    }

    private fun nextDayOfWeek(dayOfWeek: Int):Date{
        val date1 = Calendar.getInstance()
        while (date1[Calendar.DAY_OF_WEEK] != dayOfWeek) {
            date1.add(Calendar.DATE, 1)
        }
        return date1.time
    }


    private fun updateWeights(list: List<Weight>?) {
        if (list != null){
            val map = list.sortedBy { it.date }.map {
                val milliseconds = TimeUnit.MILLISECONDS.toHours(it.date.time)
                val entry = Entry(milliseconds.toFloat(), it.weight.toFloat())
                entry
            }
            setChart()


            val set1 = LineDataSet(map, "DataSet 1")
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = ColorTemplate.getHoloBlue()
            set1.valueTextColor = ColorTemplate.getHoloBlue()
            set1.lineWidth = 1.5f
            set1.setDrawCircles(false)
            set1.setDrawValues(false)
            set1.fillAlpha = 65
            set1.fillColor = ColorTemplate.getHoloBlue()
            set1.setCircleColor(Color.BLACK)

            set1.highLightColor = Color.rgb(244, 117, 117)
            set1.setDrawCircleHole(false)
            // draw selection line as dashed

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area

            // set the filled area
            set1.setDrawFilled(true)
            set1.setFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area

            // set color of filled area
            // drawables only supported on api level 18 and above
            val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.fade_red)
            set1.fillDrawable = drawable
            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTextColor(Color.WHITE)
            data.setValueTextSize(9f)

            // set data
            chart.data = data

        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun setChart() {

        // no description text

        // no description text
        chart.description.isEnabled = false


        // set listeners
        chart.setOnChartValueSelectedListener(this)
        chart.setDrawGridBackground(false)
        val mv = MyMarkerView(this, R.layout.custom_marker_view)

        // Set the marker to the chart
        mv.chartView = chart
        chart.marker = mv
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

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = chart.legend
        l.isEnabled = true

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
        leftAxis.axisMinimum = 20f
        leftAxis.axisMaximum = 130f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.rgb(255, 192, 56)

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    override fun onNothingSelected() {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Toast.makeText(this,e?.y.toString(),Toast.LENGTH_SHORT).show()
    }

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