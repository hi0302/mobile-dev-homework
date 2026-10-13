package com.example.ic03

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PhotoItem(
    val slot: String,
    @param:StringRes val title: Int,
    @param:DrawableRes val thumbnail: Int,
    @param:DrawableRes val largeImage: Int,
    val hasLargePhoto: Boolean,
    val hasThumbnailOnly: Boolean,
    @param:StringRes val credit: Int = 0,
)

object PhotoCatalog {
    private data class Slot(
        val number: String,
        @param:StringRes val title: Int,
        @param:DrawableRes val placeholder: Int,
        @param:StringRes val credit: Int = 0,
    )

    // Add a slot here, then add small_photo_XX + large_photo_XX to drawable-nodpi.
    private val slots = listOf(
        Slot("01", R.string.photo_01_title, R.drawable.placeholder_forest, R.string.stock_photo_credit),
        Slot("02", R.string.photo_02_title, R.drawable.placeholder_hills, R.string.stock_photo_credit),
        Slot("03", R.string.photo_03_title, R.drawable.placeholder_coast, R.string.stock_photo_credit),
        Slot("04", R.string.photo_04_title, R.drawable.placeholder_coast, R.string.stock_photo_credit),
        Slot("05", R.string.photo_05_title, R.drawable.placeholder_coast, R.string.stock_photo_credit),
        Slot("06", R.string.photo_06_title, R.drawable.placeholder_lake, R.string.stock_photo_credit),
        Slot("07", R.string.photo_07_title, R.drawable.placeholder_coast, R.string.stock_photo_credit),
        Slot("08", R.string.photo_08_title, R.drawable.placeholder_hills, R.string.stock_photo_credit),
        Slot("09", R.string.photo_09_title, R.drawable.placeholder_hills, R.string.stock_photo_credit),
        Slot("10", R.string.photo_10_title, R.drawable.placeholder_forest, R.string.stock_photo_credit),
    )

    // Intentional name lookup: the project builds before the student's photos exist.
    // New matching resources are picked up automatically on the next build.
    @Suppress("DiscouragedApi")
    fun load(context: Context): List<PhotoItem> = slots.map { slot ->
        val small = context.resources.getIdentifier(
            "small_photo_${slot.number}", "drawable", context.packageName
        )
        val large = context.resources.getIdentifier(
            "large_photo_${slot.number}", "drawable", context.packageName
        )
        PhotoItem(
            slot = slot.number,
            title = slot.title,
            thumbnail = small.takeIf { it != 0 } ?: large.takeIf { it != 0 } ?: slot.placeholder,
            largeImage = large.takeIf { it != 0 } ?: small.takeIf { it != 0 } ?: slot.placeholder,
            hasLargePhoto = large != 0,
            hasThumbnailOnly = small != 0 && large == 0,
            credit = slot.credit,
        )
    }
}
