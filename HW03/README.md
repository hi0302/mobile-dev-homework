# HW03 – Tùy biến ListView (CustomListDemo)

Danh bạ gồm 15 liên hệ, mỗi trang 5 dòng, chuyển trang bằng 3 nút 1, 2, 3. Mỗi dòng có ảnh đại diện, họ tên và số điện thoại. Chọn một dòng thì TextView phía trên hiển thị "You choose: <họ tên>".

Công nghệ: Kotlin, XML layout (Views), ListView + BaseAdapter tùy biến. Không dùng Compose.

## Tiến độ

- Phần 1 – Base: khung project, MainActivity, SelectionState (branch `hw03-base`)
- Phần 2 – Dữ liệu và Adapter (branch `hw03-adapter`)
- Phần 3 – Phân trang (branch `hw03-paginator`)
- Phần 4 – Nút chuyển trang (branch `hw03-page-buttons`)
- Cả nhóm test theo checklist cuối file
- Nén và nộp bài

## Cách làm chung

Phần 1 phải được merge vào main trước. Sau đó phần 2, 3, 4 làm song song, mỗi người một branch, không ai sửa file của người khác.

Base đã có sẵn các file stub (khung rỗng, có TODO, vẫn build và chạy được). Mỗi người chỉ mở đúng file của mình, thay phần TODO bằng code thật. **Giữ nguyên chữ ký hàm/class** ghi trong phần việc của mình để MainActivity không bị lỗi.

Quy trình git cho phần 2, 3, 4:

1. `git checkout main` rồi `git pull`
2. `git checkout -b <tên-branch-của-mình>`
3. Mở folder `HW03` bằng Android Studio (File > Open, chọn thẳng folder HW03), Sync Gradle
4. Sửa file của mình, chạy thử
5. Cập nhật README này: tick `[x]` phần của mình, ghi chú nếu có ảnh hưởng người khác
6. `git add .` rồi `git commit -m "HW03: <mô tả ngắn>"` rồi `git push -u origin <tên-branch>`
7. Mở Pull Request trên GitHub, nhờ cả nhóm review rồi merge

Nếu bị conflict (thường ở README này), giữ cả hai phía.

---

## Phần 1 – Base (Nguyễn Thị Diễm Thúy, branch `hw03-base`)

File: toàn bộ khung project, `Contact.kt`, `SelectionState.kt`, `MainActivity.kt`, layout XML, avatar, strings, colors, themes, `ContactRepository.kt`

[x] Thiết kế layout `activity_main.xml` (TextView, ListView, 3 nút) và `item_contact.xml` (ảnh, họ tên, số điện thoại, đường kẻ dưới)

[x] Viết `Contact.kt` (data class dùng chung cho cả nhóm)

[x] Viết `SelectionState.kt`: lưu và khôi phục trang đang xem + dòng đang chọn khi xoay màn hình, kiểm tra dữ liệu khôi phục có hợp lệ

[x] Viết `MainActivity.kt`: ghép adapter, paginator, nút trang; xử lý bấm dòng và đổi trang; chặn đổi sang chính trang hiện tại; xử lý insets

[x] `ContactRepository.sample()`: trả về đủ 15 liên hệ (id từ 1 đến 15, không trùng id và số điện thoại). Ảnh dùng `R.drawable.avatar_1` đến `avatar_5`, lặp vòng

[x] Tạo các file stub cho phần 2, 3, 4 (có TODO) sao cho project build được

## Phần 2 – Dữ liệu và Adapter (Phạm Gia Bảo, branch `hw03-adapter`)

File: `ContactRepository.kt`, `ContactAdapter.kt`

[x] `ContactAdapter.getView`: dùng ViewHolder và tái sử dụng `convertView`, không gọi `findViewById` lại mỗi lần 

[x] Dòng có `contact.id == selectedId` tô màu `R.color.row_selected`, dòng khác để trong suốt (nhớ đặt lại màu khi tái sử dụng view)

[x] Đặt `contentDescription` cho từng dòng (`R.string.contact_description`)

[x] Xử lý an toàn khi `position` nằm ngoài danh sách (không crash)

[x] `selectedId` chỉ gọi `notifyDataSetChanged()` khi giá trị thật sự đổi 

[x] Giữ nguyên chữ ký: `var selectedId: Int`, `fun submit(newItems: List<Contact>)`, `getItem(position): Contact?`, `ContactRepository.sample(): List<Contact>`

## Phần 3 – Phân trang (branch `hw03-paginator`)

File: `Paginator.kt`, `app/src/test/java/com/example/hw03/PaginatorTest.kt` (tạo mới)

[ ] `pageCount`: làm tròn lên (15 item chia 5 được 3, 16 item được 4, danh sách rỗng được 0)

[ ] `clampPage(page)`: ép số trang về khoảng `[0, pageCount - 1]`, danh sách rỗng trả về 0

[ ] `itemsOf(page)`: trả về các item của trang (đánh số từ 0), trang cuối lẻ vẫn đúng, trang không tồn tại trả về danh sách rỗng

[ ] `init` dùng `require(pageSize > 0)`

[ ] Sao chép danh sách đầu vào (`toList()`) để dữ liệu bên ngoài đổi không ảnh hưởng 

[ ] Viết `PaginatorTest` (JUnit): 15 item thành 3 trang, trang cuối lẻ, danh sách rỗng, trang 2 chứa item 6 đến 10, trang ngoài phạm vi, `clampPage`, `pageSize = 0` ném lỗi. Chạy test phải pass

[ ] Giữ nguyên chữ ký: `Paginator<T>(items: List<T>, pageSize: Int)`, `pageCount`, `clampPage(Int): Int`, `itemsOf(Int): List<T>`

## Phần 4 – Nút chuyển trang (branch `hw03-page-buttons`)

File: `PageButtonController.kt`

[ ]  `render(pageCount, currentPage)`: nút của trang hiện tại bị mờ (`alpha = 0.35f`) và `isEnabled = false`, các nút còn lại sáng (`alpha = 1f`, `isEnabled = true`)

[ ] Nút có index lớn hơn hoặc bằng `pageCount` thì ẩn (`View.GONE`), nút hợp lệ thì hiện (`View.VISIBLE`)

[ ] Đặt `contentDescription` "Page N" cho từng nút trong `init` (`R.string.page_button_description`)

[ ] Gắn click listener gọi `onPageSelected(index)` với index đúng thứ tự nút (0, 1, 2)

[ ] Hằng số độ mờ đặt trong `companion object`, không để số cứng rải rác 

[ ] Giữ nguyên chữ ký: `PageButtonController(buttons: List<Button>, onPageSelected: (Int) -> Unit)`, `fun render(pageCount: Int, currentPage: Int)`

---

## Quy ước id view và key Bundle

| Loại | Tên | Ghi chú |
| --- | --- | --- |
| id | mainRoot | LinearLayout gốc, áp dụng insets |
| id | txtChoose | TextView "You choose: ..." |
| id | listContacts | ListView |
| id | btnPage1, btnPage2, btnPage3 | 3 nút trang |
| id (item) | imgAvatar, txtName, txtPhone | trong item_contact.xml |
| Bundle key | key_page | trang đang xem (từ 0) |
| Bundle key | key_selected_id | id của Contact đang chọn, -1 là chưa chọn |

## Cách chạy

1. Android Studio, File > Open, chọn thẳng folder HW03 (không mở folder gốc của repo)
2. Sync Project with Gradle Files, kiểm tra Gradle JDK là jbr-17 nếu IDE báo lỗi JDK
3. Chọn máy ảo hoặc điện thoại API 24 trở lên, bấm Run
4. Chạy unit test: chuột phải `PaginatorTest`, Run

## Checklist test (cả nhóm tick sau khi merge đủ 4 phần)

- [ ] Mở app: hiện 5 dòng đầu (Nguyễn Văn A đến Đinh Văn D), nút 1 mờ, nút 2 và 3 sáng
- [ ] TextView ban đầu là "You choose: " (chưa chọn ai)
- [ ] Bấm một dòng: dòng đó tô vàng, TextView hiển thị đúng tên
- [ ] Bấm nút 2: hiện 5 dòng kế tiếp, nút 2 mờ, nút 1 và 3 sáng
- [ ] Bấm nút 3: hiện 5 dòng cuối, nút 3 mờ, nút 1 và 2 sáng
- [ ] Chọn một dòng ở trang 1, sang trang 2 rồi quay lại: dòng vẫn tô vàng, TextView vẫn giữ tên
- [ ] Bấm nút đang mờ: không có phản ứng, không bị load lại
- [ ] Bấm liên tục nhiều lần vào các nút: không crash
- [ ] Xoay màn hình: giữ nguyên trang, dòng đang chọn và TextView
- [ ] Bật dark mode: chữ và nền vẫn đọc được, dòng chọn vẫn nổi bật
- [ ] PaginatorTest pass

## Thay đổi ảnh hưởng người khác

(Ghi vào đây mỗi khi sửa file của người khác hoặc đổi id, key.)

## Ghi chú

- Toolchain giống IC03 và HW02: AGP 9.3.3, Gradle 9.6.0, compile/target SDK 37, minSdk 24
- Ảnh đại diện là vector drawable tự vẽ (avatar_1 đến avatar_5), dùng lặp vòng cho 15 liên hệ
