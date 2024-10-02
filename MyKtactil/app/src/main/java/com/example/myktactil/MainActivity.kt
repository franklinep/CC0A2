package com.example.myktactil

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myktactil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mBitmap = Bitmap.createBitmap(500,500,Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(mBitmap)
        // color del imageView
        mCanvas.drawColor(Color.GRAY)
        binding.myImg.setImageBitmap(mBitmap)

        val mPaint = Paint()
        // color del objeto a dibujar
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 2F// el grosor del objeto a dibujar
        mPaint.isAntiAlias =true

        var alto = mCanvas.height.toFloat()
        var ancho = mCanvas.width.toFloat()
        //dibujar la linea horizontal X
        mCanvas.drawLine(0F,alto/2,ancho,alto/2,mPaint)
        //dibujar la linea vertical Y
        mCanvas.drawLine(ancho/2,0F,ancho/2,alto,mPaint)
        mPaint.color = Color.RED
        binding.myImg.setImageBitmap(mBitmap)

        val displayMetrics = DisplayMetrics().also {
            windowManager.defaultDisplay.getMetrics(it)
        }

        binding.myImg.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                var proporcionancho = displayMetrics.widthPixels
                var proporcionalto = displayMetrics.heightPixels
                var x = e.x*500/proporcionancho
                var y = e.y*500/proporcionancho
                var mensaje1:String = "("+x.toString()+","+y.toString()+")"
                binding.lblposicion.setText(mensaje1)
                var mensaje2:String = "("+(x-250).toString()+","+(y-250).toString()+")"
                binding.lblcoordenada.setText(mensaje2)
                mCanvas.drawCircle(x,y,2F,mPaint)
                binding.myImg.setImageBitmap(mBitmap)
                return true
            }
        })

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }
}