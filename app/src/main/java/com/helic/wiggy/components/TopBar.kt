package com.helic.wiggy.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.helic.wiggy.R
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.TextColor


@Composable
fun TopBar(
    context: Context,
    navController: NavController,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo(context = context, snackbar = snackbar)
    }

    val owner by mainViewModel.userInfo.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = stringResource(R.string.hey, owner.name),
//                text = "Hey ${owner.name},",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.TextColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.adopt_new_friend),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.TextColor
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp, 36.dp, 0.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Icons",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = {
                        navController.navigate(Screens.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }),
                tint = MaterialTheme.colors.TextColor
            )
        }
    }
}