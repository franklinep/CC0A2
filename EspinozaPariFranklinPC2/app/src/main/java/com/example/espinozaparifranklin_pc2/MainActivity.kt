package com.example.espinozaparifranklin_pc2

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.espinozaparifranklin_pc2.databinding.ActivityMainBinding
import com.example.espinozaparifranklin_pc2.navigationcomponents.ListNodesActivity
import retrofit2.Call
import retrofit2.Callback
import kotlin.math.pow


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var usar_fijo: Float = 0.0f
    //private lateinit var fragmentManager: FragmentManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding for interact with views
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Set the toolbar as the action bar
        setSupportActionBar(binding.toolbar)

        // Initialize our app configurations
        init()

        // Touch listener function for our image view when the image view is touched
        binding.imgView.setOnTouchListener(::onImageTouch)

        // Draw the bezier curve when the button bezier is clicked
        binding.bttnBezier.setOnClickListener {
            clearCanvas()
            onBezierButtonClicked()
        }


        // Generate the short-path for our cities
        binding.btnaction.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {
                if (binding.checkBoxUsarFijo.isChecked){
                    usar_fijo = 1f
                }else{
                    usar_fijo = 0f
                }
                clearCanvas()
                onActionButtonClicked()
            }
        })

    }


    // ============================================================================================

    private fun init() {
        // Get imgView dimensions instead of using previous set of values
        binding.imgView.post {
            // Create bitmap
            bitmap = Bitmap.createBitmap(binding.imgView.width, binding.imgView.height, Bitmap.Config.ARGB_8888)
            // Create canvas base on our bitmap
            canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            // set the bitmap to our imgView to show it
            binding.imgView.setImageBitmap(bitmap)
        }
    }

    private fun clearCanvas() {
        canvas.drawColor(Color.WHITE)
        binding.imgView.setImageBitmap(bitmap)
        // Set the nodes again
        GlobalData.nodes.forEach { drawNode(Node(it.x, it.y, it.name)) }
    }

    // ================================NODES - QUESTION 1===============================================
    private fun onBezierButtonClicked() {
        if (GlobalData.nodes.size >= 3) {
            drawBezierCurve(GlobalData.nodes)
        } else {
            AlertDialog.Builder(this)
                .setTitle("No suficiente información")
                .setMessage("Se requieren al menos 3 nodos para generar la curva Bézier.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
    private fun drawBezierCurve(nodes: List<Node>) {
        if (nodes.size < 2) return

        val path = android.graphics.Path().apply {
            moveTo(nodes[0].x, nodes[0].y)
        }

        val bezierSteps = 100
        for (t in 0..bezierSteps) {
            val bezierPoint = calculateBezierPoint(t / bezierSteps.toFloat(), nodes)
            path.lineTo(bezierPoint.x, bezierPoint.y)
        }

        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3F
        }
        canvas.drawPath(path, paint)
        // Update the imgView with the new Bitmap
        binding.imgView.setImageBitmap(bitmap)
    }

    private fun calculateBezierPoint(t: Float, nodes: List<Node>): Node {
        var x = 0F
        var y = 0F
        val n = nodes.size - 1
        for (i in nodes.indices) {
            val bernstein = bernstein(n, i, t)
            x += bernstein * nodes[i].x
            y += bernstein * nodes[i].y
        }
        return Node(x, y, "bezierPoint")
    }

    private fun bernstein(n: Int, i: Int, t: Float): Float {
        return combination(n, i) * t.pow(i) * (1 - t).pow(n - i)
    }

    private fun combination(n: Int, k: Int): Int {
        return factorial(n) / (factorial(k) * factorial(n - k))
    }

    private fun factorial(num: Int): Int {
        return (1..num).fold(1) { acc, i -> acc * i }
    }


    private fun onImageTouch( v: View, e: MotionEvent): Boolean {
        when(e.action){
            MotionEvent.ACTION_DOWN ->{
                val x = e.x
                val y = e.y
                //Toast.makeText(this, "Touch down at: ($x, $y)", Toast.LENGTH_SHORT).show()
                showNodeDialog(x,y)
                true // Returning true means we've handled the touch event
            }
            else -> false
        }
        return true
    }

    private fun showNodeDialog(x: Float, y: Float) {
        // Show dialog and pass the function to handle the name input
        NameDialog { name ->

            // Create a instance
            val node = Node(x,y,name)
            GlobalData.nodes.add(node)
            // We draw the node in our canvas
            drawNode(node)
            // Instance Node with the provided name, and show a toast with the details
            //Toast.makeText(this, "Instance node: $name, $x, $y", Toast.LENGTH_SHORT).show()

        }.show(supportFragmentManager, "nameDialog")
    }

    private fun drawNode(node: Node) {
        // Define radio and our paint to draw
        val radius = 20f
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        // Draw a circle in a certain position
        canvas.drawCircle(node.x, node.y, radius, paint)

        // Draw the node's name :)
        paint.color = Color.BLACK
        paint.textSize = 40f
        canvas.drawText(node.name, node.x + radius + 5, node.y + radius + 5, paint)

        // Update the imgView with the new Bitmap :)
        binding.imgView.setImageBitmap(bitmap)
    }

    // ======================================MENU - QUESTION 2===============================================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu, this adds items to the action bar if it is present :0
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_en_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.item_nodes -> {
                val intent = Intent(this, ListNodesActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // ======================================Hugging Face - QUESTION 3===============================================

    fun onActionButtonClicked() {
        var nCiudades: Float = GlobalData.nodes.size.toFloat()
        var tampoblacion:Float = binding.txttampoblacion.getText().toString().toFloat()
        var probabilidad_mutacion:Float = binding.txtprobabilidadMutacion.getText().toString().toFloat()
        var numgeneraciones:Float = binding.txtnumgereraciones.getText().toString().toFloat()

        val requestData = RequestData(
            listOf(nCiudades, tampoblacion, probabilidad_mutacion, numgeneraciones, usar_fijo),
            arreglo_puntos(GlobalData.nodes)
        )

        val call = RetrofitTSP.tsp_api.predict(requestData)

        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(
                call: Call<ResponseData>,
                response: retrofit2.Response<ResponseData>
            ) {
                if(response.isSuccessful){
                    val responseData = response.body()
                    responseData?.let{
                        val minPath: MutableList<Node> = mutableListOf()
                        val pSize = it.prediction.size
                        // Let's save our message and path to show
                        var mss: String = ""
                        for(i in 0 until pSize - 1){
                            minPath.add(GlobalData.nodes[it.prediction[i]])
                            mss += " -> "+ GlobalData.nodes[it.prediction[i]].name
                        }

                        animarCamino(minPath)
                        mss += "\nLongitud Minima encontrada: ${it.prediction[pSize-1]}"
                        mss = "La mejor ruta es: " + mss
                        binding.lblresultado.setText(mss)
                    }
                }else{
                    val myToast = Toast.makeText(applicationContext,"Error1",Toast.LENGTH_LONG)
                    myToast.show()
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                var mensaje: String = t.message.toString()
                val myToast = Toast.makeText(applicationContext,"Error2:"+mensaje,Toast.LENGTH_LONG)
                myToast.show()
            }
        })
    }
    // ======================================Animation Path - QUESTION 4===============================================
    fun arreglo_puntos(Pts:MutableList<Node>): IntArray {
        val salida = IntArray(2*Pts.size){it}
        var contador:Int = 0
        for(i in 0..Pts.size-1) {
            // Con enteros ??
            salida [contador] = Pts[i].x.toInt()
            salida [contador+1] = Pts[i].y.toInt()
            contador = contador + 2
        }
        return salida
    }

    private fun animarCamino(nodes: List<Node>) {
        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5F
            isAntiAlias = true
        }

        val path = android.graphics.Path()


        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000L
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                val index = (animatedValue * (nodes.size - 1)).toInt()
                if (index < nodes.size - 1) {
                    val x0 = nodes[index].x
                    val y0 = nodes[index].y
                    path.moveTo(x0, y0)

                    val x1 = nodes[index + 1].x
                    val y1 = nodes[index + 1].y

                    path.lineTo(x1, y1)

                    canvas.drawPath(path, paint)
                    binding.imgView.setImageBitmap(bitmap)
                }
                val lastIndex = nodes.size-1
                val x0 = nodes[lastIndex].x
                val y0 = nodes[lastIndex].y
                path.moveTo(x0, y0)
                val x1 = nodes[0].x
                val y1 = nodes[0].y
                path.lineTo(x1,y1)
            }
        }
        animator.start()
    }


}