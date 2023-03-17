package com.helic.wiggy

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.helic.wiggy.data.viewmodels.MainViewModel
import com.helic.wiggy.navigation.RootNavGraph
import com.helic.wiggy.ui.theme.BackgroundColor
import com.helic.wiggy.ui.theme.WiggyTheme
import com.helic.wiggy.utils.Msnackbar
import com.helic.wiggy.utils.rememberSnackbarState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController
    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val location by mainViewModel.getLocationData().observeAsState()
            WiggyTheme {
                val systemUiController = rememberSystemUiController()
                navController = rememberAnimatedNavController()
                val appState: Msnackbar = rememberSnackbarState()
                val systemUIColor = MaterialTheme.colors.BackgroundColor
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = systemUIColor
                    )
                }
                Scaffold(
                    scaffoldState = appState.scaffoldState
                ) {
                    RootNavGraph(
                        navController = navController,
                        mainViewModel = mainViewModel,
                        location = location
                    ) { message, duration ->
                        appState.showSnackbar(message = message, duration = duration)
                    }
                }
            }
            prepLocationUpdates()
        }
    }

    private fun prepLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Handle Permission granted/rejected
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "GPS unavailable", Toast.LENGTH_SHORT).show()
            }
        }

    private fun requestLocationUpdates() {
        mainViewModel.startLocationUpdate()
    }
}
