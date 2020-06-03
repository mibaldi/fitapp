package com.mibaldi.fitapp.ui.workoutTimer

import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
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
import kotlin.collections.ArrayList


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
    private lateinit var t1: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouttimer)
        intent.extras?.let {
           val workoutList = (it.getSerializable("workoutList")as List<Workout>)
            if (workoutList.isNotEmpty()){
                workout = workoutList[0]
                if (workout.name.isEmpty()){
                    finish()
                }
            } else {
                finish()
            }
        }
        btnStart.setOnClickListener {
            restart()
            generateTextToSpeech()
            startCountDown(workout.generateList())
        }
        btnStop.setOnClickListener {
            stopCountDown()
        }
        btnPause.setOnClickListener {
            pauseCountDown()
        }
        tvRep.text = "${workout.currentRep}/${workout.repeticiones}"
        tvSet.text = "${workout.currentSet}/${workout.series}"

        countDownView.visibility = View.VISIBLE // show progress view
        countDownView.setName(workout.name)
        countDownView.showTraining(workout.total)
        tvTotal.text = workout.totalToString()
    }

    private fun generateTextToSpeech() {
        t1 = TextToSpeech(applicationContext,
            OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    t1.language = Locale.getDefault()
                }
            })
        speak("Empezando ejercicio")

    }

    private fun speak(message: String) {
        t1.speak(message,TextToSpeech.QUEUE_FLUSH,null,null)
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
        countDownTimer =
            object :
                CountDownTimer(endTime /*finishTime**/, second.toLong() /*interval**/) {
                override fun onTick(millisUntilFinished: Long) {
                    countDownView.setProgress(progressCountdown, endTime.toInt())
                    workout.tiempoRestante-= second
                    tvTotal.text = workout.tiempoRestante.toWorkoutString(true)
                    progressCountdown += second
                    tiempoEjercicioActual = endTime - progressCountdown
                    if (tiempoEjercicioActual < 4000){
                        val seconds = (tiempoEjercicioActual / 1000).toInt()
                        if (seconds > 0) {
                            speak("$seconds")
                        }
                    }
                    if (endTime - tiempoEjercicioActual == 1000L){
                        when (endTimeObject){
                            is WorkoutStatus.Calentamiento -> {
                                speak("Empezando Calentamiento")
                            }
                            is WorkoutStatus.Descanso -> {
                                speak("Empezando Descanso")

                            }
                            is WorkoutStatus.Entrenamiento -> {
                                speak("Empezando Entrenamiento")

                            }
                            is WorkoutStatus.Relajamiento -> {

                            }
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