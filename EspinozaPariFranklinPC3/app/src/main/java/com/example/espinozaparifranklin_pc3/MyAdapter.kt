package com.example.espinozaparifranklin_pc3
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MyAdapter(
    private val context: Context,
    private val arrayList: ArrayList<Foto>
) : BaseAdapter() {
    private lateinit var txtNombreFoto: TextView
    private lateinit var txtResultadoRostro: TextView
    private lateinit var imgFoto: ImageView

    override fun getCount(): Int = arrayList.size
    override fun getItem(position: Int): Any = position
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_foto, parent, false)

        txtNombreFoto = view.findViewById(R.id.txtNombreFoto)
        txtResultadoRostro = view.findViewById(R.id.txtResultadoRostro)
        imgFoto = view.findViewById(R.id.imgFoto)

        val fotoActual = arrayList[position]

        txtNombreFoto.text = fotoActual.nombreFoto
        txtResultadoRostro.text = "Rostro: ${fotoActual.hayRostro}"

        // Cargar la imagen desde URL usando Glide
        Glide.with(context)
            .load(fotoActual.miUrl)
            .into(imgFoto)

        return view
    }
}