package com.example.ic03

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.util.concurrent.Executors

/** Decode user-supplied rasters off the UI thread and bound their memory use. */
internal class PhotoImageLoader(private val resources: Resources) {
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile private var closed = false

    fun load(image: ImageView, @DrawableRes resource: Int, maxDimension: Int) {
        val request = Any()
        image.setTag(R.id.photo_request, request)
        val value = TypedValue()
        resources.getValue(resource, value, true)
        if (value.string?.endsWith(".xml") == true) {
            image.setImageResource(resource)
            return
        }
        // Clear the previous selection while decoding so the wrong image is never shown.
        image.setImageDrawable(null)
        executor.execute {
            if (closed) return@execute
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(resources, resource, options)
            var sample = 1
            while (maxOf(options.outWidth, options.outHeight) / sample > maxDimension) {
                sample *= 2
            }
            options.inJustDecodeBounds = false
            options.inSampleSize = sample
            val bitmap = BitmapFactory.decodeResource(resources, resource, options)
            if (!closed) {
                mainHandler.post {
                    // A quick second tap or rotation must not show an old request's result.
                    if (!closed && image.getTag(R.id.photo_request) === request) {
                        if (bitmap != null) image.setImageBitmap(bitmap)
                        else image.setImageResource(R.drawable.placeholder_coast)
                    }
                }
            }
        }
    }

    fun close() {
        closed = true
        executor.shutdownNow()
        mainHandler.removeCallbacksAndMessages(null)
    }
}
