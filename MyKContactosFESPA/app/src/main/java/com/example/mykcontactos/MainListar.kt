package com.example.mykcontactos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mykcontactos.databinding.ActivityMainListarBinding

class MainListar : AppCompatActivity() {
    lateinit var binding: ActivityMainListarBinding
    var adapter: MyAdapter? = null
    var selectedContactId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Vincular el layout a la actividad
        binding = ActivityMainListarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el adaptador
        adapter = MyAdapter(applicationContext)
        binding.lvmiscontactos.adapter = adapter

        // Manejar el clic en un contacto de la lista
        binding.lvmiscontactos.setOnItemClickListener { parent, view, position, id ->
            val contacto = Global.miscontactos[position]
            val nombre = contacto.nombre
            val alias = contacto.alias
            val codigo = contacto.codigo
            val idcontacto = position

            // Navegar hacia MainAgregar con los datos del contacto
            val intent = Intent(applicationContext, MainAgregar::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("alias", alias)
            intent.putExtra("codigo", codigo)
            intent.putExtra("idcontacto", idcontacto)
            startActivity(intent)
        }

        // Manejar el botón de eliminar, esto esta oculto por defecto
        binding.btneliminar.visibility = View.GONE

        binding.lvmiscontactos.setOnItemClickListener { parent, view, position, id ->
            selectedContactId = position
            binding.btneliminar.visibility = View.VISIBLE // Mostrar el botón cuando se selecciona un contacto
        }

        // Acción para eliminar el contacto seleccionado
        binding.btneliminar.setOnClickListener {
            if (selectedContactId >= 0 && selectedContactId < Global.miscontactos.size) {
                Global.miscontactos.removeAt(selectedContactId)
                adapter?.notifyDataSetChanged() // Actualizar la lista
                Toast.makeText(applicationContext, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                binding.btneliminar.visibility = View.GONE // Ocultar el botón después de eliminar
            }
        }

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}