package com.example.hw03

import androidx.annotation.DrawableRes

/** Một dòng trong danh bạ: id duy nhất, họ tên, số điện thoại và ảnh đại diện. */
data class Contact(
    val id: Int,
    val name: String,
    val phone: String,
    @param:DrawableRes val avatar: Int,
) {
    companion object {
        /** Giá trị id nghĩa là "chưa chọn ai". */
        const val NO_ID = -1
    }
}
