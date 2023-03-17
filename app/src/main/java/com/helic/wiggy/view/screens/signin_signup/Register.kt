package com.helic.wiggy.view.screens.signin_signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.helic.wiggy.R
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.Screens
import com.helic.wiggy.ui.theme.Blue
import com.helic.wiggy.ui.theme.TextColor
import com.helic.wiggy.utils.Constants.loadingState
import com.helic.wiggy.utils.LoadingState
import com.helic.wiggy.utils.registerNewUser
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun RegisterPage(
    navController: NavController,
    mainViewModel: MainViewModel,
    showSnackbar: (String, SnackbarDuration) -> Unit
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var nameValue by remember { mutableStateOf("") }
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    loadingState = MutableStateFlow(LoadingState.IDLE)
    val state by loadingState.collectAsState()

    var passwordVisibility by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.clickable { focusManager.clearFocus() }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.h4.fontSize
                    )
                    Spacer(modifier = Modifier.padding(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = nameValue,
                            onValueChange = { nameValue = it },
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
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = passwordValue,
                            onValueChange = { passwordValue = it },
                            label = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.password),
                                    color = MaterialTheme.colors.Blue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colors.Blue),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }) {
                                    Icon(
                                        if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Password Eye",
                                        tint = MaterialTheme.colors.Blue
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                registerNewUser(
                                    navController = navController,
                                    snackbar = showSnackbar,
                                    context = context,
                                    userName = nameValue,
                                    emailAddress = emailValue,
                                    password = passwordValue
                                )
                            },
                            enabled = state != LoadingState.LOADING,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = MaterialTheme.colors.Blue,
                                contentColor = Color.White
                            )
                        ) {
                            if (state == LoadingState.LOADING) {
                                CircularProgressIndicator(color = MaterialTheme.colors.TextColor)
                            } else {
                                Text(
                                    text = stringResource(R.string.sign_up),
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Row {
                            Text(
                                text = stringResource(R.string.have_an_account_login),
                                fontSize = MaterialTheme.typography.subtitle1.fontSize
                            )
                            Spacer(modifier = Modifier.padding(end = 2.dp))
                            Text(
                                text = stringResource(R.string.login),
                                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.navigate(route = Screens.Login.route) {
                                        // popUpTo = navController.graph.startDestination
                                        launchSingleTop = true
                                    }
                                })
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                    }
                }
            }
        }
    }
}
