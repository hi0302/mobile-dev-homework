package com.example.ic03

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    private lateinit var photos: List<PhotoItem>
    private lateinit var scrollViewgroup: LinearLayout
    private lateinit var scroller: HorizontalScrollView
    private lateinit var imageSelected: ImageView
    private lateinit var imageLoader: PhotoImageLoader
    private lateinit var previous: ImageButton
    private lateinit var next: ImageButton
    private var selectedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureWindow()

        photos = PhotoCatalog.load(this)
        scrollViewgroup = findViewById(R.id.viewgroup)
        scroller = findViewById(R.id.thumbnailScroller)
        imageSelected = findViewById(R.id.imageSelected)
        imageSelected.clipToOutline = true
        previous = findViewById(R.id.previousPhoto)
        next = findViewById(R.id.nextPhoto)
        imageLoader = PhotoImageLoader(resources)
        findViewById<TextView>(R.id.photoCount).text =
            resources.getQuantityString(R.plurals.photo_count, photos.size, photos.size)
        for (heading in listOf(R.id.galleryTitle, R.id.collectionTitle, R.id.photoTitle)) {
            ViewCompat.setAccessibilityHeading(findViewById(heading), true)
        }

        populateScrollView()
        previous.setOnClickListener { selectPhoto(selectedIndex - 1) }
        next.setOnClickListener { selectPhoto(selectedIndex + 1) }
        val savedSlot = savedInstanceState?.getString(SELECTED_SLOT)
        val restoredIndex = photos.indexOfFirst { it.slot == savedSlot }.coerceAtLeast(0)
        selectPhoto(restoredIndex, animate = false)
    }

    private fun populateScrollView() {
        photos.forEachIndexed { position, photo ->
            // Same inflate → bind icon/caption → addView → click listener flow as the slides.
            val frame = layoutInflater.inflate(
                R.layout.frame_icon_caption, scrollViewgroup, false
            )
            // Resource-safe unique view IDs; tag holds the lecture's zero-based position.
            frame.id = View.generateViewId()
            frame.tag = position
            // Inflated children share XML IDs. Restore visual selection from our saved slot,
            // rather than letting hierarchy restoration apply another frame's saved state.
            frame.isSaveFromParentEnabled = false
            frame.contentDescription = getString(
                R.string.thumbnail_description, position + 1, getString(photo.title)
            )
            frame.findViewById<TextView>(R.id.caption).text =
                getString(R.string.photo_caption, position + 1)
            val icon = frame.findViewById<ImageView>(R.id.icon)
            icon.clipToOutline = true
            imageLoader.load(icon, photo.thumbnail, 320)
            scrollViewgroup.addView(frame)
            frame.setOnClickListener { selectPhoto(position) }
        }
    }

    private fun selectPhoto(position: Int, animate: Boolean = true) {
        if (position !in photos.indices) return
        selectedIndex = position
        val photo = photos[position]
        showLargeImage(photo)
        findViewById<TextView>(R.id.photoTitle).setText(photo.title)
        findViewById<TextView>(R.id.txtMsg).text =
            getString(R.string.selection_message, position + 1, position)
        findViewById<TextView>(R.id.selectionCount).text =
            getString(R.string.position_count, position + 1, photos.size)
        findViewById<TextView>(R.id.photoNote).setText(when {
            photo.hasLargePhoto && photo.credit != 0 -> photo.credit
            photo.hasLargePhoto -> R.string.photo_note
            photo.hasThumbnailOnly -> R.string.thumbnail_only_note
            else -> R.string.sample_note
        })

        for (index in photos.indices) {
            val frame = scrollViewgroup.getChildAt(index)
            val selected = index == position
            frame.isSelected = selected
            ViewCompat.setStateDescription(frame, getString(
                if (selected) R.string.selected_state else R.string.unselected_state
            ))
            frame.findViewById<ImageView>(R.id.selectionCheck).visibility =
                if (selected) View.VISIBLE else View.GONE
            frame.findViewById<TextView>(R.id.caption).setTextColor(ContextCompat.getColor(
                this, if (selected) R.color.accent else R.color.secondary
            ))
        }
        previous.isEnabled = position > 0
        next.isEnabled = position < photos.lastIndex
        previous.alpha = if (previous.isEnabled) 1f else 0.35f
        next.alpha = if (next.isEnabled) 1f else 0.35f

        // Keep the chosen frame visible for both taps and previous/next navigation.
        scroller.post {
            val frame = scrollViewgroup.getChildAt(selectedIndex)
            val viewport = scroller.width - scroller.paddingLeft - scroller.paddingRight
            val target = (frame.left + frame.width / 2 - viewport / 2).coerceAtLeast(0)
            if (animate) scroller.smoothScrollTo(target, 0) else scroller.scrollTo(target, 0)
        }
    }

    private fun showLargeImage(photo: PhotoItem) {
        imageSelected.contentDescription = getString(
            R.string.preview_description, getString(photo.title), selectedIndex + 1
        )
        imageLoader.load(imageSelected, photo.largeImage, 1600)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SELECTED_SLOT, photos[selectedIndex].slot)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        imageLoader.close()
        super.onDestroy()
    }

    private fun configureWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val light = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK !=
            Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = light
            isAppearanceLightNavigationBars = light
        }
        val root = findViewById<View>(R.id.galleryRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)
    }

    private companion object {
        const val SELECTED_SLOT = "selected_photo_slot"
    }
}
