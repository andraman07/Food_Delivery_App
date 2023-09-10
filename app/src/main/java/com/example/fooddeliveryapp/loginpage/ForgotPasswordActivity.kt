package com.example.fooddeliveryapp.loginpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fooddeliveryapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailField:EditText
    private lateinit var sendLnkButton:Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

         emailField=findViewById(R.id.registered_email)
         sendLnkButton=findViewById(R.id.send_lnk_btn)
         auth = Firebase.auth



        sendLnkButton.setOnClickListener {


             if(emailField.text.isEmpty()) emailField.error="Enter your email"
             else{
                 forgotPassword()
             }


         }
    }

    private fun forgotPassword() {
        auth.sendPasswordResetEmail(emailField.text.toString())
            .addOnCompleteListener {task->

                if(task.isSuccessful){
                    Toast.makeText(this,"Password Reset link has been sent to your mail", Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    Toast.makeText(this,"Error"+ (task.exception?.message), Toast.LENGTH_SHORT).show()
                }

            }
    }


}