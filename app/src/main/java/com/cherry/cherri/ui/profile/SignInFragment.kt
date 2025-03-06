package com.cherry.cherri.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.cherry.cherri.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment(R.layout.fragment_signin) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.etEmailIn)
        val passwordEditText = view.findViewById<EditText>(R.id.etPasswordIn)
        val signInButton = view.findViewById<Button>(R.id.btnSignIn)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(view, "Signed in successfully", Snackbar.LENGTH_SHORT).show()
                        //findNavController().navigate(R.id.action_signInFragment_to_profileDetailsFragment)
                        notifyParentAuthenticationSuccess()
                    } else {
                        Snackbar.make(view, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }


    }
    private fun notifyParentAuthenticationSuccess() {
        (parentFragment as? ProfileFragment)?.onAuthenticationSuccess()
    }
}