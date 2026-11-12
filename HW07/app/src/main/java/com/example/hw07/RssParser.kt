package com.example.hw07

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

object RssParser {

    fun fetchRssItems(urlAddress: String): ArrayList<SingleItem> {
        val newsList = ArrayList<SingleItem>()
        var httpConnection: HttpURLConnection? = null

        try {
            val url = URL(urlAddress)
            httpConnection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }

            if (httpConnection.responseCode == HttpURLConnection.HTTP_OK) {
                httpConnection.inputStream.use { inputStream ->
                    val dbf = DocumentBuilderFactory.newInstance()
                    val db = dbf.newDocumentBuilder()
                    val dom = db.parse(inputStream)
                    val treeElements = dom.documentElement

                    val itemNodes = treeElements.getElementsByTagName("item")
                    for (i in 0 until itemNodes.length) {
                        newsList.add(dissectItemNode(itemNodes, i))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            httpConnection?.disconnect()
        }

        return newsList
    }

    private fun dissectItemNode(nodeList: NodeList, index: Int): SingleItem {
        val entry = nodeList.item(index) as Element
        val title = getTagValue(entry, "title")
        val description = getTagValue(entry, "description")
        val pubDate = getTagValue(entry, "pubDate")
        val link = getTagValue(entry, "link")

        return SingleItem(
            pubDate = pubDate,
            title = title,
            description = description,
            link = link
        )
    }

    private fun getTagValue(parent: Element, tagName: String): String {
        val nodes = parent.getElementsByTagName(tagName)
        if (nodes.length == 0) return ""
        return nodes.item(0)?.textContent?.trim().orEmpty()
    }
}