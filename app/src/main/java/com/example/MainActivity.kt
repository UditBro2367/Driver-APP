package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AlertStatus
import com.example.ui.components.EmergencyPriorityBanner
import com.example.ui.components.TopSyncAndPriorityBar
import com.example.ui.screens.GpsNavigationScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfilePanelScreen
import com.example.ui.screens.RequestDashboardScreen
import com.example.ui.screens.SchedulePanelScreen
import com.example.ui.theme.AmbulanceDriverTheme
import com.example.ui.theme.HDBackground
import com.example.ui.theme.HDBlueContainer
import com.example.ui.theme.HDBlueLightBg
import com.example.ui.theme.HDBluePrimary
import com.example.ui.theme.HDBorder
import com.example.ui.theme.HDEmergencyRed
import com.example.ui.theme.HDNavBackground
import com.example.ui.theme.HDNavyDark
import com.example.ui.theme.HDSurface
import com.example.ui.theme.HDTextMuted
import com.example.ui.theme.HDTextPrimary
import com.example.ui.theme.HDTextSecondary
import com.example.ui.viewmodel.AmbulanceDriverViewModel
import com.example.ui.viewmodel.AppNavTab

class MainActivity : ComponentActivity() {
    private val viewModel: AmbulanceDriverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AmbulanceDriverTheme(darkTheme = false) {
                val context = LocalContext.current
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    ) {
                        viewModel.gpsManager.startLocationTracking()
                    }
                }

                LaunchedEffect(Unit) {
                    val fineGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!fineGranted) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                AmbulanceAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AmbulanceAppRoot(viewModel: AmbulanceDriverViewModel) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isSimulatedOffline by viewModel.isSimulatedOffline.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val locationData by viewModel.locationData.collectAsStateWithLifecycle()
    val activeEmergencyAlert by viewModel.activeEmergencyAlert.collectAsStateWithLifecycle()
    val unacceptedCount by viewModel.unacceptedCriticalAlertsCount.collectAsStateWithLifecycle()

    val alerts by viewModel.allAlerts.collectAsStateWithLifecycle()
    val trips by viewModel.scheduledTrips.collectAsStateWithLifecycle()
    val profile by viewModel.driverProfile.collectAsStateWithLifecycle()
    val selectedAlertForDetails by viewModel.selectedAlertForDetails.collectAsStateWithLifecycle()
    val showProfileEditDialog by viewModel.showProfileEditDialog.collectAsStateWithLifecycle()
    val showCompanyFleetPanel by viewModel.showCompanyFleetPanel.collectAsStateWithLifecycle()

    if (!authState.isLoggedIn) {
        LoginScreen(
            authState = authState,
            onDriverNameChanged = viewModel::onDriverNameChanged,
            onBadgeChanged = viewModel::onBadgeChanged,
            onLicenseChanged = viewModel::onLicenseChanged,
            onPhoneChanged = viewModel::onPhoneChanged,
            onUnitChanged = viewModel::onUnitChanged,
            onStationChanged = viewModel::onStationChanged,
            onVehiclePlateChanged = viewModel::onVehiclePlateChanged,
            onPinChanged = viewModel::onPinChanged,
            onLoginClick = viewModel::login,
            onQuickProfileSelect = viewModel::quickLoginDemo
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_app_scaffold"),
            containerColor = HDBackground,
            topBar = {
                Column {
                    TopSyncAndPriorityBar(
                        isOnline = isOnline,
                        isSimulatedOffline = isSimulatedOffline,
                        isSyncing = isSyncing,
                        pendingSyncCount = pendingSyncCount,
                        isSirenOn = locationData.isEmergencySirenOn,
                        unitId = profile?.unitId ?: "MEDIC-402",
                        driverName = profile?.driverName ?: "Officer J. Dawson",
                        onToggleOfflineSimulation = viewModel::toggleSimulatedOffline,
                        onForceSync = viewModel::forceSyncNow,
                        onToggleSiren = viewModel::toggleEmergencySiren,
                        onTriggerTestAlert = viewModel::triggerSimulatedEmergencyDispatch
                    )

                    // Sticky High Density Emergency Priority Banner
                    val topPendingCritical = alerts.firstOrNull { it.status == AlertStatus.PENDING }
                    AnimatedVisibility(
                        visible = topPendingCritical != null && selectedTab != AppNavTab.DISPATCH_DASHBOARD,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        if (topPendingCritical != null) {
                            EmergencyPriorityBanner(
                                alert = topPendingCritical,
                                onAccept = { viewModel.acceptEmergencyAlert(topPendingCritical) },
                                onViewDetails = {
                                    viewModel.selectTab(AppNavTab.DISPATCH_DASHBOARD)
                                    viewModel.selectAlertForDetails(topPendingCritical)
                                }
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = HDNavBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(1.dp, HDBorder)
                        .testTag("bottom_nav_bar")
                ) {
                    // 1. Dispatch Radar
                    NavigationBarItem(
                        selected = selectedTab == AppNavTab.DISPATCH_DASHBOARD,
                        onClick = { viewModel.selectTab(AppNavTab.DISPATCH_DASHBOARD) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unacceptedCount > 0) {
                                        Badge(containerColor = HDEmergencyRed) {
                                            Text(
                                                text = "$unacceptedCount",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = "Radar",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Home",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppNavTab.DISPATCH_DASHBOARD) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HDNavyDark,
                            selectedTextColor = HDNavyDark,
                            indicatorColor = HDBlueContainer,
                            unselectedIconColor = HDTextMuted,
                            unselectedTextColor = HDTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_radar")
                    )

                    // 2. GPS Navigation
                    NavigationBarItem(
                        selected = selectedTab == AppNavTab.GPS_MAP,
                        onClick = { viewModel.selectTab(AppNavTab.GPS_MAP) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "GPS Nav",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "GPS Nav",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppNavTab.GPS_MAP) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HDNavyDark,
                            selectedTextColor = HDNavyDark,
                            indicatorColor = HDBlueContainer,
                            unselectedIconColor = HDTextMuted,
                            unselectedTextColor = HDTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_gps")
                    )

                    // 3. Schedule Panel
                    NavigationBarItem(
                        selected = selectedTab == AppNavTab.SCHEDULED_TRIPS,
                        onClick = { viewModel.selectTab(AppNavTab.SCHEDULED_TRIPS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Schedule",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Schedule",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppNavTab.SCHEDULED_TRIPS) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HDNavyDark,
                            selectedTextColor = HDNavyDark,
                            indicatorColor = HDBlueContainer,
                            unselectedIconColor = HDTextMuted,
                            unselectedTextColor = HDTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_schedule")
                    )

                    // 4. Driver Profile
                    NavigationBarItem(
                        selected = selectedTab == AppNavTab.DRIVER_PROFILE,
                        onClick = { viewModel.selectTab(AppNavTab.DRIVER_PROFILE) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Profile",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == AppNavTab.DRIVER_PROFILE) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HDNavyDark,
                            selectedTextColor = HDNavyDark,
                            indicatorColor = HDBlueContainer,
                            unselectedIconColor = HDTextMuted,
                            unselectedTextColor = HDTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(HDBackground)
            ) {
                when (selectedTab) {
                    AppNavTab.DISPATCH_DASHBOARD -> {
                        RequestDashboardScreen(
                            alerts = alerts,
                            activeEmergencyAlert = activeEmergencyAlert,
                            onAcceptAlert = viewModel::acceptEmergencyAlert,
                            onAdvanceAlertStep = viewModel::advanceAlertStep,
                            onDeclineAlert = viewModel::declineAlert,
                            onSelectAlertForDetails = viewModel::selectAlertForDetails,
                            selectedAlertForDetails = selectedAlertForDetails,
                            onNavigateToMap = { viewModel.selectTab(AppNavTab.GPS_MAP) }
                        )
                    }

                    AppNavTab.GPS_MAP -> {
                        GpsNavigationScreen(
                            locationData = locationData,
                            nearbyHospitals = viewModel.nearbyHospitals,
                            onToggleSiren = viewModel::toggleEmergencySiren,
                            onRouteToHospital = viewModel::routeToHospital
                        )
                    }

                    AppNavTab.SCHEDULED_TRIPS -> {
                        SchedulePanelScreen(
                            trips = trips,
                            activeEmergencyAlert = activeEmergencyAlert,
                            onStartTrip = viewModel::startScheduledTrip,
                            onAdvanceTripStatus = viewModel::advanceTripStatus,
                            onViewEmergencyDashboard = { viewModel.selectTab(AppNavTab.DISPATCH_DASHBOARD) }
                        )
                    }

                    AppNavTab.DRIVER_PROFILE -> {
                        ProfilePanelScreen(
                            profile = profile,
                            showEditDialog = showProfileEditDialog,
                            showCompanyFleetPanel = showCompanyFleetPanel,
                            onOpenEditDialog = viewModel::openProfileEditor,
                            onCloseEditDialog = viewModel::closeProfileEditor,
                            onOpenCompanyFleetPanel = viewModel::openCompanyFleetPanel,
                            onCloseCompanyFleetPanel = viewModel::closeCompanyFleetPanel,
                            onUpdateProfile = viewModel::updateProfile,
                            onUpdateHealthScore = viewModel::updateAmbulanceHealthScore,
                            onToggleShiftStatus = viewModel::toggleShiftDutyStatus,
                            onLogout = viewModel::logout
                        )
                    }
                }
            }
        }
    }
}
