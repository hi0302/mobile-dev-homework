# HW02: Xây dựng giao diện người dùng

Form đăng ký gồm hai activity: `registerform` (màn đăng ký, màn chạy đầu tiên) và `resultform` (màn hiển thị kết quả). Tên activity phải đúng như đề yêu cầu.

[← Quay lại README chung](../README.md)

## Tiến độ hiện tại

- [x] Tạo project, đẩy lên GitHub (Diễm Thúy)
- [x] Layout `registerform` (username, password, retype, birthdate, gender, hobbies) (Diễm Thúy)
- [x] Password và Retype hiện dấu chấm (`textPassword`) (Diễm Thúy)
- [x] Nút **Reset** xóa trắng toàn bộ form (Diễm Thúy)
- [x] Layout `resultform` và nhận dữ liệu từ `registerform` (Đăng Khoa)
- [x] Nút **Exit** thoát hẳn ứng dụng (Đăng Khoa)
- [ ] Validate: retype khớp password, birthdate đúng `dd/mm/yyyy`, không để trống, đã chọn gender, báo lỗi bằng Toast (Khôi Nguyên)
- [ ] Nút **Select** mở `DatePickerDialog`, tự điền vào ô Birthdate (Gia Bảo)
- [ ] Nút **Sign-up** gom dữ liệu, validate xong thì gửi sang `resultform` (Gia Bảo)

Hai màn hình dùng giao diện glassmorphism: nền gradient với quầng sáng mềm, thẻ bán trong suốt có viền sáng, ô nhập có trạng thái focus và nút gradient. Các view ID, tên activity và key Bundle giữ nguyên theo quy ước. Hiệu ứng kính dùng drawable XML, tương thích từ API 24.

`resultform` đã khai báo trong manifest (`exported="false"`), đọc năm String từ `intent.extras`, tự che mật khẩu bằng `*`, hiện `None` khi hobbies rỗng và gọi `finishAffinity()` khi bấm Exit. `registerform` vẫn là màn khởi chạy, giữ đầy đủ view trong `initViews()` và Reset xóa toàn bộ dữ liệu rồi focus Username.

Nút Sign-up đã nối luồng đăng ký → kết quả: gom năm giá trị theo key Bundle bên dưới rồi mở `resultform`. Validation vẫn đang chờ Khôi Nguyên; Gia Bảo cần nối lời gọi `validate()` trước khi chuyển màn hình và bổ sung DatePicker cho nút Select. Vì vậy mục Sign-up trong checklist chưa đánh dấu hoàn tất và nút Select hiện chỉ có giao diện.

Kiểm tra trên emulator / thiết bị bằng `./gradlew connectedDebugAndroidTest`. `FormActivitiesTest` kiểm tra hiển thị Bundle, che mật khẩu sau khi tạo lại activity, hobbies rỗng, thiếu Bundle, chuỗi dài, Reset, bấm Sign-up để chuyển dữ liệu thật từ `registerform` và Exit đóng cả hai activity.

## Phân công

| Thành viên | Branch | Nhiệm vụ |
| --- | --- | --- |
| Diễm Thúy | `hw02-registerform` | Setup + `registerform` UI + nút Reset |
| Đăng Khoa | `hw02-resultform` | `resultform` + nút Exit |
| Khôi Nguyên | `hw02-validation` | Validation |
| Gia Bảo | `hw02-datepicker` | DatePicker + nút Sign-up |

### Diễm Thúy: Setup + `registerform` UI + Reset (đã hoàn thành)

- Đổi `MainActivity` thành activity `registerform` (màn chạy đầu tiên)
- Layout `registerform` gồm: Username, Password, Retype, Birthdate + nút Select, Gender (RadioGroup), Hobbies (3 CheckBox), nút Reset và Sign-up
- Password và Retype dùng `inputType="textPassword"` nên hiện dấu chấm, không hiện ký tự
- Nút **Reset**: xóa trắng 4 ô nhập, bỏ chọn gender (`clearCheck()`), bỏ tick 3 checkbox, đưa con trỏ về ô Username
- Khai báo sẵn tất cả view trong class `registerform` (hàm `initViews()`), để các bạn khác pull về là dùng được luôn

### Đăng Khoa: `resultform` + Exit

- Tạo activity `resultform` (New > Activity > Empty Views Activity)
- Nhận `Bundle` từ `intent.extras`, hiển thị Username, Password (dạng `*`), Birthdate, Gender, Hobbies
- Nếu hobbies rỗng thì hiện `None`
- Nút Exit dùng `finishAffinity()`

### Khôi Nguyên: Validation

- Viết hàm `validate(): Boolean` trong `registerform`
- Retype phải khớp Password
- Birthdate dùng `SimpleDateFormat("dd/MM/yyyy")` với `isLenient = false` để bắt cả ngày không tồn tại (31/02/2000)
- Check ô trống và chưa chọn gender
- Mỗi lỗi hiện một `Toast` rõ ràng

### Gia Bảo: DatePicker + Sign-up

- `btnSelect` mở `DatePickerDialog`, điền ngày vào `edtBirthdate` với số 0 đứng trước (`05/03/2001`)
- Lấy gender từ `rgGender`, hobbies từ các checkbox (nối thành chuỗi `Tennis, Futbal`)
- `btnSignUp`: gọi `validate()`, hợp lệ thì đóng gói `Bundle` rồi `startActivity` sang `resultform`

## Quy ước chung của HW02

**ID các view trong `registerform`** (không tự ý đổi tên):

`edtUsername`, `edtPassword`, `edtRetype`, `edtBirthdate`, `btnSelect`, `rgGender`, `rbMale`, `rbFemale`, `cbTennis`, `cbFutbal`, `cbOthers`, `btnReset`, `btnSignUp`

**Key của Bundle** khi gửi từ `registerform` sang `resultform`:

| Key | Kiểu | Ví dụ |
| --- | --- | --- |
| `username` | String | `NguyenVanA` |
| `password` | String | mật khẩu gốc, `resultform` tự che bằng `*` |
| `birthdate` | String | `20/10/1989` |
| `gender` | String | `Male` / `Female` |
| `hobbies` | String | `Tennis, Futbal` |

## Nộp bài

Nén folder `HW02` thành `MSSV.zip` hoặc `MSSV.rar` (hỏi thầy dùng MSSV của ai), xóa `build/` trước khi nén.
