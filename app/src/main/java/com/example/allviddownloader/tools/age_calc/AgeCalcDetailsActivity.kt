package com.example.allviddownloader.tools.age_calc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityAgeCalcDetailsBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import java.text.SimpleDateFormat
import java.util.*

class AgeCalcDetailsActivity : BaseActivity() {
    val binding by lazy { ActivityAgeCalcDetailsBinding.inflate(layoutInflater) }

    var years: String = ""
    var months: String = ""
    var days: String = ""

    var birthDay: String = ""
    var birthMonth: String = ""
    var birthYear: String = ""

    var nextMonths: String = ""
    var nextDays: String = ""

    var totalSeconds: String = ""
    var totalMinutes: String = ""
    var totalHours: String = ""
    var totalDays: String = ""
    var totalWeeks: String = ""
    var totalMonths: String = ""
    var totalYears: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@AgeCalcDetailsActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNativeSmall(
                    this@AgeCalcDetailsActivity, getString(R.string.admob_native_id),
                    adFrame
                )
            }

            years = intent.getStringExtra("years").toString()
            months = intent.getStringExtra("months").toString()
            days = intent.getStringExtra("days").toString()

            birthDay = intent.extras?.getString("birthDay").toString()
            birthMonth = intent.extras?.getString("birthMonth").toString()
            birthYear = intent.extras?.getString("birthYear").toString()

            nextMonths = intent.extras?.getString("nextMonths").toString()
            nextDays = intent.extras?.getString("nextDays").toString()

            totalSeconds = intent.getStringExtra("totalSeconds").toString()
            totalMinutes = intent.getStringExtra("totalMinutes").toString()
            totalHours = intent.getStringExtra("totalHours").toString()
            totalDays = intent.getStringExtra("totalDays").toString()
            totalWeeks = intent.getStringExtra("totalWeeks").toString()
            totalMonths = intent.getStringExtra("totalMonths").toString()
            totalYears = intent.getStringExtra("totalYears").toString()

            txtYearsVal.text = years
            txtMonthsVal.text = months
            txtDaysVal.text = days

            txtMonthsValNext.text = nextMonths
            txtDaysValNext.text = nextDays

            txtYearsValTotal.text = totalYears
            txtMonthsValTotal.text = totalMonths
            txtWeeksValTotal.text = totalWeeks
            txtDaysValTotal.text = totalDays
            txtHoursValTotal.text = totalHours
            txtMinutesValTotal.text = totalMinutes
            txtSecondsValTotal.text = totalSeconds

            layoutUpcomingBDs.run {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, birthDay.toInt())
                cal.set(Calendar.MONTH, birthMonth.toInt())
                cal.set(Calendar.YEAR, cal[Calendar.YEAR] + 1)
                cal.set(Calendar.SECOND, 0)
                var dateVal = Date(cal.timeInMillis)
                var dayStr = SimpleDateFormat("EEEE", Locale.ENGLISH).format(dateVal)
                var dateStr = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(dateVal)

                val datesArr: Array<String?> = arrayOfNulls(10)
                val daysArr: Array<String?> = arrayOfNulls(10)

                for (i in 0..9) {
                    datesArr[i] = dateStr
                    daysArr[i] = dayStr

                    cal.set(Calendar.YEAR, cal[Calendar.YEAR] + 1)
                    dateVal = Date(cal.timeInMillis)
                    dayStr = SimpleDateFormat("EEEE", Locale.ENGLISH).format(dateVal)
                    dateStr = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(dateVal)
                }

                txtDate1.text = datesArr[0]
                txtDay1.text = daysArr[0]
                txtDate2.text = datesArr[1]
                txtDay2.text = daysArr[1]
                txtDate3.text = datesArr[2]
                txtDay3.text = daysArr[2]
                txtDate4.text = datesArr[3]
                txtDay4.text = daysArr[3]
                txtDate5.text = datesArr[4]
                txtDay5.text = daysArr[4]
                txtDate6.text = datesArr[5]
                txtDay6.text = daysArr[5]
                txtDate7.text = datesArr[6]
                txtDay7.text = daysArr[6]
                txtDate8.text = datesArr[7]
                txtDay8.text = daysArr[7]
                txtDate9.text = datesArr[8]
                txtDay9.text = daysArr[8]
                txtDate10.text = datesArr[9]
                txtDay10.text = daysArr[9]
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}