package com.example.mialgoritmo_genetico

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mialgoritmo_genetico.databinding.ActivityMainBinding
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    val puntos = mutableListOf<Punto>()
    private val bitmapSize = 500
    private val pointRadius = 5F

    // Variables globales para el bitmap y el canvas
    private lateinit var mCanvas: Canvas
    private lateinit var mBitmap: Bitmap
    // Utilizamos dos pinceles
    lateinit var paintPuntos: Paint
    lateinit var paintCamino: Paint

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuraciones para el canvas
        setupCanvas()

        // Paint para los puntos
        paintPuntos = Paint().apply {
            color = Color.RED
            strokeWidth = 10f
        }

        // Paint para el camino
        paintCamino = Paint().apply {
            color = Color.BLUE
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }

        binding.imageView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imageView = view as ImageView
                val (x, y) = calculateImageCoordinates(imageView, event)
                val nuevoPunto = Punto(x,y)
                puntos.add(nuevoPunto)
                // Dibujar los puntos en el ImageView sin sobreescribir lo existente
                dibujarPunto(nuevoPunto)
            }
            true
        }

        binding.btnaccion.setOnClickListener {
            val numCiudades = puntos.size
            val tamPoblacion = binding.txttampoblacion.text.toString().toInt()
            val probabilidadMutacion = binding.txtprobabilidadMutacion.text.toString().toDouble()
            val numGeneraciones = binding.txtnumgereraciones.text.toString().toInt()

            var poblacion = Array(tamPoblacion) { Individuo(numCiudades) }

            poblacion = calcular_aptitud(poblacion, puntos)
            for (t in 0 until numGeneraciones) {
                val seleccionados = seleccion_torneo(poblacion)
                for (i in seleccionados.indices step 2) {
                    val padre1 = seleccionados[i]
                    val padre2 = seleccionados[i + 1]
                    val hijo1 = mutar(cruzar(padre1, padre2), probabilidadMutacion)
                    val hijo2 = mutar(cruzar(padre2, padre1), probabilidadMutacion)
                    poblacion[i] = hijo1
                    poblacion[i + 1] = hijo2
                }
                poblacion = calcular_aptitud(poblacion, puntos)
            }

            val mejorIndividuo = poblacion[0]
            val caminoIndices = mejorIndividuo.get_camino_indices()

            val mensaje = mejorIndividuo.get_camino() + " - " + caminoIndices[0] + " => " + mejorIndividuo.get_distancia(puntos).toString()
            binding.lblresultado.text = mensaje

            //val myToast = Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG)
            //myToast.show()

            // Dibujamos el camino en el canvas
            dibujarCamino(puntos, caminoIndices)
        }
    }

    private fun calculateImageCoordinates(imageView: ImageView, e: MotionEvent): Pair<Float, Float> {
        // Convertimos las coordenadas del evento táctil en coordenadas de la imagen
        val scaleX = imageView.width.toFloat() / mBitmap.width
        val scaleY = imageView.height.toFloat() / mBitmap.height
        return Pair(e.x / scaleX, e.y / scaleY)
    }

    private fun setupCanvas() {
        mBitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
        clearCanvas()
        // Asignar el Bitmap al ImageView
        binding.imageView.setImageBitmap(mBitmap)
    }

    private fun clearCanvas() {
        // Volver a dibujar los puntos
        mCanvas.drawColor(Color.WHITE) // Limpia el canvas
        for (punto in puntos) {
            dibujarPunto(punto) // Redibuja todos los puntos
        }
    }

    private fun dibujarPunto(punto: Punto) {
        mCanvas.drawCircle(punto.x, punto.y, pointRadius, paintPuntos)
        binding.imageView.invalidate() //
    }

    private fun dibujarCamino(puntos: List<Punto>, camino: List<Int>) {
        // Limpiamos el camino anterior antes de dibujar el nuevo
        clearCanvas()
        /*
        for (punto in puntos) {
            println("Punto: (${punto.x}, ${punto.y})")
        }
        */
        // Dibujar el nuevo camino en el canvas global
        for (i in 0 until camino.size - 1) {
            val start = puntos[camino[i]]
            val end = puntos[camino[i + 1]]
            mCanvas.drawLine(start.x, start.y, end.x, end.y, paintCamino)
        }

        // Cerrar el ciclo del camino
        val first = puntos[camino[0]]
        val last = puntos[camino[camino.size - 1]]
        mCanvas.drawLine(last.x, last.y, first.x, first.y, paintCamino)

        binding.imageView.invalidate()
    }


    fun mutar(Hijo:Individuo,Pm:Double):Individuo{
        var aleatorio:Double = ThreadLocalRandom.current().nextDouble()
        if(aleatorio<Pm){
            var indice1:Int = ThreadLocalRandom.current().nextInt(Hijo.cromosoma.size)
            var indice2:Int=indice1
            while(indice1==indice2){
                indice2 =ThreadLocalRandom.current().nextInt(Hijo.cromosoma.size)
            }
            var t = Hijo.cromosoma[indice1]
            Hijo.cromosoma[indice1] = Hijo.cromosoma[indice2]
            Hijo.cromosoma[indice2] = t
        }
        return Hijo
    }
    fun cruzar(Padre1:Individuo,Padre2:Individuo):Individuo{
        var punto_cruce:Int = ThreadLocalRandom.current().nextInt(Padre1.cromosoma.size-1)
        var Hijo = Individuo(Padre1.num_ciudades)
        for (i in 0..punto_cruce-1){
            Hijo.cromosoma[i] = Padre1.cromosoma[i]
        }
        for (i in punto_cruce..Padre1.num_ciudades-1){
            Hijo.cromosoma[i] = Padre2.cromosoma[i]
        }
        return Hijo
    }
    fun seleccion_torneo(pobla:Array<Individuo>):Array<Individuo>{
        var seleccionados:Array<Individuo> = Array(pobla.size){Individuo(pobla[0].cromosoma.size)}
        for(i in 0..pobla.size-1){
            var indice1:Int = ThreadLocalRandom.current().nextInt(pobla.size)
            var indice2:Int=indice1
            while(indice1==indice2){
                indice2 = ThreadLocalRandom.current().nextInt(pobla.size)
            }
            var competidor1:Individuo = pobla[indice1]
            var competidor2:Individuo = pobla[indice2]
            if(competidor1.distancia<competidor2.distancia){
                seleccionados[i] = competidor1
            }else{
                seleccionados[i] = competidor2
            }
        }
        return seleccionados
    }
    fun calcular_aptitud(pobla:Array<Individuo>, C: MutableList<Punto>):Array<Individuo>{
        var ordenado:Array<Individuo> = Array(pobla.size){Individuo(pobla[0].cromosoma.size)}
        var aptitud:IntArray=IntArray(pobla.size){0}
        for(i in 0..pobla.size-1){
            aptitud[i] = pobla[i].get_distancia(C)
        }
        aptitud.sort()// MENOR --> MAYOR
        for(i in 0..pobla.size-1){
            for(j in 0..pobla.size-1){
                if(aptitud[i] == pobla[j].distancia){
                    ordenado[i] = pobla[j]
                    break
                }
            }
        }
        return ordenado
    }

}