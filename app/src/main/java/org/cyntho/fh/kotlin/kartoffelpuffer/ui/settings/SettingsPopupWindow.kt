package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import org.cyntho.fh.kotlin.kartoffelpuffer.R

class SettingsPopupWindow : AppCompatActivity() {

    private var popupTitle: String = ""
    private var popupText: String = ""
    private var popupButtonText = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_settings_popup_window)

        popupTitle = intent.extras?.getString("popup_title", "Title")?:""
        popupText = intent.extras?.getString("popup_text", "Text")?:""
        popupButtonText = intent.extras?.getString("popup_button_text", "Button")?:""



    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}