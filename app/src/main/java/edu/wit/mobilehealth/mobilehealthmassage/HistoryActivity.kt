package edu.wit.mobilehealth.mobilehealthmassage

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.fragment_emgscanitem.view.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        list_header.setBackgroundColor(Color.WHITE)
        list_header.date.typeface = Typeface.DEFAULT_BOLD
        list_header.date.textSize = 16F
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}