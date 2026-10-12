package com.example.hw03

/**
 * TODO: viết logic phân trang thật + PaginatorTest.
 *  - pageCount: làm tròn lên (15/5 = 3, 16/5 = 4, rỗng = 0)
 *  - clampPage(page): ép về [0, pageCount - 1]
 *  - itemsOf(page): các item của trang, trang không tồn tại trả về rỗng
 *  - require(pageSize > 0)
 * Giữ nguyên chữ ký: Paginator<T>(items, pageSize), pageCount, clampPage, itemsOf
 */
class Paginator<T>(items: List<T>, val pageSize: Int) {
    private val data = items.toList()

    val pageCount: Int get() = 1

    fun clampPage(page: Int): Int = 0

    fun itemsOf(page: Int): List<T> = data.take(pageSize)
}
