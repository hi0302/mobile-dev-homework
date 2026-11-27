# HW08 - Kabaddi Kounter: ViewModel, Data Binding & Biến đổi LiveData

Ứng dụng minh họa kiến trúc ứng dụng Android (Giao diện) theo nội dung bài giảng **Bài 8: Kiến trúc ứng dụng (Giao diện)** của môn *Phát triển phần mềm cho thiết bị di động* (HCMUS).

## Nội dung kiến trúc triển khai

1. **ViewModel (`ScoreViewModel`)**:
   - Quản lý trạng thái và dữ liệu tính điểm (`scoreA`, `scoreB`, `selectedTeam`), tách biệt khỏi vòng đời của `MainActivity`.
   - Dữ liệu được bảo toàn khi xoay màn hình hoặc thay đổi cấu hình.

2. **Data Binding (`activity_main.xml`)**:
   - Khai báo `<data><variable name="viewModel" type="com.group1.hw08.ScoreViewModel" /></data>`.
   - Tự động liên kết các thuộc tính dữ liệu và LiveData trực tiếp lên View thông qua biểu thức Data Binding (ví dụ: `@{String.valueOf(viewModel.scoreA)}`, `@{viewModel.resultA}`).
   - Gán `binding.lifecycleOwner = this` trong `MainActivity` để Data Binding tự động lắng nghe cập nhật từ LiveData.

3. **LiveData & Biến đổi LiveData (`map` và `switchMap`)**:
   - **`MutableLiveData` / `LiveData`**: Bảo vệ tính toàn vẹn dữ liệu (chỉ sửa đổi bên trong ViewModel qua biến `private _*`, bên ngoài chỉ đọc qua `LiveData`).
   - **Biến đổi `map`** (Slide 25–28):
     - `resultA`: Biến đổi từ `scoreA` sang chuỗi kết quả: trả về `"A Wins"` khi `scoreA >= 10`, ngược lại trả về chuỗi rỗng `""`.
     - `resultB`: Biến đổi từ `scoreB` sang chuỗi kết quả: trả về `"B Wins"` khi `scoreB >= 10`, ngược lại trả về chuỗi rỗng `""`.
   - **Biến đổi `switchMap`** (Slide 29–32):
     - `selectedTeam`: Giữ tên đội hiện đang được chọn theo dõi (`"Team A"` hoặc `"Team B"`). Nhấn nút **Select Team** sẽ hoán đổi giữa 2 đội.
     - `selectedScore`: Biến đổi linh hoạt theo `selectedTeam` để trỏ tới `scoreA` (nếu chọn Team A) hoặc `scoreB` (nếu chọn Team B).

4. **Chức năng bổ sung**:
   - Nút **Reset** giúp đưa điểm số cả hai đội về 0 và đặt lại đội được chọn về `Team A`.

## Hướng dẫn chạy ứng dụng

1. Mở thư mục `HW08` trong **Android Studio**.
2. Chờ Gradle sync hoàn tất.
3. Chạy cấu hình `app` trên Emulator hoặc thiết bị thật.

## Kiểm thử (Unit Tests)

Bộ kiểm thử đơn vị nằm tại `app/src/test/java/com/group1/hw08/ScoreViewModelTest.kt` kiểm tra toàn bộ:
- Khởi tạo giá trị ban đầu.
- Tăng điểm từng đội độc lập (`incrementScore`).
- Cơ chế `map` cho `resultA` và `resultB`.
- Cơ chế `switchMap` cho `selectedScore` khi chuyển đội (`selectTeam`).
- Khôi phục trạng thái với `resetScores`.
