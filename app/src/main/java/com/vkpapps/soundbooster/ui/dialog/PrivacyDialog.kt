package com.vkpapps.soundbooster.ui.dialog

import android.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vkpapps.soundbooster.R
import com.vkpapps.soundbooster.utils.KeyValue

class PrivacyDialog(private val activity: AppCompatActivity) {
    val isPolicyAccepted: Boolean
        get() {
            if (KeyValue(activity).policy) {
                return true
            } else {
                promptUser()
            }
            return false
        }

    private fun promptUser() {
        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.dialog_accept_policy, null, false)
        builder.setView(view)
        builder.setCancelable(false)
        val create = builder.create()
        create.show()
        view.findViewById<TextView>(R.id.message).movementMethod = LinkMovementMethod.getInstance()
        view.findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            activity.finish()
        }
        view.findViewById<TextView>(R.id.btnAgree).setOnClickListener {
            KeyValue(activity).policy = true
            create.hide()
            create.dismiss()
        }
    }

}