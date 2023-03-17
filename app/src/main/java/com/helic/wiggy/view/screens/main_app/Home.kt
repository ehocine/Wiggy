package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.helic.wiggy.R
import com.helic.wiggy.components.ItemAnimalCard
import com.helic.wiggy.components.TopBar
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.utils.ErrorLoadingResults
import com.helic.wiggy.utils.LoadingList
import com.helic.wiggy.utils.LoadingState
import com.helic.wiggy.utils.NoResults
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Home(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val animalList by mainViewModel.animalsList.collectAsState()

    val state by mainViewModel.gettingListOfAnimalsState.collectAsState()

    LaunchedEffect(key1 = animalList) {
        mainViewModel.getListOfAnimalsFromFirebase(context = context, snackbar = snackbar)
    }

    ModalBottomSheetLayout(
        sheetContent = {
            BottomSheetContent(
                context = context,
                mainViewModel = mainViewModel,
                snackbar = snackbar
            )
        },
        sheetState = mainViewModel.modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        scope.launch {
                            mainViewModel.modalBottomSheetState.show()
                        }
                    },
                    backgroundColor = MaterialTheme.colors.Blue,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter Button",
                            tint = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.filter),
                            color = Color.White
                        )
                    }
                )

            }) {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.BackgroundColor) {
                Column(Modifier.fillMaxSize()) {
                    TopBar(
                        context = context,
                        navController = navController,
                        mainViewModel = mainViewModel,
                        snackbar = snackbar
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (state) {
                        LoadingState.LOADING -> LoadingList()
                        LoadingState.ERROR -> ErrorLoadingResults()
                        else -> {
                            if (animalList.isEmpty()) {
                                NoResults()
                            } else {
                                LazyColumn {
                                    items(animalList) { animal ->
                                        ItemAnimalCard(
                                            animal = animal,
                                            onItemClicked = {
                                                mainViewModel.selectedAnimal.value = it
                                                navController.navigate(Screens.Details.route)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}