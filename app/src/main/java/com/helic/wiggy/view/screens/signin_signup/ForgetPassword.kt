package com.helic.wiggy.view.screens.signin_signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.helic.wiggy.R
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.Constants.loadingState
import com.helic.wiggy.utils.LoadingState
import com.helic.wiggy.utils.resetUserPassword
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun ForgetPassword(navController: NavController, showSnackbar: (String, SnackbarDuration) -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var emailValue by remember { mutableStateOf("") }

    loadingState = MutableStateFlow(LoadingState.IDLE)
    val state by loadingState.collectAsState()

    Surface(modifier = Modifier.clickable { focusManager.clearFocus() }) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.retrieve_your_password),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.h5.fontSize
                    )
                    Spacer(modifier = Modifier.padding(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        OutlinedTextField(
                            value = emailValue,
                            onValueChange = { emailValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.email_address),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.Blue
                            )
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                resetUserPassword(context, snackbar = showSnackbar, emailValue)
                            },
                            enabled = state != LoadingState.LOADING,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.Blue)
                        ) {
                            if (state == LoadingState.LOADING) {
                                CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                            } else {
                                Text(
                                    text = stringResource(R.string.submit),
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(
                            text = stringResource(R.string.back_to_login),
                            fontSize = MaterialTheme.typography.subtitle1.fontSize,
                            modifier = Modifier.clickable {
                                navController.navigate(route = Screens.Login.route) {
                                    // popUpTo = navController.graph.startDestination
                                    launchSingleTop = true
                                }
                            })
                    }
                }
            }
        }
    }
}