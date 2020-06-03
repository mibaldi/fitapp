package com.mibaldi.fitapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.Workout
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.getFile
import com.mibaldi.fitapp.ui.common.toMilliseconds
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_admin_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*


const val ACTIVITY_CHOOSE_FILE: Int = 203

class AdminActivity : BaseActivity() {
    private val viewModel: AdminViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)
        setSupportActionBar(adminProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.title = Firebase.auth.currentUser?.displayName



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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}