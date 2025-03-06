package com.cherry.cherri.ui.home

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.cherry.cherri.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CalculatorFragment : Fragment() {

    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner
    private lateinit var spinner4: Spinner
    private lateinit var spinner5: Spinner
    private lateinit var spinner6: Spinner


    private lateinit var editTextMark1: EditText
    private lateinit var editTextMark2: EditText
    private lateinit var editTextMark3: EditText
    private lateinit var editTextMark4: EditText
    private lateinit var editTextMark5: EditText
    private lateinit var editTextMark6: EditText
    private lateinit var apsScoreTextView: TextView

    private lateinit var nbt_AL: EditText
    private lateinit var nbt_QL: EditText
    private lateinit var nbt_MAT: EditText

    private lateinit var saveButton: Button
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()



    private val mathSubjects = listOf("Mathematics", "Mathematical Literacy")
    private val homeLanguages = listOf("English HL", "Afrikaans HL","isiZulu HL")
    private val additionalLanguages = listOf("Sesotho FAL", "Isizulu FAL", "Afrikaans FAL", "Sepedi FAL", "English FAL", "Xitsonga FAL", "Setswana FAL", "TshiVenda FAL")
    private val allSubjects = listOf("Physical Sciences", "History", "Geography", "Art", "Economics", "Biology", "Information Technology", "Computing and Technology", "Dramatic Arts")

    // Keep a distinct list for each spinner to prevent cross-modification
    private val subjectSpinner4 = allSubjects.toMutableList()
    private val subjectSpinner5 = allSubjects.toMutableList()
    private val subjectSpinner6 = allSubjects.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the spinners
        spinner1 = view.findViewById(R.id.spinner_subject1)
        spinner2 = view.findViewById(R.id.spinner_subject2)
        spinner3 = view.findViewById(R.id.spinner_subject3)
        spinner4 = view.findViewById(R.id.spinner_subject4)
        spinner5 = view.findViewById(R.id.spinner_subject5)
        spinner6 = view.findViewById(R.id.spinner_subject6)


        editTextMark1 = view.findViewById(R.id.editText_mark1)
        editTextMark2 = view.findViewById(R.id.editText_mark2)
        editTextMark3 = view.findViewById(R.id.editText_mark3)
        editTextMark4 = view.findViewById(R.id.editText_mark4)
        editTextMark5 = view.findViewById(R.id.editText_mark5)
        editTextMark6 = view.findViewById(R.id.editText_mark6)

        apsScoreTextView = view.findViewById(R.id.apsScoreText)

        nbt_AL = view.findViewById(R.id.editnbt1)
        nbt_QL = view.findViewById(R.id.editnbt2)
        nbt_MAT = view.findViewById(R.id.editnbt3)


        saveButton = view.findViewById(R.id.saveButton)

        // Set click listener on Save button
        saveButton.setOnClickListener {
            saveToProfile()
        }




        // Set adapters for the first three spinners (no issues expected here)
        setUpSpinner(spinner1, mathSubjects)
        setUpSpinner(spinner2, homeLanguages)
        setUpSpinner(spinner3, additionalLanguages)


        view.findViewById<View>(R.id.nbtButton).setOnClickListener {
            val url = "https://nbtests.uct.ac.za/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        view.findViewById<View>(R.id.button).setOnClickListener {
            calculateAndDisplayAPS()
        }
        // Set up remaining spinners with default values and prevent onItemSelected firing during setup
        setDefaultSelections()
    }


    private fun calculateAndDisplayAPS() {
        // Initialize the strategy instance
       // val pointCalculationStrategy = StandardPointCalculationStrategy()



        // Retrieve marks from each EditText
        val marksMap = mapOf(
            "subject1" to (view?.findViewById<EditText>(R.id.editText_mark1)?.text.toString().toIntOrNull() ?: 0),
            "subject2" to (view?.findViewById<EditText>(R.id.editText_mark2)?.text.toString().toIntOrNull() ?: 0),
            "subject3" to (view?.findViewById<EditText>(R.id.editText_mark3)?.text.toString().toIntOrNull() ?: 0),
            "subject4" to (view?.findViewById<EditText>(R.id.editText_mark4)?.text.toString().toIntOrNull() ?: 0),
            "subject5" to (view?.findViewById<EditText>(R.id.editText_mark5)?.text.toString().toIntOrNull() ?: 0),
            "subject6" to (view?.findViewById<EditText>(R.id.editText_mark6)?.text.toString().toIntOrNull() ?: 0)
        )

        val mappedScores = marksMap.values.map { mark ->
            when (mark) {
                in 80..100 -> 7
                in 70..79 -> 6
                in 60..69 -> 5
                in 50..59 -> 4
                in 40..49 -> 3
                in 30..39 -> 2
                in 20..29 -> 1
                else -> 0
            }
        }


        // Calculate the total APS score using the strategy instance
        val totalAPS = mappedScores.sum()

        val circularProgressIndicator = view?.findViewById<CircularProgressIndicator>(R.id.circularSeekBar)

        // Set the progress of the CircularProgressIndicator (assuming max APS score is 42)
        circularProgressIndicator?.setProgressCompat(totalAPS, true)
       // saveMarksToSharedPreferences()
       // circularSeekBar.progress = totalAPS.toFloat()
        // Update the TextView below the CircularSeekBar
        view?.findViewById<TextView>(R.id.apsScoreText)?.text = totalAPS.toString()
    }

    private fun setUpSpinner(spinner: Spinner, subjects: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setDefaultSelections() {
        // Disable listeners temporarily during initialization
        spinner4.onItemSelectedListener = null
        spinner5.onItemSelectedListener = null
        spinner6.onItemSelectedListener = null

        // Set up the adapters and explicit defaults
        setUpSpinner(spinner4, subjectSpinner4)
        setUpSpinner(spinner5, subjectSpinner5)
        setUpSpinner(spinner6, subjectSpinner6)

        // Set explicit default selections
        spinner4.setSelection(subjectSpinner4.indexOf("Biology")) // Set "Science" as default
        spinner5.setSelection(subjectSpinner5.indexOf("History")) // Set "History" as default
        spinner6.setSelection(subjectSpinner6.indexOf("Physical Sciences")) // Set "Physics" as default

        // Re-enable listeners after initialization
        setSpinnerListeners()
    }

    private fun setSpinnerListeners() {
        spinner4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubject = spinner4.selectedItem.toString()
                updateOtherSpinners(4, selectedSubject)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner5.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubject = spinner5.selectedItem.toString()
                updateOtherSpinners(5, selectedSubject)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner6.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubject = spinner6.selectedItem.toString()
                updateOtherSpinners(6, selectedSubject)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateOtherSpinners(changedSpinnerId: Int, selectedSubject: String) {
        // Get current selections for all spinners
        val currentSelection4 = spinner4.selectedItem?.toString() ?: ""
        val currentSelection5 = spinner5.selectedItem?.toString() ?: ""
        val currentSelection6 = spinner6.selectedItem?.toString() ?: ""

        // Create filtered lists excluding already selected subjects
        val availableSubjects4 = allSubjects.filter { it != selectedSubject && it != currentSelection5 && it != currentSelection6 }
        val availableSubjects5 = allSubjects.filter { it != selectedSubject && it != currentSelection4 && it != currentSelection6 }
        val availableSubjects6 = allSubjects.filter { it != selectedSubject && it != currentSelection4 && it != currentSelection5 }

        // Update spinners only if they weren't the one changed
        if (changedSpinnerId != 4) updateSpinnerOptions(spinner4, availableSubjects4, currentSelection4)
        if (changedSpinnerId != 5) updateSpinnerOptions(spinner5, availableSubjects5, currentSelection5)
        if (changedSpinnerId != 6) updateSpinnerOptions(spinner6, availableSubjects6, currentSelection6)
    }

    private fun updateSpinnerOptions(spinner: Spinner, subjects: List<String>, currentSelection: String) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Ensure the spinner maintains the current selection, if it's still in the available options
        val selectionIndex = subjects.indexOf(currentSelection)
        if (selectionIndex >= 0) {
            spinner.setSelection(selectionIndex)
        } else if (subjects.isNotEmpty()) {
            // If the current selection is no longer valid, reset to the first available option
            spinner.setSelection(0)
        }
    }

    private fun saveToProfile() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Please sign in to save your profile.", Toast.LENGTH_LONG).show()
            return
        }

        val apsScore = apsScoreTextView.text.toString().toIntOrNull() ?: 0

        val profileData = hashMapOf(
            "apsScore" to apsScore,
            "subjects" to mapOf(
                "subject1" to spinner1.selectedItem.toString(),
                "subject2" to spinner2.selectedItem.toString(),
                "subject3" to spinner3.selectedItem.toString(),
                "subject4" to spinner4.selectedItem.toString(),
                "subject5" to spinner5.selectedItem.toString(),
                "subject6" to spinner6.selectedItem.toString()
            ),
            "marks" to mapOf(
                "mark1" to editTextMark1.text.toString(),
                "mark2" to editTextMark2.text.toString(),
                "mark3" to editTextMark3.text.toString(),
                "mark4" to editTextMark4.text.toString(),
                "mark5" to editTextMark5.text.toString(),
                "mark6" to editTextMark6.text.toString()
            ),
            "nbtScores" to mapOf(
                "nbtAL" to nbt_AL.text.toString(),
                "nbtQL" to nbt_QL.text.toString(),
                "nbtMAT" to nbt_MAT.text.toString()
            ),
            "savedAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("profiles").document(user.uid)
            .set(profileData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }



}