package com.example.hw07

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class ShowHeadlinesActivity : AppCompatActivity() {

    private var newsList = ArrayList<SingleItem>()
    private lateinit var myListView: ListView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val paperName = intent.getStringExtra("PAPER_NAME").orEmpty()
        val channelName = intent.getStringExtra("CHANNEL_NAME").orEmpty()
        val urlAddress = intent.getStringExtra("URL_ADDRESS").orEmpty()

        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)
        myListView = findViewById(R.id.myListView)
        progressBar = findViewById(R.id.progressBar)

        tvScreenTitle.text = "ITEMS IN CHANNEL $channelName – $paperName"

        myListView.setOnItemClickListener { _, _, position, _ ->
            val selectedStoryItem = newsList[position]
            showNiceDialogBox(selectedStoryItem, channelName)
        }

        loadRssInBackground(urlAddress)
    }

    private fun loadRssInBackground(urlAddress: String) {
        progressBar.visibility = View.VISIBLE
        val executor = Executors.newSingleThreadExecutor()
        val mainHandler = Handler(Looper.getMainLooper())

        executor.execute {
            val result = RssParser.fetchRssItems(urlAddress)
            mainHandler.post {
                progressBar.visibility = View.GONE
                newsList = result
                if (newsList.isEmpty()) {
                    Toast.makeText(this, "Không tải được dữ liệu RSS!", Toast.LENGTH_SHORT).show()
                } else {
                    val titles = newsList.map { it.title }
                    val adapter = ArrayAdapter(
                        this,
                        R.layout.my_simple_list_item_1,
                        titles
                    )
                    myListView.adapter = adapter
                }
            }
        }
    }

    private fun showNiceDialogBox(selectedStoryItem: SingleItem, channelName: String) {
        val title = selectedStoryItem.title
        // Loại bỏ thẻ <img> nếu có trong CDATA description của báo Việt Nam để hộp thoại hiển thị gọn gàng
        val rawDescription = selectedStoryItem.description.replace(Regex("<img[^>]*>"), "")
        val cleanDescription = Html.fromHtml(rawDescription, Html.FROM_HTML_MODE_LEGACY).toString().trim()

        val displayBody = if (title.equals(cleanDescription, ignoreCase = true) || cleanDescription.isEmpty()) {
            title
        } else {
            "$title\n\n$cleanDescription"
        }

        val builder = AlertDialog.Builder(this)
            .setTitle(channelName)
            .setMessage(displayBody)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        if (selectedStoryItem.link.isNotEmpty()) {
            builder.setNegativeButton("More") { _, _ ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedStoryItem.link))
                startActivity(browserIntent)
            }
        }

        builder.show()
    }
}