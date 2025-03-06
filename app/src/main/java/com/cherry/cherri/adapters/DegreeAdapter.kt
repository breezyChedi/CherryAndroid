package com.cherry.cherri.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.cherry.cherri.R
import com.cherry.cherri.data.Degree
import com.cherry.cherri.data.SubjectRequirement

// DegreeAdapter.kt
class DegreeAdapter( private val degrees: List<Degree>) :
    RecyclerView.Adapter<DegreeAdapter.DegreeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DegreeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_degree_card, parent, false)
        return DegreeViewHolder(view)
    }

    fun formatSubjectRequirements(requirements: Array<SubjectRequirement>): List<String> {
        val processed = mutableListOf<String>()
        val orGroups = mutableMapOf<String, MutableMap<String, Int>>()

        requirements.forEach { req ->
            if (req.orSubject != null) {
                // Sort subjects alphabetically to create a unique key
                val subjects = listOf(req.subject, req.orSubject).sorted()
                val key = subjects.joinToString(" OR ")

                // Add to the OR group map
                if (!orGroups.containsKey(key)) {
                    orGroups[key] = mutableMapOf(req.subject to req.minPoints)
                } else {
                    orGroups[key]?.put(req.subject, req.minPoints)
                }
            } else {
                // Non-OR requirements are added directly
                processed.add("${req.subject}: ${req.minPoints}")
            }
        }

        // Process OR groups to create combined display strings
        orGroups.forEach { (subjects, subjectPoints) ->
            val formattedSubjects = subjectPoints.entries.joinToString(" OR \n") {
                "${it.key}: ${it.value}"
            }
            processed.add(formattedSubjects)
        }

        return processed
    }




    override fun onBindViewHolder(holder: DegreeViewHolder, position: Int) {
        val degree = degrees[position]
        holder.degreeTitle.text = degree.name
        holder.degreeDescription.text = degree.description
        holder.degreeMinAPS.text = "Minimum APS: ${degree.pointRequirement}"
        holder.degreeSubjReq.text = if (degree.subjectRequirements.isEmpty() || degree.subjectRequirements[0].minPoints == 0) {
            ""  // Empty string when no requirements
        } else {
            formatSubjectRequirements(degree.subjectRequirements).joinToString("\n")
        }


        // Optionally, set an OnClickListener if you want to handle item clicks
        holder.itemView.findViewById<Button>(R.id.viewDetails).setOnClickListener {
            val navController = findNavController(holder.itemView)
            val bundle = Bundle().apply {
                putString("degreeTitle", degree.name)
                putString("degreeDescription", degree.description)
            }
            navController.navigate(R.id.navigation_degree_details, bundle)
        }
    }

    private fun findNavController(view: View): NavController {
        return Navigation.findNavController(view)
    }

    override fun getItemCount(): Int = degrees.size

    class DegreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val degreeTitle: TextView = itemView.findViewById(R.id.degreeTitle)
        val degreeDescription: TextView = itemView.findViewById(R.id.desc)
        val degreeMinAPS: TextView = itemView.findViewById(R.id.minimumAps)
        val degreeSubjReq: TextView = itemView.findViewById(R.id.subjectRequirements)


    }
}
