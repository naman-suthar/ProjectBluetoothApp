package com.naman.projectbluetoothapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naman.projectbluetoothapp.databinding.ActivityAppBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.util.*


class AppActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppBinding
    var responseString = " "
    companion object{
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bSocket: BluetoothSocket? = null
        lateinit var bAdapter: BluetoothAdapter
        lateinit var progress: ProgressDialog
        var isConnected: Boolean = false
        lateinit var address: String
        val myViewModel = MyAppViewModel()


    }
    init {
        lifecycleScope.launch{
            myViewModel.stringFlow.collect{
                Log.e("Receiving",it)
            }
    }}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        supportActionBar?.title ="Fuel Indicator"
        address = intent?.getStringExtra(MainActivity.EXTRA_ADDRESS)!!
        ConnectToDevice(this).execute()
        binding.btnrefreshData.setOnClickListener {
            myViewModel.getStringData(bSocket!!)

            val newValue = myViewModel.stringFlow.value.trim()
            val strInt = newValue.split("\n")[0].trim().toInt()
            binding.txtFuelData.text = strInt.toString()
            if (strInt < 100){

        }
            if (strInt >100){

            }


        }


    }
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){
        var buffer: ByteArray = ByteArray(1024)
        var bytes: Int = 0

        private var connected: Boolean = true
        private val context: Context
        init {
            this.context =c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressDialog(context)
            progress.setTitle("Connecting..")
            progress.show()
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (bSocket == null || !isConnected){
                    bAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bAdapter.getRemoteDevice(address)


                    bSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    bSocket!!.connect()



                }
            }catch (e: IOException){
                connected = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connected){
                Log.e("Data:","Couldnt Connect")
            }else{
                isConnected = true
            }
            myViewModel.getStringData(bSocket!!)
            progress.dismiss()


        }

    }
}

class MyAppViewModel : ViewModel(){
    private val _stringFlow = MutableStateFlow("0")
    val stringFlow = _stringFlow.asStateFlow()
    fun getStringData(socket: BluetoothSocket){
        var tempIn : InputStream
        val buffer: ByteArray = ByteArray(1024)
        var bytes: Int

        try {
            tempIn = socket.inputStream
            bytes = tempIn.read(buffer)
            val receivedStr: String = String(buffer,0,bytes)
            _stringFlow.value = receivedStr


        }catch (e: IOException){
            Log.e("IOERROR:",e.message.toString())
        }

    }

}