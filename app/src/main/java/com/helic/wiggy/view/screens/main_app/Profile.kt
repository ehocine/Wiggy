package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.R
import com.helic.wiggy.components.ItemAnimalCardOfUser
import com.helic.wiggy.components.Title
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.Constants.auth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Profile(
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo(context = context, snackbar = snackbar)
    }

    val owner by mainViewModel.userInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                actions = {
                    ProfileDropMenu(
                        onEditProfileClicked = {
                            navController.navigate(Screens.EditProfileDetails.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        onPrivacyPolicyClicked = {
                            navController.navigate(Screens.PrivacyPolicy.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        onSignOutClicked = {
                            signOut(
                                context = context,
                                navController = navController,
                                showSnackbar = snackbar
                            )
                        }
                    )
                },
                backgroundColor = MaterialTheme.colors.BackgroundColor,
                contentColor = MaterialTheme.colors.TextColor,
                elevation = 0.dp,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.navigate(Screens.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Screens.AddAnimal.route) {
                        launchSingleTop = true
                    }
                },
                backgroundColor = MaterialTheme.colors.Blue,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Add Button",
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.add_animal),
                        color = Color.White
                    )
                }
            )
        },
        content = {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                DetailsView(
                    context = context,
                    owner = owner,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    snackbar = snackbar
                )
            }
        }
    )
}

@Composable
fun DetailsView(
    context: Context,
    owner: Owner?,
    mainViewModel: MainViewModel,
    navController: NavController,
    snackbar: (String, SnackbarDuration) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {
        // Basic details
        item {
            Box(Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))
                if (owner != null) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(CircleShape)
                            .align(Alignment.TopCenter),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(owner.image)
                            .crossfade(true)
                            .error(R.drawable.account)
                            .build(),
                        contentDescription = "User Image"
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                                }
                            }
                            else -> {
                                SubcomposeAsyncImageContent(
                                    modifier = Modifier.clip(RectangleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            owner.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = it!!.name)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.email,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    color = colorResource(id = R.color.text),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }

        // My story details
        item {
            owner.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = stringResource(R.string.my_story))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it!!.bio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    color = colorResource(id = R.color.text),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }

        // My animals
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Title(title = stringResource(R.string.my_animals))
        }
        items(owner!!.listOfAnimals) { animal ->
            ItemAnimalCardOfUser(
                animal = animal,
                context = context,
                mainViewModel = mainViewModel,
                onItemClicked = {
                    mainViewModel.selectedAnimal.value = it
                    navController.navigate(Screens.Details.route)
                },
                snackbar = snackbar
            )
        }
    }
}


@Composable
fun ProfileDropMenu(
    onEditProfileClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            painterResource(id = R.drawable.ic_vert),
            contentDescription = "Menu",
            tint = MaterialTheme.colors.TextColor
        )
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colors.BackgroundColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                expanded = false
                onEditProfileClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(R.string.edit_profile),
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }
            DropdownMenuItem(onClick = {
                expanded = false
                onPrivacyPolicyClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = "Privacy",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }

            DropdownMenuItem(onClick = {
                expanded = false
                onSignOutClicked()
            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(R.string.sign_out),
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }
        }
    }
}

fun signOut(
    context: Context,
    navController: NavController,
    showSnackbar: (String, SnackbarDuration) -> Unit
) {
    try {
        auth.signOut()
        showSnackbar(context.getString(R.string.successfully_signed_out), SnackbarDuration.Short)
        navController.navigate(Screens.Login.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }

        }
    } catch (e: Exception) {
        showSnackbar(context.getString(R.string.error_occurred), SnackbarDuration.Short)
    }
}