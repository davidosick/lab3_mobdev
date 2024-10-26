package com.oaomegalul.lab3

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MyListAdapter (
    context: Activity,
    private val dataSource: MutableList<ListItem>,
    private val coroutineScope: CoroutineScope,
    private val database: AppDatabase
) : ArrayAdapter<ListItem> (context, R.layout.list_item, dataSource) {

    override fun getView (position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val item = dataSource [position]
        Log.v("DEBUG", "Rendering item: ${item.task} at position: $position")
        checkBox.text = item.task
        checkBox.isChecked = item.status

        deleteButton.setOnClickListener {
            coroutineScope.launch {
                database.listItemDao().delete(item)
                dataSource.remove (item)
                notifyDataSetChanged()
            }
        }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            coroutineScope.launch {
                item.status = isChecked
                database.listItemDao().update(item)
                view.setBackgroundColor(context.getColor(if (isChecked) android.R.color.holo_green_light else android.R.color.transparent))
            }
        }


        return view
    }




}
