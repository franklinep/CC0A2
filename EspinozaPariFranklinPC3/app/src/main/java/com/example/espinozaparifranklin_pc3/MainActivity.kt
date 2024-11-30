package com.example.espinozaparifranklin_pc3

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.espinozaparifranklin_pc3.databinding.ActivityMainBinding
import com.example.espinozaparifranklin_pc3.databinding.ItemFotoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.create
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingItemFoto: ItemFotoBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private lateinit var imageFile: File
    private lateinit var imageUri: Uri
    private var mBitmap: Bitmap? = null

    private val misFotos = ArrayList<Foto>()
    private lateinit var adapter: MyAdapter

    // Camera Permission Launcher
    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startDefaultCamera()
            } else {
                Toast.makeText(this, "Por favor, habilite el permiso de cámara en configuraciones", Toast.LENGTH_SHORT).show()
            }
        }

    // Take Picture Launcher
    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    binding.imgFoto.setImageBitmap(mBitmap)
                    mBitmap?.let { bitmap -> procesarImagen(bitmap) }
                } catch (e: IOException) {
                    Toast.makeText(this, "Error al capturar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Gallery Launcher
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val selectedImageUri = result.data?.data
                mBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                binding.imgFoto.setImageBitmap(mBitmap)
                mBitmap?.let { bitmap -> procesarImagen(bitmap) }
            } catch (e: IOException) {
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        storage = FirebaseStorage.getInstance()

        // Inflar binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar adaptador
        adapter = MyAdapter(this, misFotos)
        binding.lvMisFotos.adapter = adapter

        // Configurar listeners
        binding.btnTomarFoto.setOnClickListener { handleCameraPermission() }
        binding.btnGaleria.setOnClickListener { abrirGaleria() }

        // Configurar listener para obtener fotos de Firebase
        configurarListenerFirebase()
    }

    private fun configurarListenerFirebase() {
        val escucha = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                misFotos.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val nombreFoto = postSnapshot.child("nombreFoto").value.toString()
                    val miUrl = postSnapshot.child("miUrl").value.toString()
                    val hayRostro = postSnapshot.child("hayRostro").value.toString()

                    val foto = Foto(nombreFoto, miUrl, hayRostro)
                    misFotos.add(foto)
                }

                val mensaje = "Fotos cargadas: ${misFotos.size}"
                Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()

                // Notificar cambios al adaptador
                adapter.notifyDataSetChanged()

                // Vibración al cargar datos
                val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(500)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        // Añadir listener a la referencia de fotos
        databaseReference.child("fotos").addValueEventListener(escucha)
    }

    private fun startDefaultCamera() {
        imageFile = File.createTempFile("image_", ".jpg", cacheDir).apply {
            deleteOnExit()
        }

        imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startDefaultCamera()
            }
            else -> {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun procesarImagen(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestBody = create("image/*".toMediaTypeOrNull(), byteArray)
        val body = MultipartBody.Part.createFormData("file", "image.jpeg", requestBody)

        RetrofitFoto.instance.predict(body).enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val hayRostro = response.body()?.prediction ?: "NO"

                    // Actualizar TextView de resultado
                    binding.txtResultadoRostro.text = "Rostro detectado: $hayRostro"

                    // Guardar en Firebase
                    guardarImagenEnFirebase(bitmap, hayRostro)
                } else {
                    Toast.makeText(this@MainActivity, "Error en predicción", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarImagenEnFirebase(bitmap: Bitmap, hayRostro: String) {
        // Obtener el nombre de la foto ingresado por el usuario
        val nombreFoto = binding.edtNombreFoto.text.toString().trim()

        // Validar que se haya ingresado un nombre
        if (nombreFoto.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un nombre para la foto", Toast.LENGTH_SHORT).show()
            return
        }

        // Generar un nombre de archivo único
        val nombreArchivo = "${nombreFoto}_${System.currentTimeMillis()}.jpg"
        val storageRef = storage.reference.child("imagenes/$nombreArchivo")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        storageRef.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val foto = Foto(nombreArchivo, uri.toString(), hayRostro)

                    // Guardar en Realtime Database
                    databaseReference.child("fotos").push().setValue(foto)

                    // Actualizar TextView de nombre de foto
                    bindingItemFoto.txtNombreFoto.text = nombreFoto

                    // Limpiar el EditText después de guardar
                    binding.edtNombreFoto.text.clear()

                    Toast.makeText(this, "Imagen guardada: $nombreFoto", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
    }
}