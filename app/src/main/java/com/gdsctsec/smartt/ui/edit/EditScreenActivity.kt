package com.gdsctsec.smartt.ui.edit

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity

import java.util.*
import android.graphics.Color
import android.os.Build

import android.text.TextWatcher
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.gdsctsec.smartt.R
import com.gdsctsec.smartt.data.TimeTable
import com.gdsctsec.smartt.data.Weekday
import com.gdsctsec.smartt.data.repository.LectureRepository
import com.gdsctsec.smartt.ui.main.HomeScreenFragment
import com.gdsctsec.smartt.viewmodel.EditScreenViewModel
import com.gdsctsec.smartt.viewmodel.EditscreenViewmodelfactory
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class EditScreenActivity : AppCompatActivity() {
    private lateinit var lectureEditText: EditText
    private lateinit var starttimeTextView: TextView
    private lateinit var endtimeTextView: TextView
    private lateinit var dayTextInputEditText: AutoCompleteTextView
    private lateinit var saveTextview: TextView
    private lateinit var cancelTextView: TextView
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var picker : MaterialTimePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editscreen)
        createNotificationChannel()
        lectureEditText = findViewById(R.id.add_lecture_edit_text)
        starttimeTextView = findViewById(R.id.textview_starttime)
        endtimeTextView = findViewById(R.id.textview_endtime)
        dayTextInputEditText = findViewById(R.id.textfield_day)
        saveTextview = findViewById(R.id.textView_save)
        cancelTextView = findViewById(R.id.textView_cancel)

        val viewModelFactory = EditscreenViewmodelfactory(this)
        var id : Int  = -1
        val viewModel = ViewModelProvider(this,viewModelFactory).get(EditScreenViewModel::class.java)
        val choice : String? = intent.getStringExtra("TAG").toString()
            if(choice == "HomeScreenFragment") {
                val startTime = intent.getStringExtra("Lecture_start_Time").toString()
                val endTime = intent.getStringExtra("Lecture_End_time").toString()
                val lecture = intent.getStringExtra("Lecture_Choosen_subject").toString()
                val weekDay = intent.getStringExtra("Lecture_Weekday").toString()
                id = Integer.parseInt(intent.getStringExtra("id").toString())

                lectureEditText.setText(lecture)
                starttimeTextView.setText(startTime)
                endtimeTextView.setText(endTime)
                dayTextInputEditText.setText(weekDay)
            }
           if(choice == "WeekdayActivity"){
               val weekDay = intent.getStringExtra("Weekday").toString()
               dayTextInputEditText.setText(weekDay)
                dayTextInputEditText.dropDownHeight = 0
               viewDisabled(dayTextInputEditText)
           }







        // dropdown
        var days = resources.getStringArray(com.gdsctsec.smartt.R.array.days)
        val ArrayAdapter = ArrayAdapter(
            this,
            com.gdsctsec.smartt.R.layout.dropdown, days
        )
        dayTextInputEditText.setAdapter(ArrayAdapter)

        // adding Textwatcher
        lectureEditText.addTextChangedListener(textWatcher)
        starttimeTextView.addTextChangedListener(textWatcher)
        endtimeTextView.addTextChangedListener(textWatcher)
        dayTextInputEditText.addTextChangedListener(textWatcher)


        starttimeTextView.setOnClickListener {
            showTimePicker(starttimeTextView)


        }
        endtimeTextView.setOnClickListener {
            viewModel.timePick(endtimeTextView)

        }
        saveTextview.setOnClickListener {
            // wil require all strings for saving in database
            var lecture: String = lectureEditText.text.toString()
            var day: String = dayTextInputEditText.text.toString()
            var starttime: String = starttimeTextView.text.toString()
            var endtime: String = endtimeTextView.text.toString()
            if(!choice.equals("HomeScreenFragment")){
                viewModel.addlecture(TimeTable(lec = lecture, weekday = Weekday.valueOf(day) , startTime = starttime, endTime = endtime))
                Toast.makeText(this, "adding", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.updatelecture(TimeTable(lec = lecture, weekday = Weekday.valueOf(day) , startTime = starttime, endTime = endtime, id = id))
                Toast.makeText(this, "updating", Toast.LENGTH_SHORT).show()
            }
//
            dayTextInputEditText.height = WRAP_CONTENT
            viewEnabled(dayTextInputEditText)

            setAlarm()
           finish()
        }


        cancelTextView.setOnClickListener {
            dayTextInputEditText.height = WRAP_CONTENT
            viewEnabled(dayTextInputEditText)
            finish()
        }
    }

    private fun cancelAlarm() {


        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Alarm Cancelled",Toast.LENGTH_LONG).show()



    }

    private fun setAlarm() {


        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.setRepeating(

            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent

        )

        Toast.makeText(this,"Alarm set Successfuly",Toast.LENGTH_SHORT).show()

    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name : CharSequence = "foxandroidReminderChannel"
            val description = "Channel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("foxandroid",name,importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun showTimePicker(v : TextView) {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager,"foxandroid")

        picker.addOnPositiveButtonClickListener {

            if (picker.hour > 12){

                v.text =
                    String.format("%02d",picker.hour - 12) + " : " + String.format(
                        "%02d",
                        picker.minute
                    ) + "PM"


            }else{

                String.format("%02d",picker.hour) + " : " + String.format(
                    "%02d",
                    picker.minute
                ) + "AM"

            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0


        }

    }

    private fun  btnDisabled(v : TextView){
        v.isEnabled = false
        v.setTextColor(Color.parseColor("#5D1BAACA"))
    }
    private fun  btnEnabled(v : TextView){
        v.isEnabled = true
        v.setTextColor(Color.parseColor("#1BAACA"))
    }

    private fun viewEnabled(v: AutoCompleteTextView) {
        v.isEnabled = true
        v.isPressed = true
        v. isFocusable = true
        v.isFocusableInTouchMode = true
        v.inputType = EditorInfo.TYPE_CLASS_TEXT


    }

    private fun viewDisabled(v: AutoCompleteTextView) {
        v.isEnabled = false
        v.isPressed = false
        v.isFocusable = false
        v.isFocusableInTouchMode = false
        v.inputType = EditorInfo.TYPE_NULL

    }




    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var lecture: String? = lectureEditText.text.toString()
            var day: String? = dayTextInputEditText.text.toString()
            var starttime: String? = starttimeTextView.text.toString()
            var endtime: String? = endtimeTextView.text.toString()
            if (lecture != "" && day != "Select day" && starttime != "" && endtime != "") {
                btnEnabled(saveTextview)
            } else {
                btnDisabled(saveTextview)
            }
        }
    }


}