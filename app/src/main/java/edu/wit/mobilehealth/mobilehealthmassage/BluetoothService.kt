package edu.wit.mobilehealth.mobilehealthmassage

import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Log
import java.nio.charset.Charset
import java.util.*

internal class BluetoothService(private val activityAlerter: IBluetoothAlerts) {

    // UUIDs for UART service and associated characteristics.
    val UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    val TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    val RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
    private val ourDevice = "Ard_t1"

    // UUID for the BTLE client characteristic which is necessary for notifications.
    val CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // BTLE state
    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var arduinoController: ArduinoController? = null

    // Main BTLE device callback where much of the logic occurs.
    private val callback = object : BluetoothGattCallback() {

        // Called whenever the device connection state changes, i.e. from disconnected to connected.
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                writeLine("Connected!")
                // Discover services.
                if (!gatt.discoverServices()) {
                    writeLine("Failed to start discovering services!")
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                found = false
                activityAlerter.onDeviceDisconnected()
                writeLine("Disconnected!")
            } else {
                writeLine("Connection state changed.  New state: $newState")
            }
        }

        // Called when services have been discovered on the remote device.
        // It seems to be necessary to wait for this discovery to occur before
        // manipulating any services or characteristics.
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeLine("Service discovery completed!")
            } else {
                writeLine("Service discovery failed with status: $status")
            }

            // Save reference to each characteristic.
            val transmitter = gatt.getService(UART_UUID).getCharacteristic(TX_UUID)
            val receiver = gatt.getService(UART_UUID).getCharacteristic(RX_UUID)

            Log.i("BLE", "tx:$transmitter")
            Log.i("BLE", "rx:" + receiver.getDescriptor(CLIENT_UUID))

            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(receiver, true)) {
                writeLine("Couldn't set notifications for RX characteristic!")
            }

            // Next update the RX characteristic's client descriptor to enable notifications.
            if (receiver.getDescriptor(CLIENT_UUID) != null) {
                val desc = receiver.getDescriptor(CLIENT_UUID)
                desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                if (!gatt.writeDescriptor(desc)) {
                    writeLine("Couldn't write RX client descriptor value!")
                }
            } else {
                writeLine("Couldn't get RX client descriptor!")
            }

            arduinoController = ArduinoController(transmitter,gatt)
            activityAlerter.onDeviceConnected(arduinoController!!)

        }

        // Called when a remote characteristic changes (like the RX characteristic).
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)

            if (characteristic.uuid == RX_UUID) {
                activityAlerter.plotValue(characteristic.getStringValue(0))
            }
        }
    }

    var found = false
    // BTLE device scanning callback.
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (!found) {
                writeLine("Found device: " + result.device.name)
                adapter.bluetoothLeScanner.stopScan(this)
                result.device.connectGatt(activityAlerter.getContext(), true, callback)
                writeLine("Connected to device: " + result.device.address)
                found = true
            }
        }
    }

    private fun scanForDevice() {
        val scanFilter = ScanFilter.Builder().setDeviceName(ourDevice).setServiceUuid(ParcelUuid(UART_UUID)).build()
        adapter.bluetoothLeScanner.startScan(listOf(scanFilter), ScanSettings.Builder().setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT).build(), scanCallback)
    }

    fun disconnect() {
        arduinoController?.gatt?.disconnect()
        arduinoController = null
    }

    companion object {
        @JvmStatic
        fun newInstance(activityAlerter: IBluetoothAlerts): BluetoothService {
            val bluetoothService = BluetoothService(activityAlerter)
            bluetoothService.scanForDevice()
            return bluetoothService
        }
    }

}

class ArduinoController(private val tx: BluetoothGattCharacteristic,
                        val gatt: BluetoothGatt){

    fun turnOnPin(pinNum: Int) {
        sendMessage("/digital/$pinNum/1/")
    }

    fun turnOffPin(pinNum: Int) {
        sendMessage("/digital/$pinNum/0/")
    }

    fun scanA0() {
        sendMessage("/analog/0/r/")
    }

    private fun sendMessage(message: String) {

        tx.value = message.toByteArray(Charset.forName("UTF-8"))
        if (gatt.writeCharacteristic(tx)) {
            writeLine("Sent: $message")
        } else {
            writeLine("Couldn't write TX characteristic!")
        }
    }

}

private const val TAG = "BluetoothService"
private fun writeLine(message: String) {
    Log.i(TAG, message)
}