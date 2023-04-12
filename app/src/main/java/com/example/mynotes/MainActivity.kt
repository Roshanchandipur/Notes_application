package com.example.mynotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
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
    override fun onItemClicked(note: Notes) {
        viewModel.change(note)
    }
    fun add(){
        val editText = findViewById<EditText>(R.id.man_ki_baat)
        viewModel.insert(Notes(editText.text.toString()))
    }
}