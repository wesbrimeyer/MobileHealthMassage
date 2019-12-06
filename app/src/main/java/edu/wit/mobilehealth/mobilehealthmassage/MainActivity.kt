package edu.wit.mobilehealth.mobilehealthmassage

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view_list_item.view.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCards()

    }

    private fun setupCards() {
        card_view_massage.textView.text = getString(R.string.massage)
        val massageImage = getDrawable(R.drawable.project_acu)
        massageImage?.setBounds(0, 0, 150, 150)
        card_view_massage.textView.setCompoundDrawables(massageImage, null, null, null)
        card_view_massage.setOnClickListener { goToMassageActivity() }

        card_view_EMG.textView.text = getString(R.string.measureEMG)
        val emgImage = getDrawable(R.drawable.project_emg)
        emgImage?.setBounds(0, 0, 150, 150)
        card_view_EMG.textView.setCompoundDrawables(emgImage, null, null, null)
        card_view_EMG.setOnClickListener { goToEMGActivity()}

        card_view_history.textView.text = getString(R.string.history)
        val historyImage = getDrawable(R.drawable.project_history)
        historyImage?.setBounds(0, 0, 150, 150)
        card_view_history.textView.setCompoundDrawables(historyImage, null, null, null)
        card_view_history.setOnClickListener { goToHistoryActivity() }
    }

    private fun goToMassageActivity() {
        val intent = Intent(this@MainActivity, MassageActivity::class.java)
        startActivity(intent)
    }

    private fun goToEMGActivity() {
        val intent = Intent(this@MainActivity, EMGActivity::class.java)
        startActivity(intent)
    }

    private fun goToHistoryActivity() {
        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
        startActivity(intent)
    }
}