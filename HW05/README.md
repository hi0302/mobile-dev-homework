# HW05 - SQLite

HW05 chuyển dữ liệu học sinh từ danh sách tĩnh sang SQLite cục bộ trên thiết bị.

## Cấu trúc cơ sở dữ liệu

- `LOPHOC(MaLop TEXT PRIMARY KEY, TenLop TEXT, SiSo INTEGER)`
- `HOCSINH(MaHS TEXT PRIMARY KEY, TenHS TEXT, MaLop TEXT, GPA REAL)`
- `HOCSINH.MaLop` là khóa ngoại tham chiếu `LOPHOC.MaLop`: một lớp có nhiều học sinh.

`StudentDatabaseHelper` tạo hai bảng và thêm dữ liệu mẫu khi database `students.db` được tạo lần đầu. `StudentRepository` chạy câu `SELECT ... INNER JOIN` để lấy học sinh cùng tên lớp, sĩ số và GPA. ViewModel dùng kết quả truy vấn đó cho danh sách và màn hình chi tiết.

## Chạy ứng dụng

Mở riêng thư mục `HW05` trong Android Studio, đồng bộ Gradle rồi chạy ứng dụng trên emulator hoặc thiết bị Android.
