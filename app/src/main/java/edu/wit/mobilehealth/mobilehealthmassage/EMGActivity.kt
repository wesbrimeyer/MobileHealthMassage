package edu.wit.mobilehealth.mobilehealthmassage

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_emg.*
import lecho.lib.hellocharts.model.*
import java.util.*
import kotlin.collections.ArrayList


class EMGActivity : AppCompatActivity(), IBluetoothAlerts {

    private lateinit var bluetoothService: BluetoothService
    private var arduino: ArduinoController? = null
    private var emgReadings = mutableListOf<PointValue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emg)

        initiateBluetooth()

        measure_button.setOnClickListener { beginMeasure() }
        setupGraph()

        supportActionBar!!.setBackgroundDrawable(getDrawable(android.R.color.holo_red_light))

//        val dbConn = DBConnection.instance(this)
//        dbConn.addEMG(22F, "Arm")
//        dbConn.getScansList()

    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService.disconnect()
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

    val scanRate = 100L
    private fun scanEMG(duration: Int) {
        emgReadings.clear()
        val timer = Timer()
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
                            timer.cancel()
                            setupGraph()
                            measure_button.text = getString(R.string.measureEMG)
                            measure_button.isEnabled = true
                        } else {
                            val buttonText = "Measuring... ${remaining/1000}"
                            measure_button.text = buttonText
                        }
                    }
                }
            }
        }, 0, scanRate)

    }

    private fun setupGraph() {

//        val yAxisValues = mutableListOf<PointValue>()
//        for (i in 0 until emgReadings.size) {
//            yAxisValues.add(PointValue(i.toFloat(), emgReadings[i]))
//        }

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
        viewport.right = 30f
        line_chart.maximumViewport = viewport
        line_chart.currentViewport = viewport
        line_chart.isViewportCalculationEnabled = false

    }

    private fun beginMeasure() {
        measure_button.isEnabled = false
        val seconds = 30

        scanEMG(seconds)
    }

    override fun onDeviceConnected(arduino: ArduinoController) {
        runOnUiThread {
            supportActionBar!!.setBackgroundDrawable(getDrawable(android.R.color.holo_green_light))
            this.arduino = arduino
        }
    }

    override fun onDeviceDisconnected() {
        runOnUiThread {
            supportActionBar!!.setBackgroundDrawable(getDrawable(android.R.color.holo_red_light))
            this.arduino = null
        }
    }

    override fun plotValue(value: String) {
        runOnUiThread {
            emgReadings.add(PointValue(emgReadings.size.toFloat()/(1000/scanRate), value.toFloat()))
            line_chart.lineChartData.lines[0].values = emgReadings
            line_chart.lineChartData = line_chart.lineChartData
            val newLabel = "Current Value: $value"
            cur_value_text.text = newLabel
        }
    }

    override fun getContext(): Context {
        return applicationContext
    }
}
