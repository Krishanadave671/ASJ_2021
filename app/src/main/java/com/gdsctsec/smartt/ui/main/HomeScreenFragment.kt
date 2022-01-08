package com.gdsctsec.smartt.ui.main

import android.content.Intent
import android.graphics.Color
import android.icu.text.Transliterator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdsctsec.smartt.R
import com.gdsctsec.smartt.ui.edit.EditScreenActivity
import com.gdsctsec.smartt.ui.main.adapter.SubjectsAdapter

class HomeScreenFragment : Fragment(), SubjectsAdapter.OnItemclicklistener {
    val subjectList: MutableList<String> =
        mutableListOf("Biology", "Math", "Java", "Science", "Python")
    val timeList: MutableList<String> = mutableListOf(
        "10:00 - 12:00",
        "12:00 - 14:00",
        "14:00 - 16:00",
        "16:00 - 18:00",
        "08:00 - 10:00"
    )
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val titleTextView = view.findViewById<TextView>(R.id.remindi_header)

        val word: Spannable = SpannableString("Rem")
        word.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        titleTextView.setText(word)

        val wordTwo: Spannable = SpannableString("i")
        wordTwo.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.remindi_icon_i
                )
            ), 0, wordTwo.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleTextView.append(wordTwo)

        val wordThree: Spannable = SpannableString("ndi")
        wordThree.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            wordThree.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleTextView.append(wordThree)

//        val timeList: MutableList<String> = mutableListOf(
//            "10:00 - 12:00",
//            "12:00 - 14:00",
//            "14:00 - 16:00",
//            "16:00 - 18:00",
//            "08:00 - 10:00"
//        )
        //val subjectList: MutableList<String> = mutableListOf("Biology", "Math", "Java", "Science", "Python")
        recyclerView = view.findViewById(R.id.home_recyclerView)
        recyclerView.adapter = SubjectsAdapter(subjectList, timeList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onItemClick(position: Int) {

        val chosenSubject = subjectList.get(position)
        val startTime = timeList.get(position).split(" - ")[0]
        val endTime = timeList.get(position).split(" - ")[1]
        Toast.makeText(context,"$startTime $endTime $chosenSubject",Toast.LENGTH_SHORT).show()
        val intent = Intent(context,EditScreenActivity::class.java).apply {
            putExtra("Lecture_Choosen_subject",chosenSubject)
            putExtra("Lecture_start_Time",startTime)
            putExtra("Lecture_End_time", endTime)
            putExtra("1","HomeScreenFragment")
        }
        startActivity(intent)

    }
}