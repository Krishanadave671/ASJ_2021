package com.gdsctsec.smartt.ui.weekday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdsctsec.smartt.R
import com.gdsctsec.smartt.util.SwipeGestureUtil
import com.gdsctsec.smartt.ui.edit.EditScreenActivity
import com.gdsctsec.smartt.ui.weekday.adapter.WeekdayAdapter
import com.gdsctsec.smartt.viewmodel.WeekdayActivityViewModelFactory
import com.gdsctsec.smartt.viewmodel.WeekdayActvityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WeekdayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekday)

        val imageViewCalendarImageWhenEmpty: ImageView = findViewById(R.id.empty_list_image_view)
        val lecturesRecyclerView: RecyclerView = findViewById(R.id.lecture_recycler_view)
        val lecNumberCountTextView: TextView = findViewById(R.id.lecture_number_text_view)
        val addNewLectureEventFloatingActionButton: FloatingActionButton =
            findViewById(R.id.lecture_add_floating_action_button)


        val intent = intent
        val weekDay = intent.getStringExtra("weekday").toString()
        val weekNum = intent.getStringExtra("weeknum")

        val viewModelFactory = WeekdayActivityViewModelFactory(this, weekDay.toString())
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(WeekdayActvityViewModel::class.java)


        //recycler View Adapter
        val timeList: MutableList<String> = mutableListOf()
        val subjectList: MutableList<String> = mutableListOf()
        val idList = mutableListOf<Int>()

        val adapter = WeekdayAdapter(timeList, subjectList)

        viewModel.getLiveLecturesData().observe(this, Observer {
            if (it.size != 0) {
                idList.clear()
                timeList.clear()
                subjectList.clear()
                imageViewCalendarImageWhenEmpty.visibility = View.INVISIBLE
                for (i in it.indices) {
                    idList.add(it[i].id)
                    timeList.add(i, it[i].startTime + "-" + it[i].endTime)
                    subjectList.add(i, it[i].lec)
                }

                adapter.notifyDataSetChanged()
            } else {
                imageViewCalendarImageWhenEmpty.visibility = View.VISIBLE
            }
        })



        lecturesRecyclerView.adapter = adapter
        lecturesRecyclerView.layoutManager = LinearLayoutManager(this)

        lecNumberCountTextView.text = timeList.size.toString() + " Lectures"

        viewModel.getLectureCountPerWeekday().observe(this, {
            for (day in it) {
                if (day.dow.name.equals(weekDay, ignoreCase = true)) {
                    lecNumberCountTextView.text = day.lecNo.toString() + " Lectures"
                }
            }
        })

        //getting the intent and the day color to be displayed by the weeknum int


        dayColor(Integer.parseInt(weekNum))

        //Image visibility
        if (timeList.isNotEmpty()) imageViewCalendarImageWhenEmpty.visibility =
            View.GONE else imageViewCalendarImageWhenEmpty.visibility =
            View.VISIBLE


        //Floating Button OnClick
        addNewLectureEventFloatingActionButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, EditScreenActivity::class.java).apply {
                putExtra("Weekday", weekDay)
                putExtra("TAG", "WeekdayActivity")
            }
            startActivity(intent)
        })

        val swipeDelete = object : SwipeGestureUtil() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.deleteLecture(idList[position])
                    idList.removeAt(position)
                    timeList.removeAt(position)
                    subjectList.removeAt(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeDelete)
        itemTouchHelper.attachToRecyclerView(lecturesRecyclerView)
    }

    fun dayColor(day: Int) {
        val dayColorChangingToolbar: Toolbar =
            findViewById(R.id.weekday_activity_toolbar_top_card_view)
        val dayTextView: TextView = findViewById(R.id.day_text_view)

        val backgroundTintAwareDrawable = DrawableCompat.wrap(dayColorChangingToolbar.background)



        when (day) {
            1 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_monday)
                )
                dayTextView.text = "Monday"
            }
            2 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_tuesday)
                )
                dayTextView.text = "Tuesday"
            }
            3 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_wednesday)
                )
                dayTextView.text = "Wednesday"
            }
            4 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_thursday)
                )
                dayTextView.text = "Thursday"
            }
            5 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_friday)
                )
                dayTextView.text = "Friday"
            }
            6 -> {
                DrawableCompat.setTint(
                    backgroundTintAwareDrawable,
                    ContextCompat.getColor(this, R.color.color_saturday)
                )
                dayTextView.text = "Saturday"
            }
            else -> {
                Log.d("WeekdayActivity error", "unforeseen error occurred in when(x)")
            }
        }
    }
}
