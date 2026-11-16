package com.example.hw07

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val loadedLogos = mutableMapOf<String, Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gvNewsSources = findViewById<GridView>(R.id.gvNewsSources)
        val paperNames = RssDataSource.newsSources.keys.toList()

        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_news_source,
            paperNames
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_news_source, parent, false)

                val imgLogo = itemView.findViewById<ImageView>(R.id.imgNewspaperLogo)
                val tvFallback = itemView.findViewById<TextView>(R.id.tvFallbackName)

                val paperName = paperNames[position]
                val bitmap = loadedLogos[paperName]
                    ?: RssParser.getCachedOrFallbackLogo(context, paperName)

                if (bitmap != null) {
                    imgLogo.visibility = View.VISIBLE
                    tvFallback.visibility = View.GONE
                    imgLogo.setImageBitmap(bitmap)
                } else {
                    imgLogo.visibility = View.GONE
                    tvFallback.visibility = View.VISIBLE
                    tvFallback.text = paperName
                }

                return itemView
            }
        }

        gvNewsSources.adapter = adapter

        val executor = Executors.newFixedThreadPool(paperNames.size.coerceAtLeast(1))
        val mainHandler = Handler(Looper.getMainLooper())
        val appContext = applicationContext

        paperNames.forEach { paperName ->
            val firstChannelUrl = RssDataSource.newsSources[paperName]?.firstOrNull()?.rssUrl
                ?: return@forEach

            executor.execute {
                val bitmap = RssParser.fetchPaperLogo(appContext, paperName, firstChannelUrl)
                if (bitmap != null) {
                    mainHandler.post {
                        loadedLogos[paperName] = bitmap
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        gvNewsSources.setOnItemClickListener { _, _, position, _ ->
            val selectedPaper = paperNames[position]
            val intent = Intent(this, ChannelsActivity::class.java).apply {
                putExtra("PAPER_NAME", selectedPaper)
            }
            startActivity(intent)
        }
    }
}