package com.example.espinozaparifranklin_pc2.navigationcomponents

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.espinozaparifranklin_pc2.GlobalData
import com.example.espinozaparifranklin_pc2.Node
import com.example.espinozaparifranklin_pc2.R


class ListNodesActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Node>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_list_nodes)

        listView = findViewById(R.id.lvNodes)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, GlobalData.nodes)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDialog(position)
            true
        }
    }

    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Nodo")
            .setMessage("¿Estás seguro de que deseas eliminar este nodo?")
            .setPositiveButton("Eliminar") { _, _ ->
                GlobalData.nodes.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}