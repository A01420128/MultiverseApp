package mx.tec.Multiverse

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentImagePath: String? = null

    private lateinit var btnOpenCamera: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var photo: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photo = result.data?.extras?.get("data") as Bitmap
                ivPhoto.setImageBitmap(photo)
                cargaFotoFirebase()
            }
        }

        btnOpenCamera = findViewById(R.id.btnOpenCamera)
        ivPhoto = findViewById(R.id.ivImage)

        btnOpenCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }

    }

    private fun cargaFotoFirebase() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var fotoId = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fotoName = fotoId.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("photos/$fotoName")
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        storageReference.putBytes(bytes.toByteArray()).addOnSuccessListener {
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }.addOnCanceledListener {
            Toast.makeText(this, "Cancellation to upload photo.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload photo.", Toast.LENGTH_LONG).show()
        }
    }

}

