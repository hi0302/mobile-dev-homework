# Mobile Project: Phát triển phần mềm cho thiết bị di động

Repository chung của nhóm cho các bài tập và đồ án môn **Phát triển phần mềm cho thiết bị di động** (ĐH Khoa học tự nhiên ĐHQG-HCM). Ngôn ngữ: **Kotlin**, giao diện: **XML layout (Views)**.

## Thành viên

| STT | MSSV | Họ tên |
| --- | --- | --- |
| 1 | 24120024 | Phạm Gia Bảo |
| 2 | 24120229 | Nguyễn Thị Diễm Thúy |
| 3 | 24120347 | Phan Lê Đăng Khoa |
| 4 | 24120394 | Nguyễn Đặng Khôi Nguyên |

## Danh sách bài tập

Mỗi bài có README riêng ghi tiến độ, phân công và quy ước của bài đó.

| Bài | Nội dung | Chi tiết |
| --- | --- | --- |
| HW02 | Xây dựng giao diện người dùng (form đăng ký) | [HW02/README.md](HW02/README.md) |
| HW03 | (cập nhật sau) | |

## Cấu trúc repo

```
mobile-dev-homework/
├── README.md        <- README chung 
├── .gitignore
├── HW02/            <- project Android Studio độc lập
│   └── README.md    <- README riêng của HW02
└── HW03/            <- (các tuần sau tạo tương tự)
```

Mỗi folder là **một project Android Studio riêng**. Khi mở, dùng **File > Open** và chọn thẳng folder bài (ví dụ `HW02`), **không mở folder gốc** `mobile-dev-homework`.

## Quy trình làm việc với Git

- `main`: chỉ chứa code đã chạy ổn, **không code thẳng lên main**
- Mỗi bạn một branch riêng cho mỗi task, đặt tên theo dạng `hwXX-ten-task` (ví dụ `hw02-validation`)
- Trước khi bắt đầu làm: `git checkout main` rồi `git pull origin main`, sau đó tạo branch mới
- Commit message ghi rõ bài, ví dụ: `HW02: add validation`
- Xong một phần thì push branch, tạo Pull Request, cả nhóm xem qua rồi merge vào `main`
- Dễ conflict ở `AndroidManifest.xml`, layout XML và file activity. Gặp conflict thì giữ cả hai phía, hoặc báo nhóm cùng xử lý
- Không push `build/`, `.idea/`, `local.properties` (đã có trong `.gitignore`)

Lệnh thường dùng:

```
git pull origin main
git checkout -b hwXX-ten-task
git add .
git commit -m "HWXX: mo ta ngan"
git push -u origin hwXX-ten-task
```

## Cách chạy project

1. Clone repo: `git clone https://github.com/hi0302/mobile-dev-homework`
2. Mở Android Studio > **File > Open** > chọn folder bài cần chạy (ví dụ `HW02`)
3. Đợi Gradle sync xong
4. Chọn emulator hoặc điện thoại thật (đã bật USB debugging) rồi bấm **Run**

## Nộp bài

- Chỉ nén folder bài cần nộp (ví dụ `HW02`), **không nén cả repo**
- Đặt tên file nén theo quy định của đề: `MSSV.zip` hoặc `MSSV.rar`
- Xóa folder `build/` trước khi nén cho nhẹ