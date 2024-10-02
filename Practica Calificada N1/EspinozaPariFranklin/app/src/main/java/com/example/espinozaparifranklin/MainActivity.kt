package com.example.espinozaparifranklin

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.widget.EditText
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.espinozaparifranklin.databinding.ActivityMainBinding
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    // 1. Definimos nuestra clase Nodo
    data class Node(val id: Int, val x: Float, val y: Float, val name: String)

    // 2. Definimos las variables a usar
    private lateinit var binding: ActivityMainBinding
    private val nodes = mutableListOf<Node>()
    private var c = 0
    private lateinit var paint: Paint
    private lateinit var path: Path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(mBitmap)

        // Inicializamos ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



}