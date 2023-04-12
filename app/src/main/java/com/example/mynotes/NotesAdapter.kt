package com.example.mynotes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class NotesAdapter(val context: Context, val onItemClick: ItemClickedI): RecyclerView.Adapter<NotesViewHolder>() {

    var list = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.view, parent, false))
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.text.text = list[position].notes
        holder.deleteButton.setOnClickListener{
            onItemClick.onItemClicked(list[position])
        }
    }

    fun changeedList(list1: List<Notes>){
        list.clear()
        list.addAll(list1)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val text = itemView.findViewById<TextView>(R.id.text)
    val deleteButton = itemView.findViewById<ImageView>(R.id.delete)
}

interface ItemClickedI {
    fun onItemClicked(note: Notes)
}
