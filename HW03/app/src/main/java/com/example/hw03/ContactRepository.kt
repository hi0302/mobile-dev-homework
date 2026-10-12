package com.example.hw03

/** Dữ liệu mẫu: 15 liên hệ = 3 trang x 5 dòng. */
object ContactRepository {
    // 5 avatar, dùng lặp vòng cho 15 người
    private val avatars = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5,
    )

    // (họ tên, số điện thoại)
    private val raw = listOf(
        "Nguyễn Văn A" to "0989897873",
        "Lê Thị B" to "0967995843",
        "Trần Văn C" to "0907955843",
        "Phan Văn C" to "0967885811",
        "Đinh Văn D" to "0988885231",
        "Hoàng Thị E" to "0912345678",
        "Vũ Văn F" to "0933221144",
        "Đặng Thị G" to "0978123456",
        "Bùi Văn H" to "0905678123",
        "Ngô Thị I" to "0386543210",
        "Dương Văn K" to "0977001122",
        "Lý Thị L" to "0938765432",
        "Võ Văn M" to "0919876543",
        "Phạm Thị N" to "0356781234",
        "Huỳnh Văn O" to "0942221133",
    )

    fun sample(): List<Contact> = raw.mapIndexed { index, (name, phone) ->
        Contact(
            id = index + 1,                       // id từ 1 đến 15, không trùng
            name = name,
            phone = phone,
            avatar = avatars[index % avatars.size], // lặp vòng avatar_1..avatar_5
        )
    }
}