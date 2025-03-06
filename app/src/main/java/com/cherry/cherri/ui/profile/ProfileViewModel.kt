package com.cherry.cherri.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cherry.cherri.firebase.FirebaseConfig

data class Profile(
    val apsScore: Int? = null,
    val marks: Map<String, String>? = null,
    val nbtScores: Map<String, String>? = null,
    val savedAt: com.google.firebase.Timestamp? = null,
    val subjects: Map<String, String>? = null
)

data class UserProfile(
    val createdAt: com.google.firebase.Timestamp? = null,
    val email: String? = null,
    val gender: String? = null,
    val highSchool: String? = null,
    val name: String? = null,
    val nationality: String? = null,
    val phoneNumber: String? = null,
    val surname: String? = null
)


class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseConfig.firestore
    private val auth = FirebaseConfig.auth

    val userProfile = MutableLiveData<UserProfile?>()
    val profile = MutableLiveData<Profile?>()
    val error = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun fetchUserData() {
        loading.value = true
        val userId = FirebaseConfig.getCurrentUserId()

        if (userId != null) {
            // Fetch user profile from 'users' collection
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc.exists()) {
                        userProfile.value = userDoc.toObject(UserProfile::class.java)
                    } else {
                        userProfile.value = null
                    }
                }
                .addOnFailureListener { e ->
                    error.value = "Failed to load user profile: ${e.message}"
                }

            // Fetch profile details from 'profiles' collection
            val profileDocRef = firestore.collection("profiles").document(userId)
            profileDocRef.get()
                .addOnSuccessListener { profileDoc ->
                    if (profileDoc.exists()) {
                        profile.value = profileDoc.toObject(Profile::class.java)
                    } else {
                        profile.value = null
                    }
                }
                .addOnFailureListener { e ->
                    error.value = "Failed to load profile details: ${e.message}"
                }
                .addOnCompleteListener {
                    loading.value = false
                }
        } else {
            error.value = "User not authenticated."
            loading.value = false
        }
    }
}
