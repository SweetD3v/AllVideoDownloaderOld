package com.tools.videodownloader.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivitySettingsBinding
import com.tools.videodownloader.databinding.RatingDialogBinding


class SettingsActivity : AppCompatActivity() {

    val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    var rating = 0f
    var isShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }

            llShareApp.setOnClickListener {
                openShareSheet()
            }

            llRateUs.setOnClickListener {
                showRatingDialog()
            }
        }
    }

    private fun openShareSheet() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val shareText = resources.getString(
            R.string.share_app_desc,
            packageName
        )
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(shareIntent, "Share App"))
    }

    private fun showRatingDialog() {
        val ratingDialogBinding = RatingDialogBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setView(ratingDialogBinding.root)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
        ratingDialogBinding.rateText.text =
            String.format(resources.getString(R.string.rating_desc), getString(R.string.app_name))
        ratingDialogBinding.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _: RatingBar?, rating: Float, _: Boolean ->
                ratingDialogBinding.smileyView.setSmiley(if (rating <= 4.5) rating else rating - 1)
                this.rating = rating
            }
        ratingDialogBinding.tvSubmit.setOnClickListener { v ->
            if (rating >= 4) {
                alertDialog.dismiss()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } else if (rating > 0) {
                alertDialog.dismiss()
                val feedbackDialogBuilder = AlertDialog.Builder(this, R.style.MyProgressDialog)
                    .setMessage("Thank you for your valuable feedback.")
                    .setPositiveButton(
                        "Ok"
                    ) { dialog, _ -> dialog?.dismiss() }
                    .setCancelable(false)

                val feedbackDialog = feedbackDialogBuilder.create()
                feedbackDialog.show()

                feedbackDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.primary))
            } else {
                Toast.makeText(this@SettingsActivity, "Ratings can't be empty!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        ratingDialogBinding.tvLater.setOnClickListener { v ->
            if (isShowing) {
                alertDialog.dismiss()
            } else {
                alertDialog.dismiss()
            }
        }
    }

}