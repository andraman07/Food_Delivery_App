package com.example.fooddeliveryapp.profileactivirty

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fooddeliveryapp.User
import com.example.fooddeliveryapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var galleryImageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraImageResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var storage : StorageReference
    private  var imageUri: Uri? =null

    companion object{
        private const val  GALLERY_PERMISSION_REQUEST_CODE=300
        private const val  CAMERA_PERMISSION_REQUEST_CODE=400
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRef = Firebase.database.reference
        firebaseAuth = Firebase.auth
        storage =Firebase.storage.reference

        binding.saveBtn.setOnClickListener {
            saveData()
        }

        showUserData()

        binding.photoEditBtn.setOnClickListener {

            val pictureDialog= AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems= arrayOf("Select photo from Gallery","Capture photo form Camera")
            pictureDialog.setItems(pictureDialogItems){
                    _, which->
                when(which){
                    0-> choosePhotoFromGallery()
                    1->clickPhotoFromCamera()
                }
            }
            pictureDialog.show()

        }

        galleryImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if(data!=null) {

                        try {
                            imageUri = data.data
                            binding.userPhoto.setImageURI(imageUri)

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Failed to load image from gallery",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }


        cameraImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if(data!=null) {
                        try {
                            val thumbNail: Bitmap = result!!.data!!.extras?.get("data") as Bitmap
                            binding.userPhoto.setImageBitmap(thumbNail)

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Failed to open camera",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }


            }

        uploadProfileImage()


    }

    private fun uploadProfileImage() {
              if (imageUri!=null){
                 
              }
    }


    private fun clickPhotoFromCamera() {

        val permissionsToRequest = arrayListOf<String>()

        if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
                ) {
            showRationaleDialogForPermissions()
            return
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionsToRequest.add(android.Manifest.permission.CAMERA)
        }


        if(permissionsToRequest.isEmpty()) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {

                cameraImageResultLauncher.launch(cameraIntent)

            }catch (e:ActivityNotFoundException){
                Log.e("ActivityNotFoundException",e.message.toString())
            }

        }else{
            ActivityCompat.requestPermissions(this,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun choosePhotoFromGallery() {


             if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                showRationaleDialogForPermissions()
              return
              }
              val permissionsToRequest = arrayListOf<String>()

             if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                       permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
             }

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if(permissionsToRequest.isEmpty()) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                try {

                    galleryImageResultLauncher.launch(galleryIntent)

                }catch (e:ActivityNotFoundException){
                    Log.e("ActivityNotFoundError",e.message.toString())
                }
            }else{
                ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toTypedArray(),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
            }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GALLERY_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                ) {
                    choosePhotoFromGallery()
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }

            }

            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                ) {
                    clickPhotoFromCamera()
                }

            }
        }

    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permission request for this feature It can be enabled under the Applications settings ")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.d("",e.message.toString())
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }


    private fun saveData(){

                     val userId = firebaseAuth.currentUser?.uid  // Replace with the user's actual ID
                     val user= User(binding.userName.text.toString(),binding.userEmail.text.toString(),binding.userContact.text.toString(),binding.userAddress.text.toString())

                     if (userId != null) {
                         databaseRef.child("users").child(userId).setValue(user)

             }

         }

    private fun showUserData() {

        val userId = firebaseAuth.currentUser?.uid  // Replace with the user's actual ID

        val userRef = userId?.let { databaseRef.child("users").child(it) }

        userRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user data exists
                    val user = dataSnapshot.getValue(User::class.java)

                    // Handle the user data (e.g., display it in your UI)
                    if (user != null) {
                        val name = user.username
                        val email = user.email
                        val mobileNumber = user.contact
                        val address=user.address

                        binding.userName.setText(name)
                        binding.userEmail.setText(email)
                        binding.userContact.setText(mobileNumber)
                        binding.userAddress.setText(address)

                        if (name != null) {
                            Log.d("onDataChange",name)
                        }
                    }
                } else {
                    // The user data does not exist
                    // Handle this case (e.g., show a message that the user doesn't exist)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors (e.g., network issues, permission denied, etc.)
                Log.w("onCancelled", "loadPost:onCancelled", databaseError.toException())
            }


        })


    }

}

