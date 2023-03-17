package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.helic.wiggy.R
import com.helic.wiggy.components.AnimalInfoCard
import com.helic.wiggy.components.InfoCard
import com.helic.wiggy.components.OwnerCard
import com.helic.wiggy.components.Title
import com.helic.wiggy.data.models.animals.Animal
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.sendEmailToOwner


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Details(
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val selectedAnimal by mainViewModel.selectedAnimal
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details)) },
                actions = {
                    DetailsDropMenu(
                        context = context,
                        mainViewModel = mainViewModel,
                        animal = selectedAnimal,
                        snackbar = snackbar
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
                            .size(24.dp, 24.dp)
                            .clickable {
                                navController.navigateUp()
                            },
                        tint = MaterialTheme.colors.TextColor
                    )
                }
            )
        },
        content = {
            DetailsView(context = context, animal = selectedAnimal, snackbar = snackbar)
        }

    )
}

@Composable
fun DetailsView(context: Context, animal: Animal, snackbar: (String, SnackbarDuration) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.BackgroundColor)
    ) {

        // Animal Image
        item {
            animal.let {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RectangleShape)
                        .height(250.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(animal.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Animal Image"
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
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
                Spacer(modifier = Modifier.height(16.dp))
                AnimalInfoCard(it.name, it.gender, it.location)
            }
        }

        // Animal about
        item {
            animal.let {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = stringResource(R.string.about).plus(animal.name))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.about,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    color = colorResource(id = R.color.text),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Quick info
        item {
            animal.let {

                Spacer(modifier = Modifier.height(24.dp))
                Title(title = stringResource(R.string.quick_info))
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoCard(
                        title = stringResource(R.string.age),
                        value = it.age.toString().plus(" yrs")
                    )
                    InfoCard(title = stringResource(R.string.color), value = it.color)
                    InfoCard(
                        title = stringResource(R.string.weight),
                        value = it.weight.toString().plus("Kg")
                    )
                }
            }
        }

        // Owner info
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Title(title = stringResource(R.string.owner_info))
            Spacer(modifier = Modifier.height(16.dp))
            OwnerCard(context = context, owner = animal.owner, animal = animal, snackbar = snackbar)

        }

        // CTA - Adopt me button
        item {
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                    sendEmailToOwner(
                        context = context,
                        emailAddress = animal.owner.email,
                        subject = "Adopting ${animal.name}",
                        message = "Hey ${animal.owner.name}, I would like to adopt ${animal.name}, can we get in touch?",
                        snackbar = snackbar
                    )
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
                Text(stringResource(R.string.adopt_me))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun DetailsDropMenu(
    context: Context,
    mainViewModel: MainViewModel,
    animal: Animal,
    snackbar: (String, SnackbarDuration) -> Unit
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
                //Send email to admin
                sendEmailToOwner(
                    context = context,
                    emailAddress = mainViewModel.adminEmail.value,
                    subject = "Reporting ${animal.name}",
                    message = "Hi, I would like to report ${animal.name} from the user ${animal.owner.name} of the following ID: ${animal.owner.userID}, Thank you.",
                    snackbar = snackbar
                )

            }) {
                Row(Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Report",
                        tint = MaterialTheme.colors.TextColor
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = stringResource(R.string.report),
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colors.TextColor
                    )
                }
            }
        }
    }
}

