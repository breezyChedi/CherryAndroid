package com.cherry.cherri.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cherry.cherri.R
import com.cherry.cherri.data.UniversityWithFaculties
import com.cherry.cherri.databinding.ItemUniversityCardBinding


val logoMap = mapOf(
    "/logos/cput_logo.png" to R.drawable.cput_logo,
    "/logos/dut_logo.png" to R.drawable.dut_logo,
    "/logos/eduvos_logo.png" to R.drawable.eduvos_logo,
    "/logos/nmu_logo.png" to R.drawable.nmu_logo,
    "/logos/nwu_logo.png" to R.drawable.nwu_logo,
    "/logos/rhodes_logo.png" to R.drawable.rhodes_logo,
    "/logos/smhs_logo.png" to R.drawable.smhs_logo,
    "/logos/stellies_logo.png" to R.drawable.stellies_logo,
    "/logos/tut_logo.png" to R.drawable.tut_logo,
    "/logos/uct_logo.png" to R.drawable.uct_logo,
    "/logos/ufh_logo.png" to R.drawable.ufh_logo,
    "/logos/ufs_logo.png" to R.drawable.ufs_logo,
    "/logos/uj_logo.png" to R.drawable.uj_logo,
    "/logos/ukzn_logo.png" to R.drawable.ukzn_logo,
    "/logos/ul_logo.png" to R.drawable.ul_logo,
    "/logos/unisa_logo.png" to R.drawable.unisa_logo,
    "/logos/up_logo.png" to R.drawable.up_logo,
    "/logos/uv_logo.png" to R.drawable.uv_logo,
    "/logos/uwc_logo.png" to R.drawable.uwc_logo,
    "/logos/uz_logo.png" to R.drawable.uz_logo,
    "/logos/vaal_logo.png" to R.drawable.vaal_logo,
    "/logos/VC_logo.png" to R.drawable.vc_logo,
    "/logos/wits_logo.png" to R.drawable.wits_logo,
    "/logos/wsu_logo.png" to R.drawable.wsu_logo,



)

/*
class UniversityAdapter : ListAdapter<UniversityWithFaculties, UniversityAdapter.UniversityViewHolder>(DIFF_CALLBACK) {

    // Interface to handle university click events
    private var onUniversityClickListener: ((UniversityWithFaculties) -> Unit)? = null

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UniversityWithFaculties>() {
            override fun areItemsTheSame(oldItem: UniversityWithFaculties, newItem: UniversityWithFaculties) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UniversityWithFaculties, newItem: UniversityWithFaculties) =
                oldItem == newItem
        }
    }

    // ViewHolder class
    inner class UniversityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logo: ImageView = view.findViewById(R.id.universityLogo)
        private val name: TextView = view.findViewById(R.id.universityName)
        private val appBut: Button = view.findViewById(R.id.applicationButton)

        fun bind(university: UniversityWithFaculties, isSelected: Boolean) {
            val logoUrl = university.logoUrl // Path to the image file (e.g., "logos/university.png")
            val resourceId = logoMap[logoUrl] ?: R.drawable.uct_logo // Fallback if not found

            logo.setImageResource(resourceId)
            // Assuming you use the 'logoResId' in your model
            name.text = university.name
            appBut.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(university.appUrl) // Set the URL from the model
                }
                it.context.startActivity(intent)
            }
            if (isSelected) {
                itemView.scaleX = 1.1f
                itemView.scaleY = 1.1f
            } else {
                itemView.scaleX = 1.0f
                itemView.scaleY = 1.0f
            }

            itemView.setOnClickListener {
                onUniversityClickListener?.invoke(university)
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

            }
        }
    }

    // Create a click listener setter
    fun setOnUniversityClickListener(listener: (UniversityWithFaculties) -> Unit) {
        onUniversityClickListener = listener
    }

    // Inflate the item layout for the university card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_university_card, parent, false)
        return UniversityViewHolder(view)
    }

    // Bind data to each university item
    override fun onBindViewHolder(holder: UniversityViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPosition == position)
    }
}
*/

class UniversityAdapter : ListAdapter<UniversityWithFaculties, UniversityAdapter.UniversityViewHolder>(DIFF_CALLBACK) {

    private var onUniversityClickListener: ((UniversityWithFaculties) -> Unit)? = null
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    companion object {
        private const val PAYLOAD_SELECTION = "PAYLOAD_SELECTION"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UniversityWithFaculties>() {
            override fun areItemsTheSame(oldItem: UniversityWithFaculties, newItem: UniversityWithFaculties) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UniversityWithFaculties, newItem: UniversityWithFaculties) =
                oldItem == newItem
        }
    }

    // Add the missing onCreateViewHolder implementation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_university_card, parent, false)
        return UniversityViewHolder(view)
    }

    inner class UniversityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logo: ImageView = view.findViewById(R.id.universityLogo)
        private val name: TextView = view.findViewById(R.id.universityName)
        private val appBut: Button = view.findViewById(R.id.applicationButton)

        fun bind(university: UniversityWithFaculties, isSelected: Boolean) {
            val logoUrl = university.logoUrl
            val resourceId = logoMap[logoUrl] ?: R.drawable.uct_logo

            logo.setImageResource(resourceId)
            name.text = university.name
            appBut.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(university.appUrl)
                }
                it.context.startActivity(intent)
            }

            updateSelectionState(isSelected)

            itemView.setOnClickListener {
                onUniversityClickListener?.invoke(university)
                val previousPosition = selectedPosition
                // Fix: Use absoluteAdapterPosition instead of bindingAdapterPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition, PAYLOAD_SELECTION)
                notifyItemChanged(selectedPosition, PAYLOAD_SELECTION)
            }
        }

        private fun updateSelectionState(isSelected: Boolean) {
            itemView.scaleX = if (isSelected) 1.1f else 1.0f
            itemView.scaleY = if (isSelected) 1.1f else 1.0f
        }
    }

    override fun onBindViewHolder(
        holder: UniversityViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == PAYLOAD_SELECTION) {
            holder.bind(getItem(position), selectedPosition == position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: UniversityViewHolder, position: Int) {
        holder.bind(getItem(position), selectedPosition == position)
    }

    fun setOnUniversityClickListener(listener: (UniversityWithFaculties) -> Unit) {
        onUniversityClickListener = listener
    }
}


