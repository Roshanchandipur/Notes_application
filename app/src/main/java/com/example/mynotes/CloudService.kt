package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CloudService : AppCompatActivity() {

    lateinit var signInButton: com.google.android.gms.common.SignInButton
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient
    val rc: Int = 0;
    lateinit var uploadButton: Button
    lateinit var deleteButton: Button
    lateinit var dataRepo: DataRepo
    lateinit var syncWithCloud: Button
    lateinit var userImage: ImageView
    lateinit var welcomeText: TextView
    lateinit var logoutButton: Button
    lateinit var msgToMe: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_service)

        signInButton = findViewById(R.id.sign_in_button)
        uploadButton = findViewById(R.id.uploadButton)
        deleteButton = findViewById(R.id.deleteButton)
        syncWithCloud = findViewById(R.id.syncWithCloud)
        msgToMe = findViewById(R.id.feedback)
        userImage = findViewById(R.id.userImage)
        welcomeText = findViewById(R.id.welcomeMsg)
        logoutButton = findViewById(R.id.logout)

        firebaseAuth = Firebase.auth
        firebaseDatabase = FirebaseDatabase.getInstance()

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebaseToken))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // user has not signed in
        if (firebaseAuth.currentUser == null)
            updateUILoginFlow()

        signInButton.setOnClickListener {
            signIn()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        uploadButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                upload()
            }
        }

        deleteButton.setOnClickListener {
            deleteFromCloud()
        }
        syncWithCloud.setOnClickListener {
            Log.d("cloud started", "sync button clicked")
            GlobalScope.launch(Dispatchers.IO) {
                dataRepo.syncWithCloud()
            }
        }
    }


    private fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, rc)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rc) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val googleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuth(googleSignInAccount.idToken!!)
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun upload() {
        dataRepo.uploadToCloud()
    }


    private fun deleteFromCloud() {
        dataRepo.deleteFromCloud()
    }

    private fun firebaseAuth(token: String) {
        val credentials = GoogleAuthProvider.getCredential(token, null)
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    var m: HashMap<String, String> = HashMap()
                    m.put("id", user!!.uid)
                    m.put("displayName", user.displayName!!)

                    m.put("pic", user.photoUrl.toString())
                    firebaseDatabase.getReference().child("users").child(user.getUid()).setValue(m)
                    Toast.makeText(this, "You have signed in", Toast.LENGTH_SHORT).show()
                    updateUISignedInFlow()

                    val database = NotesDatabase.getDatabaseInstance(application)
                    val noteDao = database.notesDAO()
                    dataRepo = DataRepo(noteDao)

//                    val intent1 = Intent(this, MainActivity::class.java)
//                    startActivity(intent1)

                } else {
                    Toast.makeText(this, "SignIn failed", Toast.LENGTH_SHORT).show()
                    // signin failed
                }
            }
            .addOnCanceledListener {

            }
    }

    private fun loginUser() {
        signInButton.alpha = 1f


    }

    private fun updateUILoginFlow() {
        syncWithCloud.alpha = 0.5f
        deleteButton.alpha = 0.5f
        uploadButton.alpha = 0.5f
        msgToMe.alpha = 0.5f
        logoutButton.alpha = 0.5f
        userImage.visibility = View.GONE

        syncWithCloud.isClickable = false
        deleteButton.isClickable = false
        uploadButton.isClickable = false
        msgToMe.isClickable = false
        logoutButton.isClickable = false

        signInButton.alpha = 1f
        signInButton.isClickable = true

        welcomeText.setText("Manage all your cloud services here")
    }

    private fun updateUISignedInFlow() {
        syncWithCloud.alpha = 1f
        deleteButton.alpha = 1f
        uploadButton.alpha = 1f
        msgToMe.alpha = 1f
        logoutButton.alpha = 1f
        userImage.visibility = View.VISIBLE

        val requestOptions: RequestOptions = RequestOptions()
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.ALL)

        Glide.with(this)
            .load(firebaseAuth.currentUser!!.photoUrl.toString())
            .apply(requestOptions)
            .into(userImage)


        syncWithCloud.isClickable = true
        deleteButton.isClickable = true
        uploadButton.isClickable = true
        msgToMe.isClickable = true
        logoutButton.isClickable = true

        signInButton.alpha = 0.5f
        signInButton.isClickable = false

        welcomeText.setText(firebaseAuth.currentUser!!.displayName.toString() + ", Manage all your cloud services here")
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        updateUILoginFlow()
    }
}