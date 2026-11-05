# HW06 - Progress bar với luồng nền

Ứng dụng Kotlin + XML minh họa thanh tiến trình được cập nhật từ một luồng nền.

## Chạy ứng dụng

Mở riêng thư mục `HW06` trong Android Studio, chờ Gradle đồng bộ, sau đó chạy cấu hình `app` trên emulator hoặc thiết bị Android.

## Cách sử dụng

Nhập số bước dương rồi nhấn **Do it again**. Luồng nền tăng tiến trình từng bước một; phần trăm được tính từ số bước đã hoàn thành trên tổng số đã nhập. Ví dụ: với `100`, một bước tương ứng với 1%; với `1000`, cần 10 bước để tăng 1%. Khi đạt 100%, có thể nhấn nút lần nữa để chạy lại.
