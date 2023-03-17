package com.helic.wiggy.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.helic.wiggy.R
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.utils.Constants.FIRESTORE_ANIMALS_DOCUMENT
import com.helic.wiggy.utils.Constants.FIRESTORE_DATABASE
import com.helic.wiggy.utils.Constants.FIRESTORE_USERS_DATABASE
import com.helic.wiggy.utils.Constants.LIST_OF_ANIMALS
import com.helic.wiggy.utils.Constants.TIMEOUT_IN_MILLIS
import com.helic.wiggy.utils.Constants.USER_IMAGE
import com.helic.wiggy.utils.Constants.auth
import com.helic.wiggy.utils.Constants.loadingState
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


//Register new user
fun registerNewUser(
    navController: NavController,
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context,
    userName: String,
    emailAddress: String,
    password: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.createUserWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            val user = Firebase.auth.currentUser
                            val setUserName = userProfileChangeRequest {
                                displayName = userName
                            }
                            user!!.updateProfile(setUserName).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Tag", user.displayName.toString())
                                    createUser(user)
                                }
                            }
                            user.sendEmailVerification().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    snackbar(
                                        context.getString(R.string.verification_email_sent),
                                        SnackbarDuration.Short
                                    )
                                }
                            }
                            navController.navigate(Screens.Login.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }

                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Register: ${e.message}")
                        snackbar(
                            "${e.message}",
                            SnackbarDuration.Short
                        )
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}

//Sign in existing user
fun signInUser(
    navController: NavController,
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context,
    emailAddress: String,
    password: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.signInWithEmailAndPassword(emailAddress, password).await()
                        loadingState.emit(LoadingState.LOADED)
                        val user = Firebase.auth.currentUser
                        if (user!!.isEmailVerified) {
                            withContext(Dispatchers.Main) {
                                navController.navigate(Screens.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                snackbar(
                                    context.getString(R.string.email_address_not_verified),
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Sign in: ${e.message}")
                        snackbar(
                            "${e.message}",
                            SnackbarDuration.Short
                        )
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}


// Function to create a new user by getting the ID from the auth system
fun createUser(user: FirebaseUser?) {
    val db = Firebase.firestore
    val newUser = user?.let {
        Owner(
            userID = it.uid,
            name = it.displayName.toString(),
            email = it.email.toString(),
            bio = "",
            image = "",
            listOfAnimals = listOf()
        )
    }
    if (newUser != null) {
        db.collection(FIRESTORE_USERS_DATABASE).document(user.uid)
            .set(newUser)
            .addOnCompleteListener { task ->
                Log.d("Tag", "success $task")
            }.addOnFailureListener { task ->
                Log.d("Tag", "Failure $task")
            }
    }
}

//Reset password function
fun resetUserPassword(
    context: Context,
    snackbar: (String, SnackbarDuration) -> Unit,
    emailAddress: String
) {
    if (hasInternetConnection(context)) {
        if (emailAddress.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                        loadingState.emit(LoadingState.LOADING)
                        auth.sendPasswordResetEmail(emailAddress).await()
                        loadingState.emit(LoadingState.LOADED)
                        withContext(Dispatchers.Main) {
                            snackbar(context.getString(R.string.email_sent), SnackbarDuration.Short)
                        }
                    } ?: withContext(Dispatchers.Main) {
                        loadingState.emit(LoadingState.ERROR)
                        snackbar(context.getString(R.string.time_out), SnackbarDuration.Short)
                    }
                } catch (e: Exception) {
                    loadingState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        Log.d("Tag", "Reset: ${e.message}")
                        snackbar(
                            e.message.toString(),
                            SnackbarDuration.Short
                        )
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.verify_inputs), SnackbarDuration.Short)
        }
    } else {
        snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
    }
}


fun resendVerificationEmail(
    snackbar: (String, SnackbarDuration) -> Unit,
    context: Context
) {
    val user = auth.currentUser
    if (user != null) {
        if (!user.isEmailVerified) {
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    snackbar(
                        context.getString(R.string.verification_email_sent),
                        SnackbarDuration.Short
                    )
                }
            }.addOnFailureListener {
                snackbar(
                    it.message.toString(),
                    SnackbarDuration.Long
                )
            }
        } else {
            snackbar(
                context.getString(R.string.email_already_verified),
                SnackbarDuration.Short
            )
        }
    } else {
        snackbar(
            context.getString(R.string.error_occurred),
            SnackbarDuration.Short
        )
    }
}


fun uploadProfilePicture(fileUri: Uri, mainViewModel: MainViewModel) =
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Get current username
            val currentUser = Firebase.auth.currentUser

            // Create a storage reference from our app
            val storageRef = Firebase.storage.reference

            val profileRef =
                currentUser?.let { storageRef.child("${it.uid}/profilePicture/${fileUri.lastPathSegment}") }

            // Upload file
            profileRef?.putFile(fileUri)?.addOnSuccessListener {
                currentUser.let { firebaseUser ->
                    storageRef.child("${firebaseUser.uid}/profilePicture/${mainViewModel.userInfo.value.image}")
                        .downloadUrl.addOnSuccessListener {
                            Log.d("Tag", "Profile picture: $it")
                            updateProfilePictureUri(ownerImage = it.toString())
                        }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
        }
    }

fun deleteOldProfilePicture(mainViewModel: MainViewModel) {

    val oldFileUri = mainViewModel.userInfo.value.image

    // Get current username
    val currentUser = Firebase.auth.currentUser

    // Create a storage reference from our app
    val storageRef = Firebase.storage.reference
    currentUser?.let { storageRef.child("${it.uid}/profilePicture/${oldFileUri}") }?.delete()

}

fun downloadAnimalPicture(animal: Animal): String {

    var pictureURI = ""
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Get current username
            val currentUser = Firebase.auth.currentUser

            // Create a storage reference from our app
            val storageRef = Firebase.storage.reference

            currentUser?.let { firebaseUser ->
                storageRef.child("${firebaseUser.uid}/animals/${animal.image}")
                    .downloadUrl.addOnSuccessListener {
                        pictureURI = it.toString()
                    }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
        }
    }
    return pictureURI
}


fun uploadAnimalPicture(fileUri: Uri, animal: Animal) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Get current username
            val currentUser = Firebase.auth.currentUser

            // Create a storage reference from our app
            val storageRef = Firebase.storage.reference

            val profileRef =
                currentUser?.let { storageRef.child("${it.uid}/animals/${fileUri.lastPathSegment}") }

            // Upload file
            profileRef?.putFile(fileUri)?.addOnSuccessListener {

                currentUser.let { firebaseUser ->
                    storageRef.child("${firebaseUser.uid}/animals/${animal.image}")
                        .downloadUrl.addOnSuccessListener {
                            updateAnimalPictureUri(animal = animal, pictureUri = it.toString())
                        }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
            }
        }
    }
}

fun updateProfilePictureUri(ownerImage: String) {
    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser
    val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
    CoroutineScope(Dispatchers.IO).launch {

        data?.update(USER_IMAGE, ownerImage)
            ?.addOnSuccessListener {
            }?.addOnFailureListener {
            }
    }
}

fun updateAnimalPictureUri(pictureUri: String, animal: Animal) {
    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser
    val data = db.collection(FIRESTORE_DATABASE).document(FIRESTORE_ANIMALS_DOCUMENT)
    CoroutineScope(Dispatchers.IO).launch {

        data.update(
            LIST_OF_ANIMALS,
            FieldValue.arrayRemove(animal)
        ).addOnSuccessListener {
            animal.image = pictureUri
            data.update(
                LIST_OF_ANIMALS,
                FieldValue.arrayUnion(animal)
            )
        }.addOnFailureListener {
//            snackbar("Something went wrong: $it", SnackbarDuration.Short)
        }

        val dataOfUser =
            currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }

        dataOfUser?.update(
            LIST_OF_ANIMALS,
            FieldValue.arrayRemove(animal)
        )?.addOnSuccessListener {
            animal.image = pictureUri
            dataOfUser.update(
                LIST_OF_ANIMALS,
                FieldValue.arrayUnion(animal)
            )
        }?.addOnFailureListener {
            //            snackbar("Something went wrong: $it", SnackbarDuration.Short)
        }
    }
}

// Function to check is the user is not null and has email verified
fun userLoggedIn(): Boolean {
    val user = Firebase.auth.currentUser
    return user != null && user.isEmailVerified
}