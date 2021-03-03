package io.gmp.does.lambent.droid

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.gmp.does.lambent.droid.ui.main.MainFragment
import io.gmp.does.lambent.droid.ui.main.MainViewModel

inline fun log(lambda: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d("LOGGY", lambda())
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    fun showRenameDialog(device: Device, viewModel: MainViewModel) {
        Toast.makeText(this, "Rename Clicked", Toast.LENGTH_SHORT).show()
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_device_rename, null)
        builder.setMessage(device.iname)
            .setTitle(R.string.dialog_title_device_rename)
        builder.apply {
            setView(dialogView)
            setPositiveButton(R.string.app_submit,
                DialogInterface.OnClickListener { dialog, id ->
                    val text: EditText = dialogView.findViewById(R.id.device_name)!!
                    viewModel.device_rename(device.iname, text.text.toString())
                })
            setNegativeButton(R.string.app_cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        }

        val edit_text_for_name: EditText = dialogView.findViewById(R.id.device_name)!!
        if (device.name != null) {
            edit_text_for_name.setText(device.name)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }
}