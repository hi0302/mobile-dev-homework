package com.example.hw03

import android.widget.Button

/**
 * TODO: hoàn thiện điều khiển 3 nút trang.
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
    init {
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onPageSelected(index) }
        }
    }

    fun render(pageCount: Int, currentPage: Int) {
        // TODO
    }
}
