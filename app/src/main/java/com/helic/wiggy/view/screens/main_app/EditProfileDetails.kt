package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.R
import com.helic.wiggy.components.Title
import com.helic.wiggy.data.models.owner.Owner
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.deleteOldProfilePicture
import com.helic.wiggy.utils.uploadProfilePicture

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfileDetails(
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val context = LocalContext.current
    val owner by mainViewModel.userInfo.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile)) },
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
                ProfileDetails(
                    owner = owner,
                    mainViewModel = mainViewModel,
                    navController = navController,
                    context = context,
                    snackbar = snackbar
                )
            }
        }
    )
}

@Composable
fun ProfileDetails(
    owner: Owner?,
    mainViewModel: MainViewModel,
    navController: NavController,
    context: Context,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    var userName by remember { mutableStateOf(owner!!.name) }
    var userBio by remember { mutableStateOf(owner!!.bio) }

    var imageUri by remember {
        mutableStateOf(Uri.parse(owner?.image))
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
                Spacer(modifier = Modifier.height(16.dp))
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .align(Alignment.TopCenter),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri.toString())
                        .crossfade(true)
                        .error(R.drawable.account)
                        .build(),
                    contentDescription = "User Image"
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
            owner.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = stringResource(R.string.my_name))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = {
                        Text(
                            text = stringResource(R.string.change_your_name),
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.change_your_name),
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
        }
        // My story details
        item {
            owner.let {
                Spacer(modifier = Modifier.height(24.dp))
                Title(title = stringResource(R.string.my_story))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userBio,
                    onValueChange = { userBio = it },
                    label = {
                        Text(
                            text = stringResource(R.string.change_bio),
                            color = MaterialTheme.colors.Blue
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.change_bio),
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
        }
        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    imageUri?.let {
                        mainViewModel.updateUserDetails(
                            context = context,
                            ownerName = userName,
                            ownerBio = userBio,
                            ownerImage = it.lastPathSegment.toString(),
                            snackbar = snackbar
                        )
                        if (pictureChanged) {
                            deleteOldProfilePicture(mainViewModel = mainViewModel)
                            uploadProfilePicture(fileUri = it, mainViewModel = mainViewModel)
                        }
                    }
                    navController.navigate(Screens.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
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
                Text(stringResource(R.string.save))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}