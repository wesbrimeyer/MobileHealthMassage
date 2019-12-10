package edu.wit.mobilehealth.mobilehealthmassage

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_emg.*
import lecho.lib.hellocharts.model.*
import java.util.*


class EMGActivity : AppCompatActivity(), IBluetoothAlerts {

    private lateinit var bluetoothService: BluetoothService
    private var arduino: ArduinoController? = null
    private var emgReadings = mutableListOf<PointValue>()
    private var averageReading = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emg)

        initiateBluetooth()

        measure_button.setOnClickListener { beginMeasure() }
        measure_button.isEnabled = arduino != null
        save_button.setOnClickListener { saveScanData() }
        description_editText.addTextChangedListener(textWatcher)
        cancel_button.setOnClickListener { resetScan() }
        view_history_button.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        setupGraph()

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.subtitle = "Searching for device..."
            this.setBackgroundDrawable(getDrawable(android.R.color.holo_red_light))
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService.disconnect()
    }

    private val textWatcher = object:TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            save_button.isEnabled = !s.isNullOrBlank()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }

    }

    private fun resetScan() {
        stopScanning()
        emgReadings.clear()
        save_button.visibility = View.GONE
        cancel_button.isEnabled = false
    }

    private fun saveScanData() {
        DBConnection.instance(applicationContext).addEMG(averageReading, "${description_editText.text.trim()}")
        save_button.visibility = View.GONE
        description_editText.text.clear()
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
    }

    private fun initiateBluetooth() {
        val bltAdapter = BluetoothAdapter.getDefaultAdapter() ?: return
        if (!bltAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 11)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        40)
            }
            bluetoothService = BluetoothService.newInstance(this)
        }
    }

    private val scanRate = 100L
    private lateinit var timer: Timer
    private fun scanEMG(duration: Int) {
        emgReadings.clear()
        timer = Timer()
        var remaining = 1000L*duration

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() { //your method
                if (arduino != null) {
                    arduino!!.scanA0()
                }
                remaining -= scanRate

                if (remaining % 1000L == 0L) {
                    runOnUiThread {
                        if (remaining <= 0) {
                            save_button.visibility = View.VISIBLE
                            showRecommendation()
                            stopScanning()
                        } else {
                            val buttonText = "Measuring... ${remaining/1000}"
                            measure_button.text = buttonText
                        }
                    }
                }
            }
        }, 0, scanRate)

    }

    private fun stopScanning() {
        timer.cancel()
        measure_button.text = getString(R.string.measureEMG)
        measure_button.isEnabled = true

    }

    private fun setupGraph() {
        val line = Line(emgReadings).setColor(getColor(R.color.colorAccent))
        line.pointRadius = 0

        val lines: MutableList<Line> = mutableListOf(line)

        val data = LineChartData()
        data.lines = lines

        val axis = Axis()
        //axis.values = axisValues
        axis.textSize = 16
        axis.name = "Time (seconds)"
        axis.textColor = getColor(R.color.colorPrimaryDark)
        data.axisXBottom = axis

        val yAxis = Axis()
        yAxis.name = "EMG Reading"
        yAxis.textColor = getColor(R.color.colorPrimaryDark)
        yAxis.textSize = 16
        data.axisYLeft = yAxis

        line_chart.lineChartData = data
        val viewport = Viewport(line_chart.maximumViewport)
        viewport.top = 650f
        viewport.bottom = 0f
        viewport.left = 0f
        viewport.right = 31f
        line_chart.maximumViewport = viewport
        line_chart.currentViewport = viewport
        line_chart.isViewportCalculationEnabled = false
    }

    private fun beginMeasure() {
        measure_button.isEnabled = false
        cancel_button.isEnabled = true

        val seconds = 30
        scanEMG(seconds)
    }

    private fun showRecommendation() {
        val allTimeAverage = DBConnection.instance(this).getAverageValue()
        if (averageReading > allTimeAverage) {
            AlertDialog.Builder(this)
                    .setTitle("Massage Recommended")
                    .setMessage(R.string.recommendation)
                    .setPositiveButton(R.string.get_a_massage) { _: DialogInterface, _: Int ->
                        saveScanData()
                        startActivity(Intent(this, MassageActivity::class.java))
                    }.setNegativeButton("Cancel", null)
                    .show()
        }
    }

    override fun onDeviceConnected(arduino: ArduinoController) {
        runOnUiThread {
            supportActionBar?.setBackgroundDrawable(getDrawable(android.R.color.holo_green_light))
            supportActionBar?.subtitle = "Connected!"
            this.arduino = arduino
            measure_button.isEnabled = true
        }
    }

    override fun onDeviceDisconnected() {
        runOnUiThread {
            supportActionBar?.setBackgroundDrawable(getDrawable(android.R.color.holo_red_light))
            supportActionBar?.subtitle = "Searching for device..."
            this.arduino = null
        }
    }

    override fun plotValue(result: String) {
        val value = result.toFloat()
        averageReading = (averageReading*emgReadings.size + value)/(emgReadings.size+1)
        runOnUiThread {
            emgReadings.add(PointValue(emgReadings.size.toFloat()/(1000/scanRate), value))
            line_chart.lineChartData.lines[0].values = emgReadings
            line_chart.lineChartData = line_chart.lineChartData
            val newValLabel = "Current Value: $value"
            cur_value_text.text = newValLabel
            val newAvgLabel = "Average: ${String.format("%.1f", averageReading)}"
            average_text.text = newAvgLabel
        }
    }

    override fun getContext(): Context {
        return applicationContext
    }
}
