package com.mibaldi.fitapp.ui.workoutTimer

import android.content.Context
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.mibaldi.domain.Workout
import com.mibaldi.domain.WorkoutStatus
import com.mibaldi.domain.createWorkoutStatus
import com.mibaldi.domain.toWorkoutString
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_workouttimer.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.util.*


class WorkoutTimerActivity : BaseActivity() {

    private val viewModel: WorkoutTimerViewModel by lifecycleScope.viewModel(this)
    var progressCountdown = 0
    var countDownTimer: CountDownTimer? = null
    var workout = Workout()
    var firstTime = true
    var descanso = false
    var repDone : Int = 0
    var setDone : Int = 0
    lateinit var ejerciciosRestantes: ArrayList<WorkoutStatus>
    var tiempoEjercicioActual = 0L
    lateinit var  workoutList : List<Workout>
    private  var t1: TextToSpeech? = null
    private lateinit var vibrator: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouttimer)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        intent.extras?.let { bundle ->
            workoutList = (bundle.getSerializable("workoutList")as List<Workout>)
            if (workoutList.isNotEmpty()){
                adapter.addAll(workoutList.map { it.name })
                spinner.adapter = adapter
                workout = workoutList[0]
                viewModel.init(workout)
                if (workoutList.isEmpty()){
                    finish()
                }
            } else {
                finish()
            }
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = adapter.getItem(position)
                val find = workoutList.find { it.name == item }
                viewModel.init(find!!)
            }

        }

        viewModel.workout.observe(this,Observer(::showWorkout))
        generateTextToSpeech()

        btnStart.setOnClickListener {
            restart()
            startCountDown(workout.generateList())
        }
        btnStop.setOnClickListener {
            stopCountDown()
        }
        btnPause.setOnClickListener {
            pauseCountDown()
        }
    }


    private fun showWorkout(workout: Workout?) {

        workout?.apply {
            tvRep.text = "$currentRep/$repeticiones"
            tvSet.text = "$currentSet/$series"

            countDownView.visibility = View.VISIBLE // show progress view
            countDownView.setName(name)
            countDownView.showTraining(total)
            tvTotal.text = totalToString()
        }

    }

    private fun generateTextToSpeech() {
        t1 = TextToSpeech(applicationContext,
            OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    t1?.language = Locale.getDefault()
                }
            })
    }

    private fun speak(message: String) {
        t1?.speak(message,TextToSpeech.QUEUE_FLUSH,null,null)
    }

    override fun onStop() {
        t1?.let {
            it.stop()
            t1 = null
        }
        super.onStop()
    }

    private fun restart() {

        workout.tiempoRestante = workout.total
        workout.currentSet = 1
        workout.currentRep = 1
        countDownView.setName(workout.name)
    }

    private fun pauseCountDown() {
        if (countDownTimer != null){
            btnPause.text = "RESTART"
            countDownTimer!!.cancel()
            countDownTimer = null
            val primerEjercicio = ejerciciosRestantes[0]
            ejerciciosRestantes[0]= createWorkoutStatus(primerEjercicio.name,tiempoEjercicioActual)
        } else {
            btnPause.text = "PAUSE"
            startCountDown(ejerciciosRestantes)
        }

    }

    fun vibrate(time : Long){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(time);
        }
    }

    private fun startCountDown(list:ArrayList<WorkoutStatus>) {
        ejerciciosRestantes = list
        val endTimeObject = list.removeAt(0)
        val endTime = endTimeObject.time
        countDownView.setName(endTimeObject.name)
        tvRep.text = "${workout.currentRep}/${workout.repeticiones}"
        tvSet.text = "${workout.currentSet}/${workout.series}"
        startCountDownButtons()
        progressCountdown = 0
        val second: Int = 1000
        when (endTimeObject){
            is WorkoutStatus.Calentamiento -> {
                speak("Calentamiento")
            }
            is WorkoutStatus.Descanso -> {
                speak("Descanso")
            }
            is WorkoutStatus.Entrenamiento -> {
                speak("Entrenamiento ${workout.currentRep} de ${workout.repeticiones} ")
            }
            is WorkoutStatus.Relajamiento -> {
                speak("Relax")
            }
        }
        countDownTimer =
            object :
                CountDownTimer(endTime /*finishTime**/, second.toLong() /*interval**/) {
                override fun onTick(millisUntilFinished: Long) {
                    countDownView.setProgress(progressCountdown, endTime.toInt())
                    workout.tiempoRestante-= second
                    tvTotal.text = workout.tiempoRestante.toWorkoutString(true)
                    tiempoEjercicioActual = endTime - progressCountdown
                    progressCountdown += second
                    if (tiempoEjercicioActual < 4000){
                        val seconds = (tiempoEjercicioActual / 1000).toInt()
                        vibrate(500)
                        if (seconds > 0) {
                            speak("$seconds")
                        }
                    }
                }

                override fun onFinish() {
                    countDownView.setProgress(progressCountdown, endTime.toInt())
                    if (list.isNotEmpty()){
                       when (endTimeObject){
                           is WorkoutStatus.Calentamiento -> { }
                           is WorkoutStatus.Descanso -> {
                               workout.currentSet++
                               workout.currentRep = 1
                           }
                           is WorkoutStatus.Entrenamiento -> {

                           }
                           is WorkoutStatus.Relajamiento -> {
                               if (workout.currentRep < workout.repeticiones) {
                                   workout.currentRep++
                               }
                           }
                       }

                        startCountDown(list)
                    } else {
                        speak("Entrenamiento Finalizado")
                        countDownView.showTraining(workout.total)
                        finishCountDown()
                    }


                }
            }
        countDownTimer?.start() // start timer
    }




    private fun stopCountDown() {
        countDownView.setProgress(0, workout.entrenamiento.toInt())
        countDownView.showTraining(workout.total)
        countDownTimer!!.cancel()
        countDownView.setName(workout.name)
        finishCountDown()
    }

    private fun startCountDownButtons() {
        btnStart.visibility = View.GONE // hide button
        btnStop.visibility = View.VISIBLE // show cancel button
        btnPause.visibility = View.VISIBLE // show pause button
    }

    private fun finishCountDown() {
        btnStop.visibility = View.GONE
        btnPause.visibility = View.GONE
        btnStart.visibility = View.VISIBLE
    }
}