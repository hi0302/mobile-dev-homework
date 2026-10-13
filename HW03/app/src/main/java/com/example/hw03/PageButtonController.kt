package com.example.hw03

import android.widget.Button
import android.view.View

/**
 * Điều khiển trạng thái và sự kiện của 3 nút trang.
 *  - nút của trang hiện tại: isEnabled = false và alpha = 0.35f (mờ)
 *  - các nút khác: isEnabled = true và alpha = 1f (sáng)
 *  - nút có index >= pageCount thì ẩn (View.GONE)
 *  - đặt contentDescription "Page N" cho từng nút (R.string.page_button_description)
 * Giữ nguyên chữ ký: PageButtonController(buttons, onPageSelected), fun render(pageCount, currentPage)
 */
class PageButtonController(
    private val buttons: List<Button>,
    private val onPageSelected: (Int) -> Unit,
) {
    companion object {
        private const val DISABLED_ALPHA = 0.35f
        private const val ENABLED_ALPHA = 1.0f
    }
    
    init {
        buttons.forEachIndexed { index, button ->
            button.contentDescription = button.context.getString(
                R.string.page_button_description,
                index + 1
            )
            button.setOnClickListener { 
                onPageSelected(index)
            }
        }
    }

    fun render(pageCount: Int, currentPage: Int) {
        buttons.forEachIndexed { index, button ->
            button.isSelected = index == currentPage && index < pageCount
            if (index >= pageCount) {
                button.visibility = View.GONE
                return@forEachIndexed
            }

            button.visibility = View.VISIBLE

            if (index == currentPage) {
                button.alpha = DISABLED_ALPHA
                button.isEnabled = false
            }
            else {
                button.alpha = ENABLED_ALPHA
                button.isEnabled = true
            }
        }
    }
}
