package com.naman.projectbluetoothapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.naman.projectbluetoothapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    lateinit var progress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        fAuth = FirebaseAuth.getInstance()
        progress = ProgressDialog(this)
        binding.btnLogin.setOnClickListener {
            if (binding.txtEmail.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter your Email",Toast.LENGTH_SHORT).show()
            }
            if (binding.txtPassword.text.isNullOrEmpty()){
                Toast.makeText(this,"Enter your Email",Toast.LENGTH_SHORT).show()
            }
            progress.setTitle("Signing In")
            progress.setCanceledOnTouchOutside(false)
            progress.show()
            val email = binding.txtEmail.text.toString()
            val password = binding.txtPassword.text.toString()

            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                Toast.makeText(this,"Signed In successfully",Toast.LENGTH_SHORT).show()
                progress.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                progress.dismiss()
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
            }

        }
    }
}