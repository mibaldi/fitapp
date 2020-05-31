package com.mibaldi.fitapp.ui.training

import android.os.Bundle
import androidx.lifecycle.Observer
import com.mibaldi.domain.Training
import com.mibaldi.domain.generateStringDate
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.TrainingsAdapter
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.calendarUtils.indexCurrentDay
import com.mibaldi.fitapp.ui.calendarUtils.myCalendarChangesObserver
import com.mibaldi.fitapp.ui.calendarUtils.myCalendarViewManager
import com.mibaldi.fitapp.ui.calendarUtils.mySelectionManager
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.detail.DetailActivity
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_training_form.*
import kotlinx.android.synthetic.main.activity_training_form.recycler
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.util.*
import kotlin.collections.HashMap


class TrainingActivity : BaseActivity() {

    private val viewModel: TrainingViewModel by lifecycleScope.viewModel(this)
    private val calendar = Calendar.getInstance()
    private lateinit var trainingAdapter: TrainingsAdapter
    private var currentMonth = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_form)
        viewModel.trainings.observe(this, Observer(::setupCalendar))
        trainingAdapter = TrainingsAdapter(viewModel::onTrainingClicked)
        recycler.adapter = trainingAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.navigation.observe (this,Observer {event ->
            event.getContentIfNotHandled()?.let {
                startActivity<DetailActivity> {
                    putExtra(DetailActivity.TRAINING,it.id)
                }
            }
        })
    }

    fun setupCalendar(model: HashMap<String,List<Training>>) {
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]
        val myCalendarViewManager = myCalendarViewManager(model)
        val singleRowCalendar = main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver(::setupHeader) {
                trainingAdapter.trainings = model[generateStringDate(it)] ?: emptyList()
            }
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 30
            includeCurrentDate = true
            init()
        }
        with(singleRowCalendar){
            select(indexCurrentDay(this))
            initialPositionIndex = indexCurrentDay(this)
        }
    }

    private fun setupHeader(date: Date) {
        tvMonth.text = DateUtils.getMonthName(date)
        tvYear.text = DateUtils.getYear(date)
    }
}