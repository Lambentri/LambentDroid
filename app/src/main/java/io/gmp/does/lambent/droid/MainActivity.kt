package io.gmp.does.lambent.droid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.gmp.does.lambent.droid.ui.main.MainFragment

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
}