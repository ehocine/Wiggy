package com.helic.wiggy.data.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helic.wiggy.LocationData
import com.helic.wiggy.R
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.models.animals.ListOfAnimals
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.utils.AddOrRemoveAnimalAction
import com.helic.wiggy.utils.Constants.ADMIN_EMAIL
import com.helic.wiggy.utils.Constants.FIRESTORE_ANIMALS_DOCUMENT
import com.helic.wiggy.utils.Constants.FIRESTORE_DATABASE
import com.helic.wiggy.utils.Constants.FIRESTORE_USERS_DATABASE
import com.helic.wiggy.utils.Constants.LIST_OF_ANIMALS
import com.helic.wiggy.utils.Constants.PRIVACY_POLICY
import com.helic.wiggy.utils.Constants.USERNAME_FIELD
import com.helic.wiggy.utils.Constants.USER_BIO_FIELD
import com.helic.wiggy.utils.Constants.USER_IMAGE
import com.helic.wiggy.utils.Constants.UTILS_DATABASE
import com.helic.wiggy.utils.Constants.UTILS_DOCUMENT
import com.helic.wiggy.utils.LoadingState
import com.helic.wiggy.utils.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    init {
        getParameters(application)
    }

    private val locationData = LocationData(application)
    fun getLocationData() = locationData
    fun startLocationUpdate() {
        locationData.startLocationUpdates()
    }

    @OptIn(ExperimentalMaterialApi::class)
    val modalBottomSheetState: ModalBottomSheetState =
        ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    var selectedAnimal: MutableState<Animal> = mutableStateOf(Animal())

    var filteringQuery: MutableState<String> = mutableStateOf("")

    @SuppressLint("MutableCollectionMutableState")
    private var _animalsList: MutableStateFlow<List<Animal>> =
        MutableStateFlow(mutableListOf())
    var animalsList = _animalsList.asStateFlow()

    var gettingListOfAnimalsState = MutableStateFlow(LoadingState.IDLE)

    private var _userInfo: MutableStateFlow<Owner> = MutableStateFlow(Owner())
    var userInfo = _userInfo.asStateFlow()

    var adminEmail: MutableState<String> = mutableStateOf("")
    var privacyPolicyText: MutableState<String> = mutableStateOf("")


    fun getUserInfo(context: Context, snackbar: (String, SnackbarDuration) -> Unit) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(getApplication<Application>())) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        data?.addSnapshotListener { value, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }
                            if (value != null && value.exists()) {
                                _userInfo.value =
                                    value.toObject(Owner::class.java) ?: Owner()
                            } else {
                                snackbar(
                                    context.getString(R.string.error_occurred),
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            snackbar(
                                getApplication<Application>().getString(R.string.error_occurred),
                                SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }

    fun updateUserDetails(
        context: Context,
        ownerName: String,
        ownerBio: String,
        ownerImage: String,
        snackbar: (String, SnackbarDuration) -> Unit
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }

        if (hasInternetConnection(getApplication<Application>())) {
            if (currentUser != null) {
                CoroutineScope(Dispatchers.IO).launch {

                    var image = false

                    data?.update(USER_IMAGE, ownerImage)
                        ?.addOnSuccessListener {
                            image = true
                        }?.addOnFailureListener {

                            image = false
                        }
                    var name = false
                    data?.update(USERNAME_FIELD, ownerName)
                        ?.addOnSuccessListener {
                            name = true

                        }?.addOnFailureListener {
                            name = false
                        }
                    var bio = false
                    data?.update(USER_BIO_FIELD, ownerBio)
                        ?.addOnSuccessListener {
                            bio = true

                        }?.addOnFailureListener {
                            bio = false
                        }
//                    if (image && name && bio) {
//                        snackbar(
//                            "Details updated successfully",
//                            SnackbarDuration.Short
//                        )
//                    } else {
//                        snackbar(
//                            "Something went wrong",
//                            SnackbarDuration.Short
//                        )
//                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }

    }

    fun addOrRemoveAnimalFromFirebase(
        context: Context,
        snackbar: (String, SnackbarDuration) -> Unit,
        action: AddOrRemoveAnimalAction,
        animal: Animal
    ) {
        val db = Firebase.firestore
        val data = db.collection(FIRESTORE_DATABASE).document(FIRESTORE_ANIMALS_DOCUMENT)
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    when (action) {
                        AddOrRemoveAnimalAction.ADD -> {
                            data.update(
                                LIST_OF_ANIMALS,
                                FieldValue.arrayUnion(animal)
                            )
                                .addOnSuccessListener {

                                }.addOnFailureListener {
                                    snackbar(
                                        "Something went wrong: $it",
                                        SnackbarDuration.Short
                                    )
                                }
                        }
                        AddOrRemoveAnimalAction.REMOVE -> {
                            data.update(
                                LIST_OF_ANIMALS,
                                FieldValue.arrayRemove(animal)
                            )
                                .addOnSuccessListener {
                                }.addOnFailureListener {
                                    snackbar(
                                        "Something went wrong: $it",
                                        SnackbarDuration.Short
                                    )
                                }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            snackbar(
                                context.getString(R.string.error_occurred),
                                SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }

    fun addOrRemoveAnimalForUser(
        context: Context,
        snackbar: (String, SnackbarDuration) -> Unit,
        action: AddOrRemoveAnimalAction,
        animal: Animal
    ) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser
        val data = currentUser?.let { db.collection(FIRESTORE_USERS_DATABASE).document(it.uid) }
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    when (action) {
                        AddOrRemoveAnimalAction.ADD -> {
                            data?.update(
                                LIST_OF_ANIMALS,
                                FieldValue.arrayUnion(animal)
                            )?.addOnSuccessListener {

                            }?.addOnFailureListener {
                                snackbar(
                                    "Something went wrong: $it",
                                    SnackbarDuration.Short
                                )
                            }
                        }
                        AddOrRemoveAnimalAction.REMOVE -> {
                            data?.update(
                                LIST_OF_ANIMALS,
                                FieldValue.arrayRemove(animal)
                            )?.addOnSuccessListener {
                            }?.addOnFailureListener {
                                snackbar(
                                    "Something went wrong: $it",
                                    SnackbarDuration.Short
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            snackbar(
                                context.getString(R.string.error_occurred),
                                SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }

    }


    fun getListOfAnimalsFromFirebase(
        context: Context,
        snackbar: (String, SnackbarDuration) -> Unit
    ) {
        val db = Firebase.firestore
        val data = db.collection(FIRESTORE_DATABASE).document(FIRESTORE_ANIMALS_DOCUMENT)

        if (hasInternetConnection(getApplication<Application>())) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gettingListOfAnimalsState.emit(LoadingState.LOADING)

                    data.addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (value != null && value.exists()) {
                            val initList: MutableList<Animal> =
                                value.toObject(ListOfAnimals::class.java)?.listOfAnimals
                                    ?: mutableListOf()
//                            _animalsList.value =
//                                value.toObject(ListOfAnimals::class.java)?.listOfAnimals
//                                    ?: mutableListOf()

                            // Filtering by country
                            if (filteringQuery.value.isNotEmpty()) {
                                _animalsList.value = initList.filter { animal ->
                                    animal.location.contains(
                                        filteringQuery.value,
                                        ignoreCase = true
                                    )
                                }
                            } else {
                                _animalsList.value = initList
                            }

                        } else {
                            snackbar(
                                context.getString(R.string.error_occurred),
                                SnackbarDuration.Short
                            )
                        }
                    }
                    gettingListOfAnimalsState.emit(LoadingState.LOADED)
                } catch (e: Exception) {
                    gettingListOfAnimalsState.emit(LoadingState.ERROR)
                    withContext(Dispatchers.Main) {
                        snackbar(
                            context.getString(R.string.error_occurred),
                            SnackbarDuration.Short
                        )
                    }
                }
            }
        } else {
            snackbar(context.getString(R.string.device_not_connected), SnackbarDuration.Short)
        }
    }


    fun getParameters(
        context: Context,
    ) {
        val db = Firebase.firestore
        val data = db.collection(UTILS_DATABASE)
            .document(UTILS_DOCUMENT)
        if (hasInternetConnection(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    data.addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (value != null && value.exists()) {
                            adminEmail.value =
                                value.getString(ADMIN_EMAIL)!!
                            privacyPolicyText.value =
                                value.getString(PRIVACY_POLICY)!!
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                    }
                }
            }
        } else {
        }
    }
}