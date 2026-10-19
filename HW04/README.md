# HW04 - Fragment (Danh sách học sinh)

Bài tập – Fragment, môn Phát triển phần mềm cho thiết bị di động

Ứng dụng gồm 2 Fragment trên cùng một màn hình:

- **StudentListFragment**: danh sách học sinh (hình ảnh và mã số), có vệt chọn.
- **StudentDetailFragment**: tiêu đề lớn là mã số, họ tên, lớp, điểm trung bình, cùng 4 nút First / Previous / Next / Last.

## Thành viên

| STT | MSSV | Họ và tên |
|---|---|---|
| 1 | 24120229 | Nguyễn Thị Diễm Thúy |
| 2 | 24120347 | Phan Lê Đăng Khoa |
| 3 | 24120394 | Nguyễn Đặng Khôi Nguyên |
| 4 | 24120024 | Phạm Gia Bảo |

## Tiến độ

- [x] Khởi tạo project, MainActivity, layout chứa 2 fragment
- [x] Student, StudentViewModel 
- [x] StudentRepository, tài nguyên avatar
- [x] Extension làm mờ nút `setDimmed()` 
- [x] Unit test các trường hợp biên của StudentViewModel 
- [x] StudentListFragment, StudentAdapter, vệt chọn 
- [x] StudentDetailFragment, 4 nút điều hướng, làm mờ nút 
- [x] Kiểm thử toàn bộ theo checklist bên dưới
- [x] Nén bài và chạy thử trước khi nộp

## Phân công

### Nguyễn Thị Diễm Thúy
- Tạo project Empty Views Activity (Kotlin), thêm dependency fragment-ktx, lifecycle, recyclerview.
- `Student`: `id`, `name`, `className`, `gpa`, `avatarRes`.
- `StudentViewModel`: `students`, `selectedIndex` (LiveData, `-1` là chưa chọn), `select()`, `first()`, `previous()`, `next()`, `last()`, `canGoPrevious()`, `canGoNext()`, `getSelectedStudent()`.
- `activity_main.xml` với 2 `FragmentContainerView` (trọng số 1:2), `MainActivity`, 2 fragment stub rỗng để project biên dịch được.

### Nguyễn Đặng Khôi Nguyên
- `StudentRepository`: danh sách khoảng 8-10 học sinh, dữ liệu khác với hình minh họa của đề.
- Các icon avatar trong `res/drawable`.
- `ViewExt.kt`: `fun View.setDimmed(dimmed: Boolean)` đặt `isEnabled` và `alpha` (0.4f khi mờ, 1f khi sáng).
- Unit test cho `StudentViewModel`: chọn đầu, chọn cuối, next ở cuối, previous ở đầu, index ngoài biên, chưa chọn.

### Phạm Gia Bảo
- `fragment_student_list.xml`, `item_student.xml`, `StudentAdapter`.
- Click item gọi `viewModel.select(position)`.
- Observe `selectedIndex` để đổi vệt chọn bằng `notifyItemChanged` (item cũ và item mới) và `scrollToPosition`.
- Xử lý: bấm lại item đang chọn, chưa chọn ai (không tô màu item nào).

### Phan Lê Đăng Khoa
- `fragment_student_detail.xml`, `StudentDetailFragment`.
- Observe `selectedIndex` để hiển thị mã số (tiêu đề lớn), họ tên, lớp, điểm trung bình.
- 4 nút gọi `first()`, `previous()`, `next()`, `last()` của ViewModel.
- Làm mờ: học sinh đầu thì First và Previous mờ; học sinh cuối thì Next và Last mờ; chưa chọn thì hiển thị nội dung mặc định, không crash.

## Quy ước chung

| Thành phần | Tên |
|---|---|
| Activity | `MainActivity` |
| Fragment | `StudentListFragment`, `StudentDetailFragment` |
| ViewModel dùng chung | `StudentViewModel`, lấy bằng `by activityViewModels()` |
| id trong `fragment_student_list.xml` | `rvStudents` |
| id trong `item_student.xml` | `imgAvatar`, `tvStudentId` |
| id trong `fragment_student_detail.xml` | `tvTitleId`, `tvName`, `tvClass`, `tvGpa`, `btnFirst`, `btnPrevious`, `btnNext`, `btnLast` |
| Giá trị chưa chọn | `selectedIndex = -1` |

## Checklist kiểm thử

- [x] Mở app thấy danh sách bên trái, bên phải chưa chọn gì và không crash.
- [x] Chọn một học sinh: fragment chi tiết hiện đúng mã số (tiêu đề lớn), họ tên, lớp, điểm trung bình.
- [x] Bấm First / Previous / Next / Last: vệt chọn bên danh sách đổi đúng và tự cuộn tới học sinh đó.
- [x] Chọn học sinh đầu tiên: First và Previous mờ, không bấm được.
- [x] Chọn học sinh cuối cùng: Next và Last mờ, không bấm được.
- [x] Chọn học sinh ở giữa: cả 4 nút sáng.
- [x] Xoay màn hình: giữ nguyên học sinh đang chọn và trạng thái nút.
- [x] Bấm nhanh nhiều lần liên tiếp: không crash, không lệch vệt chọn.

## Ghi chú thay đổi ảnh hưởng người khác

- Hai fragment đang là stub rỗng; ghi đè nội dung, giữ nguyên tên class và package.
- Không dùng `viewModels()` trong fragment, chỉ dùng `activityViewModels()` để hai fragment chia sẻ cùng một ViewModel.

## Quy trình git

1. `git checkout main` và `git pull` trước khi làm.
2. Mỗi người một branch như bảng phân công, commit dạng `HW04: mô tả ngắn`.
3. Không code thẳng lên `main`; xong thì tạo Pull Request, cả nhóm review rồi merge.
4. Sau mỗi phần việc: tick `[x]` ở mục Tiến độ và cập nhật README này trong cùng Pull Request.
5. Conflict ở `AndroidManifest.xml`, layout XML, file activity: giữ cả hai phía.

## Cách chạy

1. Mở Android Studio, chọn File > Open, chọn thẳng folder `HW04` (không mở folder gốc của repo).
2. Sync Project with Gradle Files, kiểm tra Gradle JDK là `jbr-17`.
3. Chạy trên emulator hoặc điện thoại thật.

## Cách nộp

- Chỉ nén folder `HW04`, xóa folder `build` trước khi nén, không kèm `.git`.
- Đặt tên `MSSV1_MSSV2_MSSV3_MSSV4.zip` theo đề, giải nén chạy thử rồi nộp trên website môn học.