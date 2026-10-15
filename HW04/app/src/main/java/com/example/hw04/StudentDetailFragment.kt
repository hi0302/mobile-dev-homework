package com.example.hw04

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class StudentDetailFragment : Fragment(R.layout.fragment_student_detail) {

    private val viewModel: StudentViewModel by activityViewModels()

    private lateinit var tvTitleId: TextView
    private lateinit var tvName: TextView
    private lateinit var tvClass: TextView
    private lateinit var tvGpa: TextView
    private lateinit var btnFirst: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var btnLast: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitleId = view.findViewById(R.id.tvTitleId)
        tvName = view.findViewById(R.id.tvName)
        tvClass = view.findViewById(R.id.tvClass)
        tvGpa = view.findViewById(R.id.tvGpa)
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
            tvTitleId.text = getString(R.string.detail_placeholder)
            tvName.text = ""
            tvClass.text = ""
            tvGpa.text = ""
        } else {
            tvTitleId.text = student.id
            tvName.text = getString(R.string.fmt_name, student.name)
            tvClass.text = getString(R.string.fmt_class, student.className)
            tvGpa.text = getString(R.string.fmt_gpa, student.gpa)
        }

        val canPrev = viewModel.canGoPrevious(index)
        val canNext = viewModel.canGoNext(index)
        btnFirst.setDimmed(!canPrev)
        btnPrevious.setDimmed(!canPrev)
        btnNext.setDimmed(!canNext)
        btnLast.setDimmed(!canNext)
    }
}