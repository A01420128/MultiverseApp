package mx.tec.Multiverse.cameos

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.firebase.storage.FirebaseStorage
import mx.tec.Multiverse.cameos.entities.Cameo
import mx.tec.Multiverse.databinding.CaptureCameoBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameoCaptureDialog ( private  val onSubmitClickListener: (Cameo) -> Unit):
    DialogFragment() {
    private lateinit var binding: CaptureCameoBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var currentImagePath: String? = null
    private var photo: Bitmap? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = CaptureCameoBinding.inflate(LayoutInflater.from(context))
        val constructor = AlertDialog.Builder(requireActivity())
        constructor.setView(binding.root)
        val  dialog = constructor.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initAddCameo()
        initCameraLauncher()
        return dialog
    }

    private fun initAddCameo() {
        binding.addCameo.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val universe = binding.universeInput.text.toString()
            val gender = binding.genderInput.text.toString()
            if (photo != null) {
                uploadPhotoToFirebase { imageUrl ->
                    val cameo = Cameo("", name, universe, gender, imageUrl)
                    onSubmitClickListener.invoke(cameo)
                    dismiss()
                }
            } else {
                val cameo = Cameo("", name, universe, gender)
                onSubmitClickListener.invoke(cameo)
                dismiss()
            }
        }
    }

    private fun initCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photo = result.data?.extras?.get("data") as Bitmap
                this.binding.cameoImage.setImageBitmap(photo)
            }
        }

        this.binding.takePictureButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        }
    }

    private fun uploadPhotoToFirebase(onSuccess: (String) -> Unit) {
        if (photo == null) {
            return
        }

        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var fotoId = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fotoName = fotoId.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("photos/$fotoName")
        val bytes = ByteArrayOutputStream()
        photo!!.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        storageReference.putBytes(bytes.toByteArray()).addOnSuccessListener { snapshot ->
            snapshot.storage.downloadUrl.addOnCompleteListener { task ->
                val imageUrl = task.result.toString()
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                    onSuccess(imageUrl)
                }
            }
        }.addOnCanceledListener {
            Toast.makeText(this.context, "Cancellation to upload photo.", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this.context, "Failed to upload photo.", Toast.LENGTH_LONG).show()
        }
    }

}
