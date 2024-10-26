package com.oaomegalul.lab3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.view.get

@Entity(tableName = "to_do_list")
data class ListItem (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var task: String,
    var status: Boolean
)

@Dao
interface ListItemDao {
    @Update
    suspend fun update(listItem: ListItem)

    @Delete
    suspend fun delete(listItem: ListItem)

    @Insert
    suspend fun insert(listItem: ListItem)

    @Query ("SELECT * FROM to_do_list")
    suspend fun getAllItems(): List<ListItem>

    @Query("DELETE FROM to_do_list")
    suspend fun deleteAllItems()
}

@Database (
    version = 1,
    entities = [ListItem::class]
)
abstract class AppDatabase: RoomDatabase () {
    abstract fun listItemDao(): ListItemDao
}

class MainActivity : AppCompatActivity() {
    init {
        Log.v("KEK", "1")
    }
    private lateinit var listItems: MutableList<ListItem>
    private lateinit var adapter: MyListAdapter
    init {
        Log.v("KEK", "2")
    }


    init {
        Log.v("KEK", "3")
    }
    //val listItems = mutableListOf<ListItem>();


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logo_text: TextView by lazy { findViewById<TextView>(R.id.logo) }
        val user_input: EditText by lazy { findViewById<EditText>(R.id.needToDo) }
        val add_button: Button by lazy { findViewById<Button>(R.id.addButton) }
        val list_view: ListView by lazy { findViewById<ListView>(R.id.listView) }
        val clearButton: ImageButton by lazy { findViewById<ImageButton>(R.id.clearButton) }


        Log.v("LOL", logo_text.text.toString());

        val database = (application as DataBaseInit).database
        val listItemDao = database.listItemDao()

        listItems = mutableListOf()

        lifecycleScope.launch {
            listItems.addAll(listItemDao.getAllItems())
            Log.v("DEBUG", "List items added: ${listItems.size}")
            adapter.notifyDataSetChanged()
        }


        Log.v("DEBUG", "List size before setting adapter: ${listItems.size}")
        adapter = MyListAdapter(this, listItems, lifecycleScope, database)
        list_view.adapter = adapter

        add_button.setOnClickListener {
            val taskText = user_input.text.toString()
            if (taskText.isNotBlank()) {
                val newItem = ListItem(task = taskText, status = false)
                lifecycleScope.launch {
                    listItemDao.insert(newItem)
                    listItems.add(newItem)
                    Log.v("DEBUG", "Added item: $newItem, current list size: ${listItems.size}")
                    adapter.notifyDataSetChanged()

                    user_input.setText("")
                }
            } else {
                Toast.makeText(this, R.string.maybe, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        clearButton.setOnClickListener {
            lifecycleScope.launch {
                listItemDao.deleteAllItems()
                listItems.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, R.string.afterClearAllTasks, Toast.LENGTH_SHORT).show()
            }

        }

    }
}

