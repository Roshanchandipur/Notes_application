package com.example.mynotes

import android.content.Context
import android.media.Image
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
            onItemClick.onDelete(list[position])
        }
        holder.addButton.setOnClickListener{
            holder.text.visibility = View.VISIBLE
            val text = holder.editText.text.toString()
            holder.editText.setText("")
            holder.editText.visibility = View.GONE
            holder.editButton.visibility = View.VISIBLE
            holder.addButton.visibility = View.GONE
            holder.deleteButton.visibility = View.VISIBLE
            list[position].notes = text
            onItemClick.onEdit(list[position])
            onItemClick.onUpdateUnlockAdd()
        }
        holder.editButton.setOnClickListener{
//            holder.text.isEnabled = false
            holder.text.visibility = View.GONE
            holder.editText.setText(list[position].notes)
            holder.editText.visibility = View.VISIBLE

            holder.editButton.visibility = View.GONE
            holder.addButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.GONE
            holder.editText.requestFocus()
            onItemClick.onEditLockAdd()

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
    val editButton = itemView.findViewById<ImageView>(R.id.editNotes)
    val editText = itemView.findViewById<EditText>(R.id.editText)
    val addButton = itemView.findViewById<ImageView>(R.id.add)
}

interface ItemClickedI {
    fun onDelete(note: Notes)
    fun onEdit(note: Notes)
    fun onEditLockAdd()
    fun onUpdateUnlockAdd()
}
