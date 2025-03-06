package com.cherry.cherri.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.cherry.cherri.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Form field references
        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val surnameEditText = view.findViewById<EditText>(R.id.surnameEditText)
        val highSchoolEditText = view.findViewById<EditText>(R.id.highSchoolEditText)
        val phoneNumberEditText = view.findViewById<EditText>(R.id.phoneNumberEditText)
        val genderSpinner = view.findViewById<Spinner>(R.id.genderSpinner)
        val nationalitySpinner = view.findViewById<Spinner>(R.id.nationalitySpinner)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val confirmEmailEditText = view.findViewById<EditText>(R.id.confirmEmailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.passwordConfirmText)
        val signUpButton = view.findViewById<Button>(R.id.signupButton)

        // Populate spinners with options
        val genderOptions = arrayOf("Male", "Female", "Other")
        val nationalityOptions = arrayOf("South Africa", "USA", "Canada", "UK", "India") // Add more as needed

        genderSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        nationalitySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalityOptions)

        signUpButton.setOnClickListener {
            // Retrieve input values
            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()
            val highSchool = highSchoolEditText.text.toString().trim()
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val gender = genderSpinner.selectedItem?.toString() ?: ""
            val nationality = nationalitySpinner.selectedItem?.toString() ?: ""
            val email = emailEditText.text.toString().trim()
            val confirmEmail = confirmEmailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validation
            if (name.isEmpty() || surname.isEmpty() || highSchool.isEmpty() || phoneNumber.isEmpty() ||
                gender.isEmpty() || nationality.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email != confirmEmail) {
                Snackbar.make(view, "Emails do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication and Firestore Integration
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        user?.let {
                            val userData = hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "highSchool" to highSchool,
                                "phoneNumber" to phoneNumber,
                                "gender" to gender,
                                "nationality" to nationality,
                                "email" to email,
                                "createdAt" to System.currentTimeMillis()
                            )
                            FirebaseFirestore.getInstance().collection("users")
                                .document(it.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Snackbar.make(view, "Sign-up successful!", Snackbar.LENGTH_SHORT).show()
                                    //findNavController().navigate(R.id.action_signUpFragment_to_profileDetailsFragment)
                                    notifyParentAuthenticationSuccess()
                                }
                                .addOnFailureListener { error ->
                                    Snackbar.make(view, "Failed to save user data: ${error.message}", Snackbar.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Snackbar.make(view, "Sign up failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }
    private fun notifyParentAuthenticationSuccess() {
        (parentFragment as? ProfileFragment)?.onAuthenticationSuccess()
    }
}

