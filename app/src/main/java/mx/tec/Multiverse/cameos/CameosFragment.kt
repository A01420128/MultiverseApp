package mx.tec.Multiverse.cameos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import mx.tec.Multiverse.cameos.adapter.CameoAdapter
import mx.tec.Multiverse.cameos.entities.Cameo
import mx.tec.Multiverse.databinding.FragmentCameosBinding

class CameosFragment : Fragment(), android.widget.SearchView.OnQueryTextListener,
    SearchView.OnQueryTextListener {
    private lateinit var binding: FragmentCameosBinding

    private lateinit var cameos: List<Cameo>
    private lateinit var adapter: CameoAdapter
    private val databaseReference = Firebase.database.getReference("cameos")
    private lateinit var addCameoLauncher: ActivityResultLauncher<Intent>

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
        initRecycler()
        initDatasource()
        initCameoCaptureDialog()
        binding.searchBar.setOnQueryTextListener(this)
        binding.searchBar.setOnCloseListener {
            adapter.setCameos(cameos)
            adapter.notifyDataSetChanged()
            return@setOnCloseListener false
        }

    }

    private fun initRecycler () {
        cameos = mutableListOf<Cameo>()
        adapter = CameoAdapter(cameos)
        binding.cameos.layoutManager = LinearLayoutManager(this.context)
        binding.cameos.adapter = adapter
        binding.cameos.setListener(object: SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                removeCameo(position)
            }

            override fun onSwipedRight(position: Int) {
                binding.cameos.adapter?.notifyDataSetChanged()
            }

        })
    }

    private fun initDatasource() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var cameosList = mutableListOf<Cameo>()
                snapshot.children.forEach { dbObj ->
                    val cameo = dbObj.getValue(Cameo::class.java)
                    cameosList.add(cameo!!)
                }
                cameos = cameosList
                adapter.setCameos(cameos)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Error while getting gastos to firebase",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun initCameoCaptureDialog(){
        this.addCameoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado->
            if(resultado.resultCode == AppCompatActivity.RESULT_OK){
                val cameo : Cameo = resultado.data?.getSerializableExtra("cameo") as Cameo
            }

        }

        this.binding.floatingAddButton.setOnClickListener {
            CameoCaptureDialog(onSubmitClickListener = { cameo ->
                val id = databaseReference.push().key!!
                val cameo = Cameo(id, cameo.name, cameo.universe, cameo.gender, cameo.imageUrl)
                databaseReference.child(id).setValue(cameo).addOnSuccessListener {
                    Toast.makeText(this.context, "Added cameo to firebase", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(this.context, "Error while adding cameo to firebase", Toast.LENGTH_LONG).show()
                }
            }).show(getParentFragmentManager(), "")
        }

    }

    private fun removeCameo(position: Int) {
        val cameo = adapter.getCameo(position)
        databaseReference.child(cameo.id.toString()).removeValue().addOnSuccessListener {
            Toast.makeText(this.context, "Errased cameo from firebase", Toast.LENGTH_LONG ).show()
            adapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this.context, "Failed to errase cameo from firebase", Toast.LENGTH_LONG ).show()
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        binding.searchBar.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(!newText.isNullOrEmpty()){
            adapter.filterCameos(cameos, newText)
            adapter.notifyDataSetChanged()
        } else {
            adapter.setCameos(cameos)
            adapter.notifyDataSetChanged()
        }
        return true
    }

}
