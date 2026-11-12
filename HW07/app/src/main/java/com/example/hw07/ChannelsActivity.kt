package com.example.hw07

//noinspection SuspiciousImport
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChannelsActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val paperName = intent.getStringExtra("PAPER_NAME") ?: "THANH NIÊN"
        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)
        val myListView = findViewById<ListView>(R.id.myListView)

        tvScreenTitle.text = "CHANNELS IN $paperName"

        val channels = RssDataSource.newsSources[paperName].orEmpty()
        val channelNames = channels.map { it.channelName }

        val adapter = ArrayAdapter(
            this,
            R.layout.my_simple_list_item_1,
            channelNames
        )
        myListView.adapter = adapter

        myListView.setOnItemClickListener { _, _, position, _ ->
            val selectedChannel = channels[position]
            val intent = Intent(this, ShowHeadlinesActivity::class.java).apply {
                putExtra("PAPER_NAME", paperName)
                putExtra("CHANNEL_NAME", selectedChannel.channelName)
                putExtra("URL_ADDRESS", selectedChannel.rssUrl)
            }
            startActivity(intent)
        }
    }
}