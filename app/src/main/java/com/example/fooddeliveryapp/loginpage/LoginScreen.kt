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


class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth //The entry point of the Firebase Authentication SDK
    private lateinit var googleSignInClient: GoogleSignInClient  //A client for interacting with the Google Sign In API

    companion object{
        private const val RC_SIGN_IN=100
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@LoginScreen,SignUpScreen::class.java))
        } //navigating from login screen to sign up

        binding.fbBtn.setOnClickListener {
            Toast.makeText(this,"Coming Soon...",Toast.LENGTH_SHORT).show()
        }

        binding.loginBtn.setOnClickListener {

            val email: String = binding.loginEmailField.text.toString()
            val password: String = binding.loginPasswordField.text.toString()

            if(!checkInternetConnection()){

                Toast.makeText(this, "Check your connection and try again", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            if (email.isEmpty()) {
                binding.loginEmailField.error = "enter your email"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.loginProgress.visibility=View.VISIBLE


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(this,"signing up ",Toast.LENGTH_SHORT).show()

                    } else {

                        when(task.exception){

                            is FirebaseNetworkException -> Toast.makeText(this, "Check your network connection ", Toast.LENGTH_SHORT).show()
                            is FirebaseAuthInvalidUserException -> Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                            is FirebaseAuthInvalidCredentialsException -> Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            is FirebaseTooManyRequestsException ->   Toast.makeText(this, "Too many attempts, please try again later", Toast.LENGTH_SHORT).show()
                            else ->  Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()

                        }
                        Log.d("FirebaseLoginFailedException", task.exception?.message.toString())

                    }

                    binding.loginProgress.visibility=View.GONE

                }

        } // logging user when click on login btn with entered credentials

        val googleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)  //
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        binding.googleBtn.setOnClickListener {

            binding.loginProgress.visibility=View.VISIBLE
            val intent:Intent=googleSignInClient.signInIntent
             startActivityForResult(intent,RC_SIGN_IN)

        }

    }

    private fun checkInternetConnection():Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check for network capabilities for active network
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Check if the network has internet connectivity
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }   // checking if the user is connected to internet or net

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

        auth.signInWithCredential(credential)
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


