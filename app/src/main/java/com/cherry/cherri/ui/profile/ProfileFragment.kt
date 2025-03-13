package com.cherry.cherri.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cherry.cherri.R
import com.cherry.cherri.firebase.FirebaseConfig
import com.google.firebase.auth.FirebaseAuth

/*
class ProfileFragment : Fragment() {

    private val fb = FirebaseConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Use AuthManager to check if the user is authenticated
        val userAuthenticated = fb.isUserAuthenticated()

        val fragmentTransaction = childFragmentManager.beginTransaction()

        if (userAuthenticated) {
            // If authenticated, show the ProfileDetailsFragment
            fragmentTransaction.replace(R.id.fragment_container, ProfileDetailsFragment())
        } else {
            // If not authenticated, show the SignInSignUpTabsFragment
            fragmentTransaction.replace(R.id.fragment_container, SignInSignUpTabsFragment())
        }

        fragmentTransaction.commit()

        return view
    }

    fun onAuthenticationSuccess() {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, ProfileDetailsFragment())
        fragmentTransaction.commit()
    }


    override fun onStart() {
        super.onStart()
        // Add a listener for authentication state changes if needed
        fb.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        // Remove the listener when the fragment is stopped to prevent memory leaks
        fb.removeAuthStateListener(authStateListener)
    }

    fun showSignInSignUpTabs() {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignInSignUpTabsFragment())
            .commit()
    }






    // Authentication state listener to handle changes
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val userAuthenticated = firebaseAuth.currentUser != null
        val fragmentTransaction = childFragmentManager.beginTransaction()

        if (userAuthenticated) {
            // Show ProfileDetailsFragment if user is authenticated
            fragmentTransaction.replace(R.id.fragment_container, ProfileDetailsFragment())
        } else {
            // Show SignInSignUpTabsFragment if user is not authenticated
            fragmentTransaction.replace(R.id.fragment_container, SignInSignUpTabsFragment())
        }

        fragmentTransaction.commit()
    }
}

 */

class ProfileFragment : Fragment() {
    private val fb = FirebaseConfig
    private var pendingOperation: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Only perform initial fragment transaction if savedInstanceState is null
        if (savedInstanceState == null) {
            performFragmentTransaction {
                val userAuthenticated = fb.isUserAuthenticated()
                if (userAuthenticated) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileDetailsFragment())
                        .commitAllowingStateLoss()
                } else {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SignInSignUpTabsFragment())
                        .commitAllowingStateLoss()
                }
            }
        }
        return view
    }

    private fun performFragmentTransaction(transaction: () -> Unit) {
        if (!isAdded) {
            pendingOperation = transaction
            return
        }

        try {
            transaction.invoke()
        } catch (e: Exception) {
            // Log the error but don't crash
            android.util.Log.e("ProfileFragment", "Error performing fragment transaction", e)
        }
    }

    override fun onResume() {
        super.onResume()
        pendingOperation?.let {
            performFragmentTransaction(it)
            pendingOperation = null
        }
    }
    // Add this back to ProfileFragment
    fun onAuthenticationSuccess() {
        performFragmentTransaction {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileDetailsFragment())
                .commitAllowingStateLoss()
        }
    }

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        performFragmentTransaction {
            val userAuthenticated = firebaseAuth.currentUser != null
            if (userAuthenticated) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileDetailsFragment())
                    .commitAllowingStateLoss()
            } else {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SignInSignUpTabsFragment())
                    .commitAllowingStateLoss()
            }
        }
    }

    fun showSignInSignUpTabs() {
        performFragmentTransaction {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignInSignUpTabsFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        fb.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        fb.removeAuthStateListener(authStateListener)
    }
}