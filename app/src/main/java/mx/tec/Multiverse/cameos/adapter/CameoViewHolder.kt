package mx.tec.Multiverse.cameos.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mx.tec.Multiverse.R
import mx.tec.Multiverse.cameos.entities.Cameo
import mx.tec.Multiverse.databinding.CellCameoBinding

class CameoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val binding = CellCameoBinding.bind(view)
    fun bind(cameo: Cameo) {
        this.binding.name.text = cameo.name
        this.binding.universe.text = cameo.universe
        this.binding.gender.text = cameo.gender

        if (cameo.imageUrl!="" && !cameo.imageUrl.isNullOrEmpty()){
            Picasso.with(view.context)
                .load(cameo.imageUrl)
                .centerCrop()
                .resize(120,120)
                .into(this.binding.image)
        }
        else{
            this.binding.image.setImageResource(R.drawable.ic_tva)
        }
    }
}
