package com.example.fooddeliveryapp.loginpage

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.User
import com.example.fooddeliveryapp.databinding.ActivitySignUpScreenBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.signupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        databaseRef = Firebase.database.reference
        firebaseAuth = Firebase.auth

        binding.signupToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.signupBtn.setOnClickListener {
                signUp()
            }

    }

    private fun signUp(){

        val name: String = binding.signupName.text.toString()
        val email:String=binding.signupEmail.text.toString()
        val password:String=binding.signupPassword.text.toString()
        val confirmPassword:String=binding.signupConfirmPassword.text.toString()

        when{

            name.isEmpty()->binding.signupName.error = "Enter your name here"
            !isNameValid(name)-> binding.signupName.error = "Enter a valid name"
            email.isEmpty()->binding.signupEmail.error="Enter your email"
            !isPasswordValid(password)-> passwordRequirementsDialog()
            confirmPassword != password-> Toast.makeText(this,"password do not match",Toast.LENGTH_SHORT).show()



            else-> {

                binding.signupProgress.visibility=View.VISIBLE

                firebaseAuth.createUserWithEmailAndPassword(email, confirmPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener {

                                    if(it.isSuccessful){
                                        Toast.makeText(this,"A verification link has been sent to your email",Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this,"Error:" + it.exception?.message,Toast.LENGTH_SHORT).show()
                                    }
                                }

                            val key=databaseRef.push().key
                            writeNewUser(key.toString(),name, email)

                            finish()

                        } else {

                            when(task.exception){

                                is FirebaseAuthUserCollisionException -> Toast.makeText(this,"User with this email already exist",Toast.LENGTH_SHORT).show()
                                is FirebaseAuthInvalidCredentialsException ->Toast.makeText(this,"Check your Email",Toast.LENGTH_SHORT).show()
                                is FirebaseNetworkException->Toast.makeText(this,"Check your Connection",Toast.LENGTH_SHORT).show()
                                else-> {
                                    Toast.makeText(this,"failed to signup, try again",Toast.LENGTH_SHORT).show()
                                }

                            }

                        }
                        binding.signupProgress.visibility=View.GONE

                    }

            }

        }
    }

    private fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name, email)

            databaseRef.child("users").child(userId).setValue(user)

    }

    private fun passwordRequirementsDialog() {
           AlertDialog.Builder(this)
               .setTitle(R.string.password_dialog_title)
               .setMessage(getString(R.string.password_requirements))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
             .create()
               .show()

    }

    private fun isNameValid(name: String): Boolean {
        val pattern = Regex("^[a-zA-Z][a-zA-Z\\s]{2,19}\$")
        return pattern.matches(name)
    }

    private fun isPasswordValid(password: String): Boolean {
        val regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{4,}$".toRegex()
        return password.matches(regexPattern)
    }

}