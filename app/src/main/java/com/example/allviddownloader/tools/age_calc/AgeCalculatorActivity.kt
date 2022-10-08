package com.example.allviddownloader.tools.age_calc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityAgeCalculatorBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.toInt
import com.example.allviddownloader.utils.toastShort
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class AgeCalculatorActivity : BaseActivity() {
    val binding by lazy { ActivityAgeCalculatorBinding.inflate(layoutInflater) }

    var todayTimeMillis: Long = 0L
    var birthTimeMillis: Long = 0L

    var day1 = 0
    var month1: Int = 0
    var year1: Int = 0
    var day2: Int = 0
    var month2: Int = 0
    var year2: Int = 0
    var day3: Int = 0
    var month3: Int = 0
    var year3: Int = 0
    var fbdyear1: Int = 0
    var fbdmonth1: Int = 0
    var fbdday1: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@AgeCalculatorActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )
                AdsUtils.loadNative(
                    this@AgeCalculatorActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            val cal = Calendar.getInstance()

            edtDay2.setText("${cal.get(Calendar.DAY_OF_MONTH)}")
            edtMonth2.setText("${cal.get(Calendar.MONTH) + 1}")
            edtYear2.setText("${cal.get(Calendar.YEAR)}")

            day1 = cal.get(Calendar.DAY_OF_MONTH)
            month1 = cal.get(Calendar.MONTH)
            year1 = cal.get(Calendar.YEAR)

            day2 = cal.get(Calendar.DAY_OF_MONTH)
            month2 = cal.get(Calendar.MONTH)
            year2 = cal.get(Calendar.YEAR)

            fbdday1 = cal.get(Calendar.DAY_OF_MONTH)
            fbdmonth1 = cal.get(Calendar.MONTH)
            fbdyear1 = cal.get(Calendar.YEAR)

            imgCalendar1.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.addOnPositiveButtonClickListener {
                    val cal1 = Calendar.getInstance()
                    cal1.timeInMillis = it
                    todayTimeMillis = it

                    edtDay1.setText("${cal1.get(Calendar.DAY_OF_MONTH)}")
                    edtMonth1.setText("${cal1.get(Calendar.MONTH) + 1}")
                    edtYear1.setText("${cal1.get(Calendar.YEAR)}")

                    year1 = cal1.get(Calendar.YEAR)
                    month1 = cal1.get(Calendar.MONTH) + 1
                    day1 = cal1.get(Calendar.DAY_OF_MONTH)
                }
                datePicker.show(supportFragmentManager, "")
            }

            imgCalendar2.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.addOnPositiveButtonClickListener {
                    val cal2 = Calendar.getInstance()
                    cal2.timeInMillis = it
                    birthTimeMillis = it

                    edtDay2.setText("${cal2.get(Calendar.DAY_OF_MONTH)}")
                    edtMonth2.setText("${cal2.get(Calendar.MONTH) + 1}")
                    edtYear2.setText("${cal2.get(Calendar.YEAR)}")

                    year2 = cal2.get(Calendar.YEAR)
                    month2 = cal2.get(Calendar.MONTH) + 1
                    day2 = cal2.get(Calendar.DAY_OF_MONTH)
                }
                datePicker.show(supportFragmentManager, "")
            }

            btnCalculate.setOnClickListener {
                try {
                    calculateAge()
                } catch (e: Exception) {
                    toastShort(this@AgeCalculatorActivity, "Please enter the Birth date")
                }
            }

//            configOtpEditText(
//                edtDay,
//                edtMonth,
//                edtYear
//            )
        }
    }

    private fun calculateAge() {
        binding.run {
            val calBirth = Calendar.getInstance()
            calBirth.set(Calendar.DAY_OF_MONTH, edtDay1.text.toInt())
            calBirth.set(Calendar.MONTH, edtMonth1.text.toInt())
            calBirth.set(Calendar.YEAR, edtYear1.text.toInt())
            calBirth.set(Calendar.SECOND, 0)
            calBirth.set(Calendar.MILLISECOND, 0)
            birthTimeMillis = calBirth.timeInMillis

            val calToday = Calendar.getInstance()
            calToday.set(Calendar.DAY_OF_MONTH, edtDay2.text.toInt())
            calToday.set(Calendar.MONTH, edtMonth2.text.toInt())
            calToday.set(Calendar.YEAR, edtYear2.text.toInt())
            calToday.set(Calendar.SECOND, 0)
            calToday.set(Calendar.MILLISECOND, 0)
            todayTimeMillis = calToday.timeInMillis

            val millis: Long = todayTimeMillis - birthTimeMillis

            val totalDays: Long = (millis / 86400000)
            val totalWeeks = totalDays / 7
            val totalMonths: Int = (totalDays / 30.5).toInt()
            val totalYears: Int = (totalDays / 365).toInt()
            val totalHours = totalDays * 24
            val totalMinutes = totalHours * 60
            val totalSeconds = totalMinutes * 60


            Log.e("TAG", "totalSeconds: $totalSeconds")
            Log.e("TAG", "totalMinutes: $totalMinutes")
            Log.e("TAG", "totalHours: $totalHours")
            Log.e("TAG", "totalDays: $totalDays")
            Log.e("TAG", "totalWeeks: $totalWeeks")
            Log.e("TAG", "totalMonths: ${totalMonths + 1}")
            Log.e("TAG", "totalYears: $totalYears")
//
//                year3 = year2 - year1
//
//                val Bdate1 = day1
//                val Bmonth1 = month1
//                val Byear1 = year2
//
//                if (month2 >= month1) {
//                    month3 = month2 - month1
//                } else {
//                    month3 = month2 - month1
//                    month3 += 12
//                    year3--
//                }
//
//                if (day2 >= day1) {
//                    day3 = day2 - day1
//                } else {
//                    day3 = day2 - day1
//                    day3 += 30
//                    month3--
//                }

            var currentDay: Int = Integer.valueOf(edtDay2.text.toString())
            var currentMonth: Int =
                Integer.valueOf(edtMonth2.text.toString())
            var currentYear: Int = Integer.valueOf(edtYear2.text.toString())
            val now = Date(todayTimeMillis)
            val birthDay: Int = Integer.valueOf(edtDay1.text.toString())
            val birthMonth: Int = Integer.valueOf(edtMonth1.text.toString())
            val birthYear: Int = Integer.valueOf(edtYear1.text.toString())
            val dob = Date(birthTimeMillis)
            if (dob.after(now)) {
                Toast.makeText(
                    this@AgeCalculatorActivity,
                    "Birthday can't in future",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            // days of every month
            val month = intArrayOf(
                31, 28, 31, 30, 31, 30, 31,
                31, 30, 31, 30, 31
            )

            // if birth date is greater then current birth
            // month then do not count this month and add 30
            // to the date so as to subtract the date and
            // get the remaining days
            if (birthDay > currentDay) {
                currentDay += month[birthMonth - 1]
                currentMonth -= 1
            }

            // if birth month exceeds current month, then do
            // not count this year and add 12 to the month so
            // that we can subtract and find out the difference
            if (birthMonth > currentMonth) {
                currentYear -= 1
                currentMonth += 12
            }

            // calculate date, month, year
            val calculated_date = currentDay - birthDay
            val calculated_month = currentMonth - birthMonth
            val calculated_year = currentYear - birthYear

            val difference: Long = birthTimeMillis - todayTimeMillis - (3600 * 24 * 1000)

            val calNext = Calendar.getInstance()
            calNext.timeInMillis = difference

            val nextMonths = calNext[Calendar.MONTH].toString()
            val nextDays = (calNext[Calendar.DAY_OF_MONTH]).toString()

            AdsUtils.loadInterstitialAd(this@AgeCalculatorActivity,
                getString(R.string.interstitial_id),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        startActivity(
                            Intent(
                                this@AgeCalculatorActivity,
                                AgeCalcDetailsActivity::class.java
                            )
                                .putExtra("birthDay", birthDay.toString())
                                .putExtra("birthMonth", (birthMonth - 1).toString())
                                .putExtra("birthYear", birthYear.toString())
                                .putExtra("days", calculated_date.toString())
                                .putExtra("months", calculated_month.toString())
                                .putExtra("years", calculated_year.toString())
                                .putExtra("nextMonths", nextMonths)
                                .putExtra("nextDays", nextDays)
                                .putExtra("totalSeconds", totalSeconds.toString())
                                .putExtra("totalMinutes", totalMinutes.toString())
                                .putExtra("totalHours", totalHours.toString())
                                .putExtra("totalDays", totalDays.toString())
                                .putExtra("totalWeeks", totalWeeks.toString())
                                .putExtra("totalMonths", totalMonths.toString())
                                .putExtra("totalYears", totalYears.toString())
                        )
                    }
                })
        }
    }

//    fun configOtpEditText(vararg etList: EditText) {
//        val afterTextChanged = { index: Int, e: Editable? ->
//            val view = etList[index]
//            val text = e.toString()
//
//            when (view.id) {
//                // first text changed
//                etList[0].id -> {
//                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
//                }
//
//                // las text changed
//                etList[etList.size - 1].id -> {
//                    if (text.isEmpty()) etList[index - 1].requestFocus()
//                }
//
//                // middle text changes
//                else -> {
//                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
//                    else etList[index - 1].requestFocus()
//                }
//            }
//            false
//        }
//        etList.forEachIndexed { index, editText ->
//            editText.doAfterTextChanged { afterTextChanged(index, it) }
//        }
//    }

//    class GenericKeyEvent internal constructor(
//        private val currentView: EditText,
//        private val previousView: EditText?
//    ) : View.OnKeyListener {
//        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
//            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.edtDay && currentView.text.isEmpty()) {
//                //If current is empty then previous EditText's number will also be deleted
//                previousView!!.text = null
//                previousView.requestFocus()
//                return true
//            }
//            return false
//        }
//
//
//    }
//
//    class GenericTextWatcher internal constructor(
//        private val currentView: View,
//        private val nextView: View?
//    ) : TextWatcher {
//        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
//            val text = editable.toString()
//            when (currentView.id) {
//                R.id.edtDay -> if (text.length == 2) nextView!!.requestFocus()
//                R.id.edtMonth -> if (text.length == 2) nextView!!.requestFocus()
//                R.id.edtYear -> if (text.length == 4) nextView!!.requestFocus()
//                //You can use EditText4 same as above to hide the keyboard
//            }
//        }
//
//        override fun beforeTextChanged(
//            arg0: CharSequence,
//            arg1: Int,
//            arg2: Int,
//            arg3: Int
//        ) {
//        }
//
//        override fun onTextChanged(
//            arg0: CharSequence,
//            arg1: Int,
//            arg2: Int,
//            arg3: Int
//        ) {
//        }
//
//    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}