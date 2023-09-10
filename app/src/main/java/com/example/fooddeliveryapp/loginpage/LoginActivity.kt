package com.example.fooddeliveryapp.loginpage

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ActivityLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        private const val RC_SIGN_IN=100
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding=ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignupActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            emailPasswordSignIn()
        }

        binding.googleBtn.setOnClickListener {
            googleSignIn()
        }

        binding.forgotBtn.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }

    }

    private fun emailPasswordSignIn() {
        val email: String = binding.loginEmailField.text.toString()
        val password: String = binding.passwordField.text.toString()

        if(!checkInternetConnection()){
            Toast.makeText(this, "Check your connection and try again", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()){
            binding.loginEmailField.error = "enter your email"
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loginProgress.visibility=View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    if(firebaseAuth.currentUser?.isEmailVerified == true){
                        Toast.makeText(this,"signed in successfully",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else{
                        Toast.makeText(this,"email is not verified",Toast.LENGTH_SHORT).show()
                    }


                } else {
                    when(task.exception){
                        is FirebaseNetworkException -> Toast.makeText(this, "Check your network connection ", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidUserException -> Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        is FirebaseTooManyRequestsException ->   Toast.makeText(this, "Too many attempts, please try again later", Toast.LENGTH_SHORT).show()
                        else ->  Toast.makeText(this, "Sign in failed,try again", Toast.LENGTH_SHORT).show()
                    }

                }

                binding.loginProgress.visibility=View.GONE

            }

    }

    private fun googleSignIn() {

        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        binding.loginProgress.visibility=View.VISIBLE
        val intent:Intent=googleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)

    }

    private fun checkInternetConnection():Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check for network capabilities for active network
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Check if the network has internet connectivity
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.loginProgress.visibility=View.GONE

        if(requestCode==RC_SIGN_IN){

            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account.idToken)

            }catch (e:ApiException){

                Log.e("LoginActivity", "Google sign-in failed", e)

            }

        }



    }

    private fun firebaseAuthWithGoogleAccount(idToken:String?){
        val credential:AuthCredential =GoogleAuthProvider.getCredential(idToken,null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                   // val user = auth.currentUser

                Log.d("LoginActivity", "User signed in successfully")

            }else{
                    Log.e("LoginActivity", "Authentication failed", task.exception)
                }
            }

                }


    }


