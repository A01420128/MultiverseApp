package mx.tec.Multiverse.cameos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import mx.tec.Multiverse.R
import mx.tec.Multiverse.databinding.FragmentCameosBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameosFragment : Fragment() {
    private lateinit var binding: FragmentCameosBinding

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentImagePath: String? = null
    private lateinit var photo: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentCameosBinding.inflate(layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photo = result.data?.extras?.get("data") as Bitmap
                this.binding.ivImage.setImageBitmap(photo)
                cargaFotoFirebase()
            }
        }

        this.binding.btnOpenCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }
    }

    private fun cargaFotoFirebase() {
        val progressDialog = ProgressDialog(this.context)
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
            Toast.makeText(this.context, "Cancellation to upload photo.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this.context, "Failed to upload photo.", Toast.LENGTH_LONG).show()
        }
    }

}
