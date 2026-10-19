package com.example.hw04

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import java.util.Locale

class StudentDetailFragment : Fragment(R.layout.fragment_student_detail) {

    private val viewModel: StudentViewModel by activityViewModels()

    private lateinit var imgDetailAvatar: ImageView
    private lateinit var tvTitleId: TextView
    private lateinit var tvPlaceholderHint: TextView
    private lateinit var layoutStudentInfo: LinearLayout
    private lateinit var tvName: TextView
    private lateinit var tvClass: TextView
    private lateinit var tvGpa: TextView
    private lateinit var tvGpaBadge: TextView
    private lateinit var btnFirst: MaterialButton
    private lateinit var btnPrevious: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var btnLast: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgDetailAvatar = view.findViewById(R.id.imgDetailAvatar)
        tvTitleId = view.findViewById(R.id.tvTitleId)
        tvPlaceholderHint = view.findViewById(R.id.tvPlaceholderHint)
        layoutStudentInfo = view.findViewById(R.id.layoutStudentInfo)
        tvName = view.findViewById(R.id.tvName)
        tvClass = view.findViewById(R.id.tvClass)
        tvGpa = view.findViewById(R.id.tvGpa)
        tvGpaBadge = view.findViewById(R.id.tvGpaBadge)
        btnFirst = view.findViewById(R.id.btnFirst)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnNext = view.findViewById(R.id.btnNext)
        btnLast = view.findViewById(R.id.btnLast)

        btnFirst.setOnClickListener { viewModel.first() }
        btnPrevious.setOnClickListener { viewModel.previous() }
        btnNext.setOnClickListener { viewModel.next() }
        btnLast.setOnClickListener { viewModel.last() }

        viewModel.selectedIndex.observe(viewLifecycleOwner) { index ->
            render(index)
        }
    }

    private fun render(index: Int) {
        val student = viewModel.students.getOrNull(index)
        if (student == null) {
            imgDetailAvatar.setImageResource(R.drawable.ic_avatar_1)
            tvTitleId.text = getString(R.string.detail_placeholder)
            tvPlaceholderHint.visibility = View.VISIBLE
            layoutStudentInfo.visibility = View.GONE
        } else {
            imgDetailAvatar.setImageResource(student.avatarRes)
            tvTitleId.text = student.id
            tvPlaceholderHint.visibility = View.GONE
            layoutStudentInfo.visibility = View.VISIBLE

            tvName.text = student.name
            tvClass.text = student.className
            tvGpa.text = String.format(Locale.getDefault(), "%.1f", student.gpa)

            // Dynamic GPA Badge styling
            when {
                student.gpa >= 8.0 -> {
                    tvGpaBadge.text = getString(R.string.gpa_excellent)
                    tvGpaBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.gpa_high))
                    tvGpaBadge.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gpa_high_bg)
                }
                student.gpa >= 6.5 -> {
                    tvGpaBadge.text = getString(R.string.gpa_good)
                    tvGpaBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.gpa_mid))
                    tvGpaBadge.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gpa_mid_bg)
                }
                else -> {
                    tvGpaBadge.text = getString(R.string.gpa_average)
                    tvGpaBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.gpa_low))
                    tvGpaBadge.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gpa_low_bg)
                }
            }
        }

        val canPrev = viewModel.canGoPrevious(index)
        val canNext = viewModel.canGoNext(index)
        btnFirst.setDimmed(!canPrev)
        btnPrevious.setDimmed(!canPrev)
        btnNext.setDimmed(!canNext)
        btnLast.setDimmed(!canNext)
    }
}