package com.example.hw07

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gvNewsSources = findViewById<GridView>(R.id.gvNewsSources)
        val sourceNames = RssDataSource.newsSources.keys.toList()

        val adapter = ArrayAdapter(
            this,
            R.layout.item_news_source,
            sourceNames
        )
        gvNewsSources.adapter = adapter

        gvNewsSources.setOnItemClickListener { _, _, position, _ ->
            val selectedPaper = sourceNames[position]
            val intent = Intent(this, ChannelsActivity::class.java).apply {
                putExtra("PAPER_NAME", selectedPaper)
            }
            startActivity(intent)
        }
    }
}