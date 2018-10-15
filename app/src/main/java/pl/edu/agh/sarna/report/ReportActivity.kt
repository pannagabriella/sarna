package pl.edu.agh.sarna.report

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_report.*
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.*
import android.widget.LinearLayout
import android.view.ViewGroup
import android.graphics.drawable.GradientDrawable
import android.view.View
import pl.edu.agh.sarna.R
import pl.edu.agh.sarna.db.model.Processes
import pl.edu.agh.sarna.model.SubtaskStatus
import pl.edu.agh.sarna.model.AsyncResponse
import pl.edu.agh.sarna.wifi_passwords.WifiPasswordsReportTask
import java.lang.StringBuilder


class ReportActivity : AppCompatActivity(), AsyncResponse {
    private var rootState: Boolean = false
    private var eduState: Boolean = false
    private var serverState: Boolean = false
    private var reportState: Boolean = false
    private var processID: Long = 0
    private val successDrawable = GradientDrawable()
    private val failDrawable = GradientDrawable()

    private val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        initialiseLayout()
        initialiseOptions()
        DbReportTask(this, this, processID).execute()

    }

    private fun extendedReportOnClickListener(runID: Long): View.OnClickListener? {
        return View.OnClickListener {view ->
            when(view.tag) {
                getString(R.string.wifi_title) -> generateExtendedWifiReport(runID)
            }

        }
    }

    private fun generateExtendedWifiReport(runID: Long) {
        WifiPasswordsReportTask(this, this, runID).execute()
    }

    private fun initialiseLayout() {
        layoutParams.setMargins(0, 0, 0, 10)

        successDrawable.setColor(Color.rgb(0, 204, 102))
        successDrawable.cornerRadius = 10f
        successDrawable.setStroke(1, Color.WHITE)

        failDrawable.setColor(Color.rgb(255, 51, 51))
        failDrawable.cornerRadius = 10f
        failDrawable.setStroke(1, Color.WHITE)
    }

    override fun onBackPressed() {

    }
    private fun initialiseOptions() {
        rootState = intent.getBooleanExtra("root_state", false)
        eduState = intent.getBooleanExtra("edu_state", false)
        serverState = intent.getBooleanExtra("server_state", false)
        reportState = intent.getBooleanExtra("report_state", false)
        processID = intent.getLongExtra("process_id", 0)
    }

    override fun processFinish(output: Any) {
        val builder =  AlertDialog.Builder(this)
        val info = StringBuilder()
        for(subtask in output as ArrayList<SubtaskStatus>){
            info.append("${subtask.description} : ${subtask.value}\n")
        }

        builder.setTitle("Details")
                .setMessage(
                        info
                )
                .setPositiveButton("OK") { _, _ -> }
                .show();
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun load(output: Any) {
        for (subtask in output as ArrayList<SubtaskStatus>){
            val button = Button(this)
            button.tag = subtask.description
            button.text = subtask.description

            val runID = subtask._id
            if (reportState) button.setOnClickListener(extendedReportOnClickListener(runID))

            button.background = if (subtask.value as Boolean) successDrawable else failDrawable

            reportLayout.addView(button, layoutParams)
        }

    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun terminateApp(view : View){
        this.finishAffinity()
        System.exit(0)
    }
}
