
package com.example.week4

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private val names = mutableListOf<String>()
    private lateinit var adapter: NameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = NameAdapter(
            names,

            onItemClick = { position ->
                AlertDialog.Builder(this)
                    .setTitle("이름 목록 삭제하기")
                    .setMessage("${names[position]}을(를) 목록에서 삭제하시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("삭제") { _, _ ->
                        names.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                    .show()
            },


            onItemLongClick = { position ->

                val editText = EditText(this)
                editText.setText(names[position])

                val container = android.widget.FrameLayout(this)
                val padding = (24 * resources.displayMetrics.density).toInt()

                container.setPadding(padding, 0, padding, 0)
                container.addView(editText)

                val dialog = AlertDialog.Builder(this)
                    .setTitle("이름 수정하기")
                    .setMessage("수정할 이름을 입력하세요.")
                    .setView(container)
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", null)
                    .create()

                dialog.setOnShowListener git branch -a{
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {

                            val newName = editText.text.toString().trim()

                            if (newName.isNotEmpty()) {
                                names[position] = newName
                                adapter.notifyItemChanged(position)
                                dialog.dismiss()
                            } else {
                                editText.error = "이름을 입력하세요."
                            }
                        }
                }

                dialog.show()
            }

        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            val name = editTextName.text.toString().trim()

            if (name.isNotEmpty()) {
                names.add(name)
                adapter.notifyItemInserted(names.size - 1)
                recyclerView.scrollToPosition(names.size - 1)
                editTextName.text.clear()
            } else {
                Toast.makeText(
                    this,
                    "이름을 입력하세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
