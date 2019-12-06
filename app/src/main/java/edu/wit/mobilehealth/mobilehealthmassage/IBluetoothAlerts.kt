package edu.wit.mobilehealth.mobilehealthmassage

import android.content.Context

interface IBluetoothAlerts {

    fun onDeviceConnected(arduino: ArduinoController)

    fun onDeviceDisconnected()

    fun plotValue(value: String)

    fun getContext(): Context
}