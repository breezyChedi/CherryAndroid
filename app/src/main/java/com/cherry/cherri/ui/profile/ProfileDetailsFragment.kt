package com.cherry.cherri.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cherry.cherri.R
import com.cherry.cherri.databinding.FragmentProfileDetailsBinding
import com.cherry.cherri.firebase.FirebaseConfig

class ProfileDetailsFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileDetailsBinding // View binding setup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setupListeners()

        observeViewModel()
        viewModel.fetchUserData()

        return binding.root
    }
/*
    private fun setupListeners() {
        binding.btnExit.setOnClickListener {
            FirebaseConfig.signOut()

            if (findNavController().currentDestination?.id == R.id.profileDetailsFragment) {
                findNavController().navigate(R.id.action_profileDetailsFragment_to_profileFragment)
            } else {
                Log.e("Navigation", "Invalid current destination for navigation.")
                Toast.makeText(context, "Navigation error. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/
private fun setupListeners() {
    binding.btnExit.setOnClickListener {
        // Sign out the user
        FirebaseConfig.signOut()

        // Inform the parent fragment or activity to replace this fragment
        (parentFragment as? ProfileFragment)?.showSignInSignUpTabs()
    }
}


    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                binding.textViewUserName.text = (userProfile.name+" "+userProfile.surname) ?: "N/A"

                binding.textViewHighSchoolValue.text = userProfile.highSchool
                binding.textViewNationalityValue.text = userProfile.nationality

            // Bind other userProfile fields
            }
        }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                Log.e("nbt al :" ,profile.nbtScores!!["nbtAL"] ?: "Err")
                Log.e("nbt ql :" ,profile.nbtScores!!["nbtQL"] ?: "Err")
                Log.e("nbt mat :" ,profile.nbtScores!!["nbtMAT"] ?: "Err")

                binding.textViewApsScoreValue.text = profile.apsScore.toString() ?: "N/A"
                // Bind other profile fields
                binding.nbtAL.text = profile.nbtScores!!["nbtAL"]
                binding.nbtQL.text = profile.nbtScores!!["nbtQL"]
                binding.nbtMAT.text = profile.nbtScores!!["nbtMAT"]


                binding.subjectsLayout.removeAllViews()
                var subjectKeys = profile.subjects?.keys?.toList()
                if (subjectKeys != null) {
                    subjectKeys=subjectKeys.sorted()
                }
                var markKeys = profile.marks?.keys?.toList()
                if (markKeys != null) {
                    markKeys=markKeys.sorted()
                }
                println(subjectKeys)
                println(markKeys)
                // Ensure both lists have the same size to avoid mismatches
                if (subjectKeys != null) {
                    if (markKeys != null) {
                        if (subjectKeys.size == markKeys.size) {
                            for (index in subjectKeys.indices) {
                                val subjectKey = subjectKeys[index] // "subject1", "subject2", ...
                                val markKey = markKeys[index]       // "mark1", "mark2", ...

                                val subjectName = profile.subjects?.get(subjectKey) // e.g., "Mathematics"
                                val markValue = profile.marks?.get(markKey)         // e.g., "70"

                                println("Subject: $subjectName, Mark: $markValue")

                                // Create and add the subject view
                                val subjectView = createSubjectView(subjectName ?: "Unknown", markValue ?: "N/A")
                                binding.subjectsLayout.addView(subjectView)
                            }
                        } else {
                            // Handle size mismatch
                            println("Error: The size of subjects and marks does not match!")
                        }
                    }
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


/*
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
     */
    }


    private fun createSubjectView(subject: String, mark: String): View {
        val inflater = LayoutInflater.from(requireContext())
        val subjectEntry = inflater.inflate(R.layout.subject_entry, null)

        val subjectName = subjectEntry.findViewById<TextView>(R.id.subjectName)
        val subjectMark = subjectEntry.findViewById<TextView>(R.id.subjectMark)

        subjectName.text = subject
        subjectMark.text = "$mark%"

        return subjectEntry
    }


}