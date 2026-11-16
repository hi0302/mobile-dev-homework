package com.example.hw07

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.xml.parsers.DocumentBuilderFactory

data class RssFeedResult(
    val channelLogoBitmap: Bitmap?,
    val items: ArrayList<SingleItem>
)

object RssParser {

    private val paperLogoCache = ConcurrentHashMap<String, Bitmap>()

    // Bảng ánh xạ logo dự phòng đầy đủ cho tất cả các nguồn tin
    private val fallbackLogos: Map<String, Int> = mapOf(
        "VNEXPRESS" to R.drawable.logo_vnexpress,
        "DÂN TRÍ" to R.drawable.logo_dantri,
        "THANH NIÊN" to R.drawable.logo_thanh_nien,
        "TUỔI TRẺ" to R.drawable.logo_tuoi_tre,
        "HCMUS FIT" to R.drawable.logo_hcmus_fit
    )

    fun getCachedOrFallbackLogo(context: Context, paperName: String): Bitmap? {
        paperLogoCache[paperName]?.let { return it }
        return getFallbackBitmap(context, paperName)
    }

    private fun getFallbackBitmap(context: Context, paperName: String): Bitmap? {
        val resId = fallbackLogos[paperName] ?: return null
        return BitmapFactory.decodeResource(context.resources, resId)
    }

    fun fetchRssFeed(context: Context, paperName: String, urlAddress: String): RssFeedResult {
        val newsList = ArrayList<SingleItem>()
        var logoUrl = ""
        var httpConnection: HttpURLConnection? = null

        try {
            val url = URL(urlAddress)
            httpConnection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = true
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

                    // 1. Lấy đường dẫn logo từ thẻ <channel> -> <image> -> <url>
                    logoUrl = extractChannelLogoUrl(treeElements)

                    // 2. Lấy danh sách bài viết <item>
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

        // Ưu tiên cache -> ảnh tải từ RSS -> ảnh dự phòng trong res/drawable
        val bitmap = paperLogoCache[paperName]
            ?: (if (logoUrl.isNotEmpty()) downloadBitmap(logoUrl) else null)?.also {
                paperLogoCache[paperName] = it
            }
            ?: getFallbackBitmap(context, paperName)?.also {
                paperLogoCache[paperName] = it
            }

        return RssFeedResult(
            channelLogoBitmap = bitmap,
            items = newsList
        )
    }

    fun fetchPaperLogo(context: Context, paperName: String, sampleRssUrl: String): Bitmap? {
        paperLogoCache[paperName]?.let { return it }
        return fetchRssFeed(context, paperName, sampleRssUrl).channelLogoBitmap
    }

    private fun extractChannelLogoUrl(root: Element): String {
        val imageNodes = root.getElementsByTagName("image")
        if (imageNodes.length > 0) {
            val imageElement = imageNodes.item(0) as? Element ?: return ""
            val urlNodes = imageElement.getElementsByTagName("url")
            if (urlNodes.length > 0) {
                return urlNodes.item(0)?.textContent?.trim().orEmpty()
            }
        }
        return ""
    }

    private fun downloadBitmap(imageUrl: String): Bitmap? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(imageUrl)
            conn = (url.openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                conn.inputStream.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun dissectItemNode(nodeList: NodeList, index: Int): SingleItem {
        val entry = nodeList.item(index) as Element
        return SingleItem(
            pubDate = getTagValue(entry, "pubDate"),
            title = getTagValue(entry, "title"),
            description = getTagValue(entry, "description"),
            link = getTagValue(entry, "link")
        )
    }

    private fun getTagValue(parent: Element, tagName: String): String {
        val nodes = parent.getElementsByTagName(tagName)
        if (nodes.length == 0) return ""
        return nodes.item(0)?.textContent?.trim().orEmpty()
    }
}