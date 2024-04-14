package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CloudService : AppCompatActivity(){

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
    lateinit var msg: EditText
    lateinit var msgToMe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_service)

        signInButton = findViewById(R.id.sign_in_button)
        uploadButton = findViewById(R.id.uploadButton)
        deleteButton = findViewById(R.id.deleteButton)
        syncWithCloud = findViewById(R.id.syncButton)
        msgToMe = findViewById(R.id.sendButton)
        userImage = findViewById(R.id.userImage)
        welcomeText = findViewById(R.id.welcomeMsg)

        logoutButton = findViewById(R.id.logout)
        msg = findViewById(R.id.msgToMe)

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
            showDeleteConfirmationDialog()
        }
        syncWithCloud.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {
                dataRepo.syncWithCloud()
            }
            print("Updated your notes with cloud")
        }
        msgToMe.setOnClickListener {
            sendMsgToDeveloper()
        }
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser!=null){
            updateUISignedInFlow()
            val database = NotesDatabase.getDatabaseInstance(application)
            val noteDao = database.notesDAO()
            dataRepo = DataRepo(noteDao)
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
        print("Uploaded to cloud")
    }


    private fun deleteFromCloud() {
        dataRepo.deleteFromCloud()
        print("Deleted from cloud")
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
                    print("You are now signed in")
                    updateUISignedInFlow()

                    val database = NotesDatabase.getDatabaseInstance(application)
                    val noteDao = database.notesDAO()
                    dataRepo = DataRepo(noteDao)

                } else {
                    print("Your sign in request failed, plz try again")
                }
            }
            .addOnCanceledListener {

            }
    }

    private fun updateUILoginFlow() {
        logoutButton.visibility = View.GONE

        syncWithCloud.isClickable = false
        deleteButton.isClickable = false
        uploadButton.isClickable = false
        msgToMe.isClickable = false

        signInButton.alpha = 1f
        signInButton.isClickable = true
        signInButton.visibility = View.VISIBLE

        Glide.with(this).clear(userImage)
        welcomeText.setText("Sign in to manage all your cloud services here")
    }



    private fun updateUISignedInFlow() {
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
        logoutButton.visibility = View.VISIBLE
        logoutButton.isClickable = true

        signInButton.visibility = View.GONE


        welcomeText.setText(firebaseAuth.currentUser!!.displayName.toString() + ", Manage all your cloud services here")
    }

    private fun logoutUser() {
        firebaseAuth.signOut()
        updateUILoginFlow()
        print("You are now signed out")
    }

    fun print(item: String) {
        Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Delete")
            setMessage("Are you sure you want to delete entire cloud data?")
            setCancelable(true)
            setPositiveButton("Delete"){
                    dialog, _ ->
                deleteFromCloud()
                dialog.dismiss()
            }
            setNegativeButton("Cancel"){
                    dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun sendMsgToDeveloper(){
        var note = msg.text.toString()
        if(note.isEmpty()){
            print("Your note is empty")
            return
        }
        note = firebaseAuth.currentUser!!.displayName + " sent you, " + note
        firebaseDatabase.getReference().child("users").child(firebaseAuth.currentUser!!.uid).child("msgToMe").setValue(note)
        msg.setText("")
        
        print("Roshan will get your note soon...")
    }
}