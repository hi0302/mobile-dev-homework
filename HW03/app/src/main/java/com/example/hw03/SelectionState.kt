package com.example.hw03

import android.os.Bundle

/** Trạng thái cần giữ khi xoay màn hình: trang đang xem + contact đang chọn. */
class SelectionState(
    var page: Int = 0,
    var selectedId: Int = Contact.NO_ID,
) {
    fun saveTo(outState: Bundle) {
        outState.putInt(KEY_PAGE, page)
        outState.putInt(KEY_SELECTED_ID, selectedId)
    }

    companion object {
        const val KEY_PAGE = "key_page"
        const val KEY_SELECTED_ID = "key_selected_id"

        /** Khôi phục và kiểm tra lại dữ liệu: trang ngoài phạm vi / id không tồn tại thì bỏ qua. */
        fun restore(saved: Bundle?, pageCount: Int, validIds: Set<Int>): SelectionState {
            if (saved == null) return SelectionState()
            val page = saved.getInt(KEY_PAGE, 0).coerceIn(0, maxOf(pageCount - 1, 0))
            val id = saved.getInt(KEY_SELECTED_ID, Contact.NO_ID)
                .takeIf { it in validIds } ?: Contact.NO_ID
            return SelectionState(page, id)
        }
    }
}
