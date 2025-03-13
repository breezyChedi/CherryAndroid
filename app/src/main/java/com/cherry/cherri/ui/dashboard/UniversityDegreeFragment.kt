package com.cherry.cherri.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cherry.cherri.R
import com.cherry.cherri.adapters.DegreeAdapter
import com.cherry.cherri.adapters.UniversityAdapter
import com.cherry.cherri.data.Faculty
import com.cherry.cherri.data.UniversityWithFaculties
import com.cherry.cherri.data.getDegreesForFaculty
import com.cherry.cherri.data.getUniversitiesWithFaculties
import com.cherry.cherri.data.Degree
import com.cherry.cherri.firebase.FirebaseConfig
import com.cherry.cherri.ui.profile.Profile
import com.cherry.cherri.util.filterDegreesByEligibility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UniversityDegreeFragment : Fragment() {

    private lateinit var universityRecyclerView: RecyclerView
    private lateinit var degreeRecyclerView: RecyclerView
    private lateinit var facultySpinner: Spinner
    private lateinit var filterCheckbox: CheckBox

    private lateinit var degreeAdapter: DegreeAdapter
    private lateinit var facultyAdapter: ArrayAdapter<String>


    private val universityAdapter = UniversityAdapter()


    private val faculties = mutableListOf<String>()
    private val degrees = mutableListOf<Degree>()

    private var selectedUniversity: UniversityWithFaculties? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_university_degree, container, false)

        // Initialize UI elements
        universityRecyclerView = view.findViewById(R.id.universityRecyclerView)
        degreeRecyclerView = view.findViewById(R.id.degreeRecyclerView)
        facultySpinner = view.findViewById(R.id.facultySpinner)
        filterCheckbox = view.findViewById(R.id.filterCheckbox)

        degreeAdapter = DegreeAdapter(degrees)
        setupUniversityRecyclerView()
        setupFacultySpinner()
        setupDegreeRecyclerView()
        setupFilterCheckbox()

        loadUniversities()


        return view
    }
/*
    private fun updateDegreesBasedOnSelection(faculty: Faculty, isFilterChecked: Boolean) {
        if (isFilterChecked) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.e("user",user.toString())
            var userIn = FirebaseConfig.isUserAuthenticated()
            Log.e("in:? ", userIn.toString())
            if (userIn) {
                // User is authenticated, fetch the profile
                val userId = FirebaseConfig.getCurrentUserId()
                val db = FirebaseFirestore.getInstance()
                if (userId != null) {
                    db.collection("profiles").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userProfile = document.toObject(Profile::class.java)
                                if (userProfile != null) {
                                    // Filter degrees using the user's profile and eligibility
                                    Log.e("deg for fac", getDegreesForFaculty(faculty.id).size.toString())
                                    val filteredDegrees = filterDegreesByEligibility(getDegreesForFaculty(faculty.id), userProfile, faculty.name)
                                    Log.e("filtered: ",filteredDegrees.size.toString())
                                    degrees.clear()
                                    degrees.addAll(filteredDegrees.toCollection(ArrayList()))
                                    degreeAdapter.notifyDataSetChanged()
                                } else {
                                    Toast.makeText(requireContext(), "Profile data is invalid.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Profile document not found.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Failed to load profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                filterCheckbox.isChecked = false
                // User is not authenticated, redirect to ProfileFragment
                Toast.makeText(requireContext(), "Please sign in to use filtering", Toast.LENGTH_SHORT).show()

            }
        } else {
            // Reset degrees when filter is unchecked
            degrees.clear()
            selectedUniversity?.faculties?.find { it.name == faculty.name }?.let {
                degrees.addAll(getDegreesForFaculty(faculty.id).toCollection(ArrayList()))
            }
            degreeAdapter.notifyDataSetChanged()
        }
    }
*/

    // Add these at class level
    private var cachedDegrees: Map<Long, List<Degree>> = mutableMapOf()
    private var cachedUserProfile: Profile? = null

    private fun updateDegreesBasedOnSelection(faculty: Faculty, isFilterChecked: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (isFilterChecked) {
                    if (!FirebaseConfig.isUserAuthenticated()) {
                        withContext(Dispatchers.Main) {
                            filterCheckbox.isChecked = false
                            Toast.makeText(requireContext(), "Please sign in to use filtering", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val userId = FirebaseConfig.getCurrentUserId() ?: run {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    // Use cached profile if available, otherwise fetch from Firestore
                    val userProfile = cachedUserProfile ?: withContext(Dispatchers.IO) {
                        try {
                            val document = FirebaseFirestore.getInstance()
                                .collection("profiles")
                                .document(userId)
                                .get()
                                .await()

                            document.toObject(Profile::class.java)?.also {
                                cachedUserProfile = it // Cache the profile
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileFetch", "Error fetching profile", e)
                            null
                        }
                    }

                    if (userProfile == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Profile data is invalid", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    // Get degrees from cache or fetch them
                    val facultyDegrees = withContext(Dispatchers.IO) {
                        cachedDegrees[faculty.id] ?: getDegreesForFaculty(faculty.id).also {
                            cachedDegrees = cachedDegrees + (faculty.id to it)
                        }
                    }

                    val filteredDegrees = withContext(Dispatchers.Default) {
                        filterDegreesByEligibility(facultyDegrees, userProfile, faculty.name)
                    }

                    withContext(Dispatchers.Main) {
                        degrees.clear()
                        degrees.addAll(filteredDegrees)
                        degreeAdapter.notifyDataSetChanged()
                    }

                } else {
                    // Handle unchecked filter case
                    withContext(Dispatchers.IO) {
                        val unfiltereDegrees = cachedDegrees[faculty.id] ?: getDegreesForFaculty(faculty.id).also {
                            cachedDegrees = cachedDegrees + (faculty.id to it)
                        }

                        withContext(Dispatchers.Main) {
                            degrees.clear()
                            degrees.addAll(unfiltereDegrees)
                            degreeAdapter.notifyDataSetChanged()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateDegrees", "Error updating degrees", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error updating degrees: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun setupFilterCheckbox() {
        filterCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Fetch selected faculty and update degree list accordingly
            facultySpinner.selectedItem?.let { selectedFacultyName ->
                selectedUniversity?.faculties?.find { it.name == selectedFacultyName }?.let { faculty ->
                    updateDegreesBasedOnSelection(faculty, isChecked)
                }
            }
        }
    }

/*
    private fun fetchProfileAndFilterDegrees(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("profile").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(Profile::class.java)
                    if (profile != null) {
                        filterDegrees(profile)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to fetch profile data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

*/
    private fun filterDegrees(profile: Profile) {
        val selectedFacultyName = faculties.getOrNull(facultySpinner.selectedItemPosition)
        if (selectedFacultyName != null && selectedUniversity != null) {
            val faculty = selectedUniversity!!.faculties.find { it.name == selectedFacultyName }
            if (faculty != null) {
                val filteredDegrees = filterDegreesByEligibility(degrees, profile, faculty.name)
                degrees.clear()
                degrees.addAll(filteredDegrees)
                degreeAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupUniversityRecyclerView() {
        universityRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        universityRecyclerView.adapter = universityAdapter

        // Mock Data: Load universities
        universityAdapter.setOnUniversityClickListener { university ->
            // When a university card is selected, update the faculty spinner and degree list
            selectedUniversity = university
            updateFacultySpinner(university.faculties)
        }
    }


    private fun setupDegreeRecyclerView() {
        val spanCount = if (resources.configuration.screenWidthDp < 800) {
            1 // Single column for small screens
        } else {
            2 // Two columns for larger screens
        }

        degreeRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        degreeRecyclerView.adapter = degreeAdapter
    }

/*
    private fun loadUniversities() {
        // Fetch universities and their faculties using getUniversitiesWithFaculties function
        val universities = getUniversitiesWithFaculties() // This is the function you previously defined
        universityAdapter.submitList(universities)
    }*/

    private fun loadUniversities() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val universities = getUniversitiesWithFaculties()
                universityAdapter.submitList(universities)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading universities",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupFacultySpinner() {
        // Initialize the adapter with a placeholder
        facultyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf("Select a university first")
        )
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the Spinner
        facultySpinner.adapter = facultyAdapter

        // Initially disable the spinner
        facultySpinner.isEnabled = false
    }

    private fun updateFacultySpinner(facultiesList: List<Faculty>) {
        if (facultiesList.isEmpty()) {
            // Reset to placeholder if no faculties are available
            facultyAdapter.clear()
            facultyAdapter.add("Select a university first")
            facultySpinner.isEnabled = false
            return
        }

        // Populate the adapter with faculty names
        facultyAdapter.clear()
        facultyAdapter.addAll(facultiesList.map { it.name })
        facultySpinner.isEnabled = true

        facultySpinner.setSelection(0)

        facultySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFaculty = facultiesList[position]
                // Call the update function when a new faculty is selected
                updateDegreesBasedOnSelection(selectedFaculty, filterCheckbox.isChecked)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Initialize with the first faculty if available
        updateDegreesBasedOnSelection(facultiesList[0], filterCheckbox.isChecked)
    }

    private fun updateDegreeList(faculty: Faculty) {
        // Clear the current degree list
        degrees.clear()
       // println("\nUpdate\n")

        // Filter degrees based on the selected faculty
        selectedUniversity?.faculties?.find { it.name == faculty.name }?.let {
            degrees.addAll(getDegreesForFaculty(faculty.id).toCollection(ArrayList())) // Assuming Faculty has a list of degrees
        }
       // println(degrees)
        Log.d("UpdateDegreeList", "Degrees size: ${degrees.size}")

        degreeAdapter.notifyDataSetChanged()
    }



}
