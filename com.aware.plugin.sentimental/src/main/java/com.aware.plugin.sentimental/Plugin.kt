package com.aware.plugin.sentimental

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import com.aware.Applications
import com.aware.Aware
import com.aware.Aware_Preferences
import com.aware.providers.Applications_Provider
import com.aware.providers.Keyboard_Provider
import com.aware.utils.Aware_Plugin

class Plugin : Aware_Plugin() {

    companion object {
        val PACKAGE_NAME = "packageName"
        val TYPED_TEXT = "typedText"
    }

    var currentForeground = ""
    var textBuffer = ""

    override fun onCreate() {
        super.onCreate()

        TAG = "AWARE: Sentimental"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (PERMISSIONS_OK) {

            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true")
            Aware.setSetting(this, Settings.STATUS_PLUGIN_SENTIMENTAL, true)

            if (Applications.isAccessibilityServiceActive(this)) {

                Aware.startKeyboard(this)
                Applications.setSensorObserver(object : Applications.AWARESensorObserver {
                    override fun onCrash(data: ContentValues?) {}
                    override fun onNotification(data: ContentValues?) {}
                    override fun onBackground(data: ContentValues?) {}
                    override fun onKeyboard(data: ContentValues?) {
                        val keyboardInPackage = data!!.getAsString(Keyboard_Provider.Keyboard_Data.PACKAGE_NAME)
                        if (keyboardInPackage == currentForeground) {
                            textBuffer = data.getAsString(Keyboard_Provider.Keyboard_Data.CURRENT_TEXT)
                        }
                    }

                    override fun onTouch(data: ContentValues?) {}
                    override fun onForeground(data: ContentValues?) {
                        val packagesOfInterest = Aware.getSetting(applicationContext, Settings.PLUGIN_SENTIMENTAL_PACKAGES).split(",")
                        val currentApp = data!!.getAsString(Applications_Provider.Applications_Foreground.PACKAGE_NAME)
                        if (packagesOfInterest.count() > 0 && packagesOfInterest.contains(currentApp)) {
                            currentForeground = currentApp
                        } else {
                            if (!textBuffer.isEmpty() && awareSensor != null) {

                                val contentValues = ContentValues()
                                contentValues.put(Plugin.PACKAGE_NAME, currentForeground)
                                contentValues.put(Plugin.TYPED_TEXT, textBuffer)

                                (awareSensor as AWARESensorObserver).onTextContextChanged(contentValues)

                                if (DEBUG) Log.d("Sentimental", "onTextContextChanged -> ${contentValues.toString()}")
                            }
                            textBuffer = ""
                            currentForeground = ""
                        }
                    }
                })
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Aware.setSetting(this, Settings.STATUS_PLUGIN_SENTIMENTAL, false)
        Aware.stopKeyboard(this)
    }

    private var awareSensor: AWARESensorObserver? = null

    fun setSensorObserver(observer: AWARESensorObserver) {
        awareSensor = observer
    }

    fun getSensorObserver(): AWARESensorObserver {
        return awareSensor!!
    }

    interface AWARESensorObserver {
        fun onTextContextChanged(data: ContentValues)
    }
}