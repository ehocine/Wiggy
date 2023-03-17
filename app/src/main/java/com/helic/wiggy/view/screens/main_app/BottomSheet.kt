package com.helic.wiggy.view.screens.main_app

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helic.wiggy.R
import com.helic.wiggy.components.Title
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.Blue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    context: Context,
    mainViewModel: MainViewModel,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    val scope = rememberCoroutineScope()

    var filterLocation by mainViewModel.filteringQuery


    Column(
        Modifier
            .background(MaterialTheme.colors.BackgroundColor)
            .padding(15.dp)
    ) {
        Title(title = stringResource(R.string.filter_by_location))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = filterLocation,
            onValueChange = { filterLocation = it },
            label = {
                Text(
                    text = stringResource(R.string.location),
                    color = MaterialTheme.colors.Blue
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.location),
                    color = MaterialTheme.colors.Blue
                )
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.Blue
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                mainViewModel.filteringQuery.value = filterLocation
                mainViewModel.getListOfAnimalsFromFirebase(context = context, snackbar = snackbar)
                scope.launch {
                    mainViewModel.modalBottomSheetState.hide()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.Blue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(R.string.apply),
                fontSize = 16.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}