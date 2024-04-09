package com.example.driveohioia.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.driveohioia.location.BackgroundLocationPermissionTextProvider
import com.example.driveohioia.ui.navigation.BottomNavItem
import com.example.driveohioia.location.CoarseLocationPermissionTextProvider
import com.example.driveohioia.location.FineLocationPermissionTextProvider
import com.example.driveohioia.ui.theme.AppTheme
import com.example.driveohioia.ui.navigation.Navigation
import com.example.driveohioia.location.PermissionDialog
import com.example.driveohioia.ui.navigation.Screen
import com.example.driveohioia.db.DriveDAO
import com.example.driveohioia.other.Constants
import com.example.driveohioia.other.Constants.ACTION_SHOW_LIVE_DRIVE_SCREEN
import com.example.driveohioia.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/*
    The main activity, where all the major actions take place. This is where the permissions are
    requested, navigation is set up, and composables that appear on other screens are housed.

    The major screens are composables of this activity that are housed in different files.

 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var driveDAO: DriveDAO

    private val permissionsToRequest = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    @OptIn(UiToolingDataApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // Handles Permission Requests
                // Must be directly in Main Activity block because it inherits important things
                val mainViewModel = viewModel<MainViewModel>()
                val dialogQueue = mainViewModel.visiblePermissionDialogQueue
                // Creates a launcher that can request the permissions
                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        // Checks which permissions aren't granted
                        permissionsToRequest.forEach { permission ->
                            mainViewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }

                    },
                )

                // Launches permission request launcher
                SideEffect {
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                }
                // Dialog shown with request from app to ask for permissions
                dialogQueue
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                // Permissions that need to be requested, and their text providers
                                Manifest.permission.ACCESS_COARSE_LOCATION -> CoarseLocationPermissionTextProvider()
                                Manifest.permission.ACCESS_FINE_LOCATION -> FineLocationPermissionTextProvider()
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION -> BackgroundLocationPermissionTextProvider()
                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = mainViewModel::dismissDialog,
                            onOkClick = {
                                mainViewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )

                            },
                            onGoToAppSettingsClick = ::openAppSettings // Sends to app settings

                        )
                    }

                // Initializes the Navigation controller and navigation bar
                // In main activity so that it is present on all screens
                val navController = rememberNavController()
                NavigationBarScaffold(navController = navController)


            }

        }
    }

}





// Function that sends user to the app settings
fun Activity.openAppSettings(){
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package",packageName,null)
    ).also(::startActivity)
}


// Navigation Bar Composable.
// Separate function to not bog down the set content block
@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBarScaffold(navController: NavHostController) {
        Scaffold(
            topBar  = {
                // Top Bar showing title of app
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Text("Driving Safety Score App")
                    }
                )
            },
            // Major Navigation Bar Section
            bottomBar = {
                BottomNavigationBar(
                    items = listOf(
                        // The items on the bar that a user can click
                        BottomNavItem(
                            name = "Home", // Title
                            route = Screen.HomeScreen.route, // Route that is sent to navController
                            icon = Icons.Default.Home, //Icon that appears on the bar

                            ),
                        BottomNavItem(
                            name = "Statistics",
                            route = Screen.StatisticsScreen.route,
                            icon = Icons.AutoMirrored.Filled.List,

                            ),
                        BottomNavItem(
                            name = "New Drive",
                            route = Screen.DriveScreen.route,
                            icon = Icons.Default.Place,

                            ),
                        BottomNavItem(
                            name = "History",
                            route = Screen.HistoryScreen.route,
                            icon = Icons.Default.DateRange,
                        ),
                    ),
                    navController = navController,
                    onItemClick = {
                        navController.navigate(it.route)
                    }
                )
            }
        )
        {
            Navigation(navController = navController)
        }

    }

// UI of the bottom navigation bar. Colors, Icons, etc.
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit

){
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 5.dp
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    disabledIconColor = MaterialTheme.colorScheme.error,
                    disabledTextColor = MaterialTheme.colorScheme.error
                ),
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                        )
                    }
                    if(selected){
                        Text(
                            item.name,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            modifier = Modifier.offset(y=Dp(30F)),
                        )
                    }
                }
            )

        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    MaterialTheme{
    }
}




