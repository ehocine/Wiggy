package com.helic.wiggy.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

object Constants {

    const val TIMEOUT_IN_MILLIS = 10000L

    var loadingState = MutableStateFlow(LoadingState.IDLE)
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    const val UTILS_DATABASE= "utils"
    const val UTILS_DOCUMENT = "utlis"
    const val ADMIN_EMAIL = "admin_email"
    const val PRIVACY_POLICY = "privacy_policy"

    const val FIRESTORE_DATABASE = "data"
    const val FIRESTORE_ANIMALS_DOCUMENT = "animals"
    const val FIRESTORE_USERS_DATABASE = "users"

    const val USERNAME_FIELD = "name"
    const val USER_BIO_FIELD = "bio"
    const val USER_IMAGE = "image"

    const val LIST_OF_ANIMALS = "listOfAnimals"
}