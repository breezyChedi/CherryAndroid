package com.cherry.cherri.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cherry.cherri.R

class DegreeDetailsFragment : Fragment(R.layout.fragment_degree_details) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val degreeTitle = arguments?.getString("degreeTitle")
        val degreeDescription = arguments?.getString("degreeDescription")

        view.findViewById<TextView>(R.id.tvDegreeTitle).text = degreeTitle
        view.findViewById<TextView>(R.id.tvDegreeDescription).text = degreeDescription

        view.findViewById<Button>(R.id.btnReturn).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
