package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.R
import com.helic.wiggy.components.Title
import com.helic.wiggy.data.LocationDetails
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.AddOrRemoveAnimalAction
import com.helic.wiggy.utils.DropDownOptions
import com.helic.wiggy.utils.uploadAnimalPicture
import java.io.IOException
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddAnimal(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    location: LocationDetails?,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val context = LocalContext.current
    val owner by mainViewModel.userInfo.collectAsState()

    val geoCoder = Geocoder(context, Locale.getDefault())
    var newLocation: List<Address> = listOf()
    try {
        if (location != null) {
            newLocation =
                geoCoder.getFromLocation(
                    location.latitude.toDouble(),
                    location.longitude.toDouble(),
                    1
                )
        }
    } catch (e: IOException) {

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_animal)) },
                backgroundColor = MaterialTheme.colors.BackgroundColor,
                contentColor = MaterialTheme.colors.TextColor,
                elevation = 0.dp,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp, 24.dp)
                            .clickable {
                                navController.navigate(Screens.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        },
        content = {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                AnimalDetails(
                    mainViewModel = mainViewModel,
                    navController = navController,
                    owner = owner,
                    context = context,
                    newLocation = newLocation,
                    snackbar = snackbar
                )
            }
        }
    )
}

@Composable
fun AnimalDetails(
    mainViewModel: MainViewModel,
    navController: NavController,
    owner: Owner,
    context: Context,
    newLocation: List<Address>,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    var animalName by remember { mutableStateOf("") }
    var animalGender by remember { mutableStateOf("") }
    var animalAge by remember { mutableStateOf("") }
    var animalColor by remember { mutableStateOf("") }
    var animalWeight by remember { mutableStateOf("") }
    var animalLocation by remember { mutableStateOf("") }
    var animalBio by remember { mutableStateOf("") }

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse(""))
    }
    var pictureChanged by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            pictureChanged = true
        } else {
            pictureChanged = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {
        // Basic details
        item {
            Box(Modifier.fillMaxWidth()) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RectangleShape)
                        .height(250.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri.toString())
                        .crossfade(true)
                        .error(R.drawable.placeholder)
                        .build(),
                    contentDescription = "Animal Image"
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                        }
                    } else {
                        SubcomposeAsyncImageContent(
                            modifier = Modifier.clip(RectangleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                IconButton(onClick = {
                    launcher.launch("image/*")
                }, Modifier.align(Alignment.BottomEnd)) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "Profile picture")
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(
                    text = "*",
                    color = Color.Red,
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.W600,
                )
                Title(title = stringResource(R.string.animal_name))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalName,
                onValueChange = { animalName = it },
                label = {
                    Text(
                        text = stringResource(R.string.name),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.name),
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Title(title = stringResource(R.string.tell_us_more))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalBio,
                onValueChange = { animalBio = it },
                label = {
                    Text(
                        text = stringResource(R.string.bio),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.bio),
                        color = MaterialTheme.colors.Blue
                    )
                },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                )
            )

        }
        item {
            val genderList = listOf("Male", "Female")
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(
                    text = "*",
                    color = Color.Red,
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.W600,
                )
                Title(title = stringResource(R.string.animal_gender))
            }
            Spacer(modifier = Modifier.height(16.dp))
            DropDownOptions(
                label = stringResource(R.string.animal_gender),
                optionsList = genderList,
                onOptionSelected = {
                    animalGender = it
                })

        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(
                    text = "*",
                    color = Color.Red,
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.W600,
                )
                Title(title = stringResource(R.string.animal_age))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalAge,
                onValueChange = { animalAge = it },
                label = {
                    Text(
                        text = stringResource(R.string.animal_age_in_years),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.animal_age_in_years),
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Title(title = stringResource(R.string.animal_color))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalColor,
                onValueChange = { animalColor = it },
                label = {
                    Text(
                        text = stringResource(R.string.animal_color),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.animal_color),
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(
                    text = "*",
                    color = Color.Red,
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.W600,
                )
                Title(title = stringResource(R.string.animal_weight))
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalWeight,
                onValueChange = { animalWeight = it },
                label = {
                    Text(
                        text = stringResource(R.string.animal_weight_in_kgs),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.animal_weight_in_kgs),
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Text(
                    text = "*",
                    color = Color.Red,
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.W600,
                )
                Title(title = stringResource(R.string.animal_location))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // TODO: Load a location from GPS
                    mainViewModel.startLocationUpdate()
                    if (newLocation.isNotEmpty()) {
                        animalLocation =
                            "${newLocation[0].locality}, ${newLocation[0].adminArea}, ${newLocation[0].countryName}"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.get_location))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = animalLocation,
                onValueChange = { animalLocation = it },
                readOnly = true,
                label = {
                    Text(
                        text = stringResource(R.string.animal_location),
                        color = MaterialTheme.colors.Blue
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.animal_location),
                        color = MaterialTheme.colors.Blue
                    )
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.Blue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    if (pictureChanged && animalName.isNotEmpty()
                        && animalGender.isNotEmpty()
                        && animalAge.isNotEmpty()
                        && animalWeight.isNotEmpty()
                        && animalLocation.isNotEmpty()
                    ) {
                        val animal = Animal(
                            name = animalName,
                            age = animalAge.toDouble(),
                            gender = animalGender,
                            color = animalColor,
                            weight = animalWeight.toDouble(),
                            location = animalLocation,
                            image = imageUri?.lastPathSegment.toString(),
                            about = animalBio,
                            owner = owner
                        )

                        mainViewModel.addOrRemoveAnimalFromFirebase(
                            context = context,
                            snackbar = snackbar,
                            animal = animal,
                            action = AddOrRemoveAnimalAction.ADD
                        )
                        mainViewModel.addOrRemoveAnimalForUser(
                            context = context,
                            snackbar = snackbar,
                            animal = animal,
                            action = AddOrRemoveAnimalAction.ADD
                        )
                        imageUri?.let { uploadAnimalPicture(fileUri = it, animal = animal) }

                        navController.navigate(Screens.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    } else {
                        snackbar("Please fill out all the required fields!", SnackbarDuration.Short)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(16.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.add))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}