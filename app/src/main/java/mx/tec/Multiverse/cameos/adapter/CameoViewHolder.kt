package mx.tec.Multiverse.cameos.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mx.tec.Multiverse.cameos.entities.Cameo
import mx.tec.Multiverse.databinding.CellCameoBinding

class CameoViewHolder (view: View): RecyclerView.ViewHolder(view) {
    private val binding = CellCameoBinding.bind(view)
    fun bind(cameo: Cameo){
        this.binding.name.text = cameo.name
        this.binding.universe.text = cameo.universe
        this.binding.gender.text = cameo.gender
    }
}
