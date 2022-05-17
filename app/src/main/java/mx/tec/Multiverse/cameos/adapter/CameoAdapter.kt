package mx.tec.Multiverse.cameos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.tec.Multiverse.R
import mx.tec.Multiverse.cameos.entities.Cameo

class CameoAdapter(private var cameos: List<Cameo>) :
    RecyclerView.Adapter<CameoViewHolder>() {

    fun getCameo(position: Int): Cameo {
        return cameos[position]
    }

    fun setCameos(cameos: List<Cameo>){
        this.cameos = cameos
    }
    fun filterCameos(cameos: List<Cameo>, universe: String){
        val filtered = mutableListOf<Cameo>()
        for (cameo in cameos){
            if (cameo.universe == universe) filtered.add(cameo)
        }
        this.cameos = filtered
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CameoViewHolder(layoutInflater.inflate(
            R.layout.cell_cameo,
            parent, false))
    }

    override fun getItemCount(): Int = cameos.size


    override fun onBindViewHolder(holder: CameoViewHolder, position: Int) {
        val item = cameos[position]
        holder.bind(item)
    }
}
