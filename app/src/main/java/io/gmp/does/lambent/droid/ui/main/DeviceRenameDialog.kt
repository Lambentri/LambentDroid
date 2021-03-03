package io.gmp.does.lambent.droid.ui.main

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.gmp.does.lambent.droid.R

class DeviceRenameDialog : DialogFragment() {

    fun onCreateDialog(savedInstanceState: Bundle): AlertDialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_title_device_rename)
                .setPositiveButton(
                    R.string.app_submit,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
                .setNegativeButton(
                    R.string.app_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}