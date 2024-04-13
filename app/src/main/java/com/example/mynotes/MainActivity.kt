package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity(), ItemClickedI {

    lateinit var viewModel: NotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<RecyclerView>(R.id.viewNotes)

        viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        view.layoutManager = LinearLayoutManager(this)
        val adapter = NotesAdapter(this, this)
        view.adapter = adapter
        viewModel.allNotes.observe(this, Observer {
            adapter.changeedList(it)
        })
    }
    override fun onDelete(note: Notes) {
        viewModel.change(note)
    }
    fun add(view: View){
        val editText = findViewById<EditText>(R.id.saySomething)
        if(editText.text.isEmpty())
            return
        viewModel.insert(Notes(editText.text.toString()))
        editText.setText("")
    }

    override fun onEdit(note: Notes) {
        viewModel.edit(note)
    }

    override fun onEditLockAdd(){
        val saySomething = findViewById<EditText>(R.id.saySomething);
        saySomething.setHint("can't add new notes at the moment")
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.isClickable = false
        addButton.alpha = 0.5f
    }

    override fun onUpdateUnlockAdd(){
        val saySomething = findViewById<EditText>(R.id.saySomething);
        saySomething.setHint("Add Notes")
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.isClickable = true
        addButton.alpha = 1f
    }
    fun moreOptions(view: View){
        val intent = Intent(this, CloudService::class.java)
        startActivity(intent)
    }
}