package com.naman.projectbluetoothapp

import android.Manifest
import android.R
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.naman.projectbluetoothapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    var bAdapter: BluetoothAdapter? = null
    lateinit var bPairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BT = 101

    companion object{
        val EXTRA_ADDRESS: String = "Device_address"
    }
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bAdapter == null){
            Toast.makeText(this,"This device does not support Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }


        if (!bAdapter!!.isEnabled){

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                val enableintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableintent,REQUEST_ENABLE_BT)
                return
            }
            val enableintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableintent,REQUEST_ENABLE_BT)

        }
        binding.btnRefresh.setOnClickListener {
            pairedDeviceList()
        }

    }
    private fun pairedDeviceList(){


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val enableintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableintent,REQUEST_ENABLE_BT)

        }
        bPairedDevices = bAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (bPairedDevices.isNotEmpty()){
            for (device:BluetoothDevice in bPairedDevices){
                list.add(device)
                Log.e("Device:",""+device)
            }
        }else Toast.makeText(this,"No paired Devices found",Toast.LENGTH_SHORT).show()

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1,list)
        binding.lvBluetoothDevices.adapter = adapter
        binding.lvBluetoothDevices.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this,AppActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                if (bAdapter!!.isEnabled){
                    Toast.makeText(this,"Bluetooth is enabled",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Bluetooth has been disabled",Toast.LENGTH_SHORT).show()

                }
            }else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this,"Bluetooth has been cancelled",Toast.LENGTH_SHORT).show()

            }
        }
    }
}