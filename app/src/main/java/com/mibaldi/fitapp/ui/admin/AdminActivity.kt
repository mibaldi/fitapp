package com.mibaldi.fitapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.User
import com.mibaldi.domain.Workout
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.UsersAdapter
import com.mibaldi.fitapp.ui.auth.FirebaseUIActivity
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.generateTrainingList
import com.mibaldi.fitapp.ui.common.getFile
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.common.toMilliseconds
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_admin_profile.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*


const val ACTIVITY_CHOOSE_FILE: Int = 203

class AdminActivity : BaseActivity() {
    private lateinit var usersAdapter: UsersAdapter
    private val viewModel: AdminViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_profile)
        setSupportActionBar(adminProfileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.title = Firebase.auth.currentUser?.displayName

        viewModel.users.observe(this, Observer(::setupUserList))
        viewModel.userClicked.observe(this, Observer {
            selectCSVFile()
        })
        usersAdapter = UsersAdapter(viewModel::onUserClicked)
        rvusers.adapter = usersAdapter
    }

    private fun setupUserList(list: List<User>?) {
        if (!list.isNullOrEmpty()){
            usersAdapter.users = list
        }
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
        val trainingMap = generateTrainingList(readAll)
        viewModel.exportTrainings(trainingMap.values.flatten())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        startActivity<FirebaseUIActivity>{}
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}