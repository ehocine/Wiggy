package com.helic.wiggy.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.helic.wiggy.data.LocationDetails
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.utils.userLoggedIn
import com.helic.wiggy.view.screens.main_app.*
import com.helic.wiggy.view.screens.signin_signup.ForgetPassword
import com.helic.wiggy.view.screens.signin_signup.LoginPage
import com.helic.wiggy.view.screens.signin_signup.RegisterPage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    location: LocationDetails?,
    snackbar: (String, SnackbarDuration) -> Unit
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = if (!userLoggedIn()) Screens.Login.route else Screens.Home.route,
        route = "root"
    ) {
        composable(
            route = Screens.Login.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            LoginPage(
                navController = navController,
                mainViewModel = mainViewModel,
                showSnackbar = snackbar
            )
        }

        composable(
            route = Screens.Register.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            RegisterPage(
                navController = navController,
                mainViewModel = mainViewModel,
                showSnackbar = snackbar
            )
        }

        composable(
            route = Screens.ForgetPassword.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            ForgetPassword(
                navController = navController,
                showSnackbar = snackbar
            )
        }

        composable(
            route = Screens.Home.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            Home(
                navController = navController,
                mainViewModel = mainViewModel,
                snackbar = snackbar
            )
        }
        composable(
            route = Screens.Details.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            Details(navController, mainViewModel = mainViewModel, snackbar = snackbar)
        }

        composable(
            route = Screens.AddAnimal.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            AddAnimal(
                navController = navController,
                mainViewModel = mainViewModel,
                location = location,
                snackbar = snackbar
            )
        }
        composable(
            route = Screens.Profile.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            Profile(
                navController = navController,
                mainViewModel = mainViewModel,
                snackbar = snackbar
            )
        }
        composable(
            route = Screens.EditProfileDetails.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            EditProfileDetails(
                navController = navController,
                mainViewModel = mainViewModel,
                snackbar = snackbar
            )
        }
        composable(
            route = Screens.PrivacyPolicy.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            PrivacyPolicy(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }

}