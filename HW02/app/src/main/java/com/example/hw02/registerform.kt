package com.example.hw02

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class registerform : AppCompatActivity() {

    lateinit var edtUsername: EditText
    lateinit var edtPassword: EditText
    lateinit var edtRetype: EditText
    lateinit var edtBirthdate: EditText
    lateinit var btnSelect: Button
    lateinit var rgGender: RadioGroup
    lateinit var rbMale: RadioButton
    lateinit var rbFemale: RadioButton
    lateinit var cbTennis: CheckBox
    lateinit var cbFutbal: CheckBox
    lateinit var cbOthers: CheckBox
    lateinit var btnReset: Button
    lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerform)
        configureGlassWindow(findViewById(R.id.registerRoot))

        initViews()

        // button reset
        btnReset.setOnClickListener { resetForm() }
        btnSignUp.setOnClickListener { openResultForm() }
        btnSelect.setOnClickListener { showDatePickerDialog() }
    }

    private fun initViews() {
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        edtRetype = findViewById(R.id.edtRetype)
        edtBirthdate = findViewById(R.id.edtBirthdate)
        btnSelect = findViewById(R.id.btnSelect)
        rgGender = findViewById(R.id.rgGender)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        cbTennis = findViewById(R.id.cbTennis)
        cbFutbal = findViewById(R.id.cbFutbal)
        cbOthers = findViewById(R.id.cbOthers)
        btnReset = findViewById(R.id.btnReset)
        btnSignUp = findViewById(R.id.btnSignUp)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentDateStr = edtBirthdate.text.toString().trim()
        val parts = currentDateStr.split("/")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            val year = parts[2].toIntOrNull()
            if (day != null && month != null && year != null && month in 1..12) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            }
        }

        val datePickerDialog = DatePickerDialog(
            this,
            androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    Locale.US,
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                edtBirthdate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openResultForm() {
        // Khôi Nguyên cần uncomment khối if phía dưới sau khi đã thực hiện hàm validate và đổi tên hàm(nếu khác).
//        if (!validate()) return

        val gender = when (rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "Male"
            R.id.rbFemale -> "Female"
            else -> ""
        }
        val hobbies = listOf(cbTennis, cbFutbal, cbOthers)
            .filter { it.isChecked }
            .joinToString(", ") { it.text.toString() }
        val data = Bundle().apply {
            putString("username", edtUsername.text.toString())
            putString("password", edtPassword.text.toString())
            putString("birthdate", edtBirthdate.text.toString())
            putString("gender", gender)
            putString("hobbies", hobbies)
        }
        startActivity(Intent(this, resultform::class.java).putExtras(data))
    }

    // xóa trắng toàn bộ form cho người dùng nhập lại
    private fun resetForm() {
        edtUsername.text.clear()
        edtPassword.text.clear()
        edtRetype.text.clear()
        edtBirthdate.text.clear()

        rgGender.clearCheck()

        cbTennis.isChecked = false
        cbFutbal.isChecked = false
        cbOthers.isChecked = false

        // đưa con trỏ về ô đầu tiên
        edtUsername.requestFocus()
    }
}
