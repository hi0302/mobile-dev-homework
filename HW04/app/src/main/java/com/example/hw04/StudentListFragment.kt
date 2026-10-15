package com.example.hw04

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    private val viewModel: StudentViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvStudents)
        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = StudentAdapter(viewModel.students) { position ->
            viewModel.select(position)
        }
        rv.layoutManager = layoutManager
        rv.adapter = adapter
        rv.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        viewModel.selectedIndex.observe(viewLifecycleOwner) { index ->
            adapter.setSelected(index)
            if (index >= 0) {
                val firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition()
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                if (index < firstVisible || index > lastVisible) {
                    rv.scrollToPosition(index)
                }
            }
        }
    }
}