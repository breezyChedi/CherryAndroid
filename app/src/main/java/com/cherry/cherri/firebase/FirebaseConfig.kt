package com.cherry.cherri.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {

    // Lazy initialization of FirebaseAuth instance
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // Lazy initialization of FirebaseFirestore instance
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // Helper function to get the current user ID (if logged in)
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Helper function to check if a user is authenticated
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    // Remove the listener when no longer needed
    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Sign-in successful
                } else {
                    callback(false, task.exception?.message) // Sign-in failed
                }
            }
    }
    fun signOut() {
        auth.signOut()
    }

    fun registerUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null) // Registration successful
                } else {
                    callback(false, task.exception?.message) // Registration failed
                }
            }
    }

}
