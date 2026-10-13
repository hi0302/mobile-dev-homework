package com.example.hw03

/** Phân trang trên bản sao dữ liệu đầu vào; chỉ số trang bắt đầu từ 0. */
class Paginator<T>(items: List<T>, val pageSize: Int) {
    init {
        require(pageSize > 0) { "pageSize must be greater than zero" }
    }

    private val data = items.toList()

    // Dùng phép chia và phần dư để tránh tràn số khi pageSize rất lớn.
    val pageCount: Int = data.size / pageSize + if (data.size % pageSize == 0) 0 else 1

    fun clampPage(page: Int): Int = page.coerceIn(0, maxOf(pageCount - 1, 0))

    fun itemsOf(page: Int): List<T> {
        if (page !in 0 until pageCount) return emptyList()
        val start = page * pageSize
        val count = minOf(pageSize, data.size - start)
        return data.subList(start, start + count).toList()
    }
}
