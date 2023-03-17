package com.helic.wiggy.view.screens.main_app

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.helic.wiggy.R
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.TextColor

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PrivacyPolicy(
    navController: NavController,
    mainViewModel: MainViewModel,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.privacy_policy)) },
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
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = mainViewModel.privacyPolicyText.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            ,
                        color = colorResource(id = R.color.text),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }

            }
        }
    )
}