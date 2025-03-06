package com.cherry.cherri.ui.notifications


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cherry.cherri.R

class NotificationsFragment : Fragment() {

    private lateinit var inputEditText: EditText
    private lateinit var displayTextView: TextView
    private lateinit var actionButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialize UI components
        /*
        actionButton = view.findViewById(R.id.button_action)

        // Set up button click listener
        actionButton.setOnClickListener {
            val inputText = inputEditText.text.toString()
            if (inputText.isNotEmpty()) {
                displayTextView.text = inputText
                Toast.makeText(requireContext(), "Text updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter some text.", Toast.LENGTH_SHORT).show()
            }
        }
*/
        return view
    }
}

