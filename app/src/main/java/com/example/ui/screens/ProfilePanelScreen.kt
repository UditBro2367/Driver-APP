package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.data.model.ShiftStatus
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.HDBackground
import com.example.ui.theme.HDBlueContainer
import com.example.ui.theme.HDBlueLightBg
import com.example.ui.theme.HDBluePrimary
import com.example.ui.theme.HDBorder
import com.example.ui.theme.HDChipBg
import com.example.ui.theme.HDEmergencyBorder
import com.example.ui.theme.HDEmergencyContainer
import com.example.ui.theme.HDEmergencyRed
import com.example.ui.theme.HDEmergencySubtext
import com.example.ui.theme.HDEmergencyText
import com.example.ui.theme.HDNavyDark
import com.example.ui.theme.HDSurface
import com.example.ui.theme.HDTextMuted
import com.example.ui.theme.HDTextPrimary
import com.example.ui.theme.HDTextSecondary
import com.example.ui.theme.VitalGreen
import com.example.ui.theme.VitalGreenDark

@Composable
fun ProfilePanelScreen(
    profile: DriverProfile?,
    showEditDialog: Boolean,
    showCompanyFleetPanel: Boolean,
    onOpenEditDialog: () -> Unit,
    onCloseEditDialog: () -> Unit,
    onOpenCompanyFleetPanel: () -> Unit,
    onCloseCompanyFleetPanel: () -> Unit,
    onUpdateProfile: (name: String, badge: String, unit: String, station: String, license: String, certs: String, phone: String) -> Unit,
    onUpdateHealthScore: (score: Int, grade: String, status: String, engine: Int, brakes: Int, battery: Int, sanitization: Int, inspector: String, notes: String) -> Unit,
    onToggleShiftStatus: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val current = profile ?: DriverProfile()

    // Edit Personal Credentials state
    var editName by remember(current.driverName) { mutableStateOf(current.driverName) }
    var editBadge by remember(current.badgeNumber) { mutableStateOf(current.badgeNumber) }
    var editUnit by remember(current.unitId) { mutableStateOf(current.unitId) }
    var editStation by remember(current.stationName) { mutableStateOf(current.stationName) }
    var editLicense by remember(current.paramedicLicense) { mutableStateOf(current.paramedicLicense) }
    var editCerts by remember(current.certifications) { mutableStateOf(current.certifications) }
    var editPhone by remember(current.phone) { mutableStateOf(current.phone) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_panel_screen"),
        color = HDBackground
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Driver Profile Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HDBorder, RoundedCornerShape(24.dp))
                        .testTag("driver_credential_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = HDSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(HDBlueLightBg)
                                        .border(1.dp, HDBlueContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = HDBluePrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = current.driverName,
                                        color = HDNavyDark,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp
                                    )
                                    Text(
                                        text = "Badge: ${current.badgeNumber} • ${current.unitId}",
                                        color = HDBluePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = current.stationName,
                                        color = HDTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onOpenEditDialog,
                                modifier = Modifier.testTag("edit_profile_btn")
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Credentials",
                                    tint = HDBluePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Professional Certifications & License
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(HDBlueLightBg)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "PARAMEDIC LICENSE: ${current.paramedicLicense}",
                                    color = HDNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "CERTIFICATIONS: ${current.certifications}",
                                    color = HDTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "DIRECT CONTACT: ${current.phone}",
                                    color = HDTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 1. DRIVER RATING CARD (Improved / Deproved from Backend)
            item {
                DriverRatingCard(
                    profile = current
                )
            }

            // 2. AMBULANCE HEALTH SCORE CARD (Managed by App Company Fleet Panel)
            item {
                AmbulanceHealthScoreCard(
                    profile = current,
                    onOpenCompanyPanel = onOpenCompanyFleetPanel
                )
            }

            // Shift Status & Duty Control
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HDSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE SESSION TELEMETRY",
                                color = HDTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (current.shiftStatus) {
                                            ShiftStatus.ON_DUTY_AVAILABLE -> HDBlueLightBg
                                            ShiftStatus.DISPATCHED_EMERGENCY -> HDEmergencyContainer
                                            else -> HDChipBg
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .border(
                                        1.dp,
                                        when (current.shiftStatus) {
                                            ShiftStatus.ON_DUTY_AVAILABLE -> HDBlueContainer
                                            ShiftStatus.DISPATCHED_EMERGENCY -> HDEmergencyBorder
                                            else -> HDBorder
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text(
                                    text = current.shiftStatus.name.replace("_", " "),
                                    color = when (current.shiftStatus) {
                                        ShiftStatus.ON_DUTY_AVAILABLE -> HDNavyDark
                                        ShiftStatus.DISPATCHED_EMERGENCY -> HDEmergencyText
                                        else -> HDTextMuted
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatPill(
                                title = "RUNS TODAY",
                                value = "${current.totalRunsToday}",
                                icon = Icons.Default.DirectionsCar,
                                color = HDBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                title = "AVG RESPONSE",
                                value = "${current.avgResponseMinutes}m",
                                icon = Icons.Default.Timer,
                                color = HDBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                title = "ACTIVE UNIT",
                                value = current.unitId.take(7),
                                icon = Icons.Default.Shield,
                                color = HDBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Toggle Shift Status Button
                        Button(
                            onClick = onToggleShiftStatus,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (current.shiftStatus == ShiftStatus.ON_DUTY_AVAILABLE) HDNavyDark else HDBluePrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("toggle_duty_status_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (current.shiftStatus == ShiftStatus.ON_DUTY_AVAILABLE) "SWITCH TO STANDBY (STATION)" else "SET AVAILABLE ON-DUTY",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Ambulance Vehicle Health & Supplies Checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HDSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "AMBULANCE SUPPLIES & GAUGES",
                            color = HDTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // O2 Tank PSI
                        VehicleSupplyRow(
                            icon = Icons.Default.Air,
                            title = "Main Oxygen Tank",
                            value = "${current.o2TankPsi} PSI",
                            statusColor = HDBluePrimary,
                            subtitle = if (current.o2TankPsi > 1500) "Optimal (Safe for 8h dispatch)" else "Refill Recommended"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fuel
                        VehicleSupplyRow(
                            icon = Icons.Default.LocalGasStation,
                            title = "Vehicle Fuel Level",
                            value = "${current.fuelPercent}%",
                            statusColor = if (current.fuelPercent > 40) HDBluePrimary else HDEmergencyRed,
                            subtitle = "Diesel Fleet Tank • Range ~380km"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mileage
                        VehicleSupplyRow(
                            icon = Icons.Default.Speed,
                            title = "Odometer Mileage",
                            value = "${current.vehicleMileage} mi",
                            statusColor = HDBluePrimary,
                            subtitle = "Daily vehicle pre-trip inspection verified"
                        )
                    }
                }
            }

            // Sign Out / End Shift
            item {
                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HDEmergencyRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HDEmergencyBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("profile_logout_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("END SHIFT & SIGN OUT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Modal Dialog: Update Driver Credentials
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = onCloseEditDialog,
            containerColor = HDSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Update Driver Credentials",
                    color = HDNavyDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Driver / Officer Name") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HDTextPrimary,
                            unfocusedTextColor = HDTextPrimary,
                            focusedBorderColor = HDBluePrimary,
                            unfocusedBorderColor = HDBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBadge,
                        onValueChange = { editBadge = it },
                        label = { Text("Badge Number") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HDTextPrimary,
                            unfocusedTextColor = HDTextPrimary,
                            focusedBorderColor = HDBluePrimary,
                            unfocusedBorderColor = HDBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editUnit,
                        onValueChange = { editUnit = it },
                        label = { Text("Assigned Ambulance Unit") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HDTextPrimary,
                            unfocusedTextColor = HDTextPrimary,
                            focusedBorderColor = HDBluePrimary,
                            unfocusedBorderColor = HDBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editLicense,
                        onValueChange = { editLicense = it },
                        label = { Text("Paramedic License #") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HDTextPrimary,
                            unfocusedTextColor = HDTextPrimary,
                            focusedBorderColor = HDBluePrimary,
                            unfocusedBorderColor = HDBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Direct Phone #") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HDTextPrimary,
                            unfocusedTextColor = HDTextPrimary,
                            focusedBorderColor = HDBluePrimary,
                            unfocusedBorderColor = HDBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(
                            editName,
                            editBadge,
                            editUnit,
                            editStation,
                            editLicense,
                            editCerts,
                            editPhone
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HDBluePrimary)
                ) {
                    Text("SAVE CHANGES", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseEditDialog) {
                    Text("CANCEL", color = HDTextSecondary)
                }
            }
        )
    }

    // Modal Dialog: App Company Fleet Panel (Upload / Edit Ambulance Health Score)
    if (showCompanyFleetPanel) {
        CompanyFleetHealthEditorDialog(
            currentProfile = current,
            onDismiss = onCloseCompanyFleetPanel,
            onSave = onUpdateHealthScore
        )
    }
}

// -----------------------------------------------------------------------------
// Driver Performance & Rating Card with Dynamic Backend Improved/Deproved Trends
// -----------------------------------------------------------------------------
@Composable
fun DriverRatingCard(
    profile: DriverProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HDBorder, RoundedCornerShape(24.dp))
            .testTag("driver_rating_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HDSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Driver Rating",
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DRIVER RATING & CAD EVALUATION",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Reviews count pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HDBlueLightBg)
                        .border(1.dp, HDBlueContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${profile.totalReviewsCount} CAD Reviews",
                        color = HDNavyDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating Score & Dynamic Backend Trend Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${profile.driverRating}",
                        color = HDNavyDark,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = " / 5.0",
                        color = HDTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp, start = 3.dp)
                    )
                }

                // Dynamic Trend Pill (Improved vs Deproved from Backend)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (profile.isRatingImproved) Color(0xFFE6F7F0)
                            else HDEmergencyContainer
                        )
                        .border(
                            1.dp,
                            if (profile.isRatingImproved) Color(0xFFA3E6C8)
                            else HDEmergencyBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("driver_rating_trend_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.isRatingImproved) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (profile.isRatingImproved) VitalGreenDark else HDEmergencyRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (profile.isRatingImproved)
                                "IMPROVED (+${profile.ratingDelta})"
                            else "DEPROVED (-${profile.ratingDelta})",
                            color = if (profile.isRatingImproved) VitalGreenDark else HDEmergencyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-category Metric Breakdown Bars
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                RatingMetricRow(
                    label = "Safe Emergency Driving & EVOC",
                    score = profile.ratingCategorySafety,
                    barColor = HDBluePrimary
                )
                RatingMetricRow(
                    label = "CAD Response Punctuality",
                    score = profile.ratingCategoryPunctuality,
                    barColor = VitalGreen
                )
                RatingMetricRow(
                    label = "Patient Care & Hospital Handover",
                    score = profile.ratingCategoryPatientCare,
                    barColor = AmberWarning
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Backend CAD evaluation message quote
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HDChipBg)
                    .padding(10.dp)
            ) {
                Text(
                    text = "📡 ${profile.backendRatingSyncMessage}",
                    color = HDTextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Ambulance Vehicle Health Score Card (Managed by App Company Fleet Panel)
// -----------------------------------------------------------------------------
@Composable
fun AmbulanceHealthScoreCard(
    profile: DriverProfile,
    onOpenCompanyPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = profile.vehicleHealthScore
    val isGoodHealth = score >= 80
    val scoreColor = if (score >= 85) VitalGreen else if (score >= 70) AmberWarning else HDEmergencyRed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HDBorder, RoundedCornerShape(24.dp))
            .testTag("ambulance_health_score_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HDSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Company Fleet Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Health Score",
                        tint = HDBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AMBULANCE FLEET HEALTH SCORE",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HDBlueContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "App Company Panel",
                        color = HDNavyDark,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Score Dial & Status Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$score",
                        color = scoreColor,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 34.sp
                    )
                    Text(
                        text = " / 100",
                        color = HDTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp, start = 3.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isGoodHealth) HDBlueLightBg else HDEmergencyContainer)
                        .border(
                            1.dp,
                            if (isGoodHealth) HDBlueContainer else HDEmergencyBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = profile.fleetInspectionStatus,
                        color = if (isGoodHealth) HDNavyDark else HDEmergencyText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Grade: ${profile.vehicleHealthGrade}",
                color = HDNavyDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subsystem Diagnostic Health Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubsystemHealthMiniBadge(
                    icon = Icons.Default.DirectionsCar,
                    name = "Engine",
                    percent = profile.engineHealthPercent,
                    modifier = Modifier.weight(1f)
                )
                SubsystemHealthMiniBadge(
                    icon = Icons.Default.Build,
                    name = "Brakes",
                    percent = profile.brakesHealthPercent,
                    modifier = Modifier.weight(1f)
                )
                SubsystemHealthMiniBadge(
                    icon = Icons.Default.BatteryChargingFull,
                    name = "Power",
                    percent = profile.batteryInverterHealthPercent,
                    modifier = Modifier.weight(1f)
                )
                SubsystemHealthMiniBadge(
                    icon = Icons.Default.CleaningServices,
                    name = "Sanitized",
                    percent = profile.medicalSanitizationPercent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inspector ID & Company Fleet Notes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HDBlueLightBg)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "🛠️ Inspector: ${profile.lastFleetInspector} • ${profile.lastInspectionDate}",
                        color = HDNavyDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Notes: \"${profile.companyFleetNotes}\"",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Company Panel Upload & Edit Button
            Button(
                onClick = onOpenCompanyPanel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HDNavyDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("open_company_fleet_panel_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APP COMPANY FLEET PANEL (EDIT & UPLOAD)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SubsystemHealthMiniBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    percent: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HDChipBg)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = HDBluePrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = name, color = HDTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "$percent%",
                color = if (percent >= 85) HDNavyDark else AmberWarning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun RatingMetricRow(
    label: String,
    score: Int,
    barColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = HDTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(text = "$score%", color = HDNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = HDChipBg
        )
    }
}

// -----------------------------------------------------------------------------
// Company Fleet Panel Modal: Edit & Upload Vehicle Health Score & Maintenance
// -----------------------------------------------------------------------------
@Composable
fun CompanyFleetHealthEditorDialog(
    currentProfile: DriverProfile,
    onDismiss: () -> Unit,
    onSave: (score: Int, grade: String, status: String, engine: Int, brakes: Int, battery: Int, sanitization: Int, inspector: String, notes: String) -> Unit
) {
    var healthScore by remember { mutableFloatStateOf(currentProfile.vehicleHealthScore.toFloat()) }
    var enginePercent by remember { mutableFloatStateOf(currentProfile.engineHealthPercent.toFloat()) }
    var brakesPercent by remember { mutableFloatStateOf(currentProfile.brakesHealthPercent.toFloat()) }
    var batteryPercent by remember { mutableFloatStateOf(currentProfile.batteryInverterHealthPercent.toFloat()) }
    var sanitizationPercent by remember { mutableFloatStateOf(currentProfile.medicalSanitizationPercent.toFloat()) }

    var statusText by remember { mutableStateOf(currentProfile.fleetInspectionStatus) }
    var inspectorName by remember { mutableStateOf(currentProfile.lastFleetInspector) }
    var fleetNotes by remember { mutableStateOf(currentProfile.companyFleetNotes) }

    val computedGrade = when {
        healthScore >= 95 -> "A+ (Excellent Fleet Condition)"
        healthScore >= 85 -> "A (Certified Active Roadworthy)"
        healthScore >= 70 -> "B (Minor Maintenance Advised)"
        else -> "C (Grounded for Shop Overhaul)"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HDSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(HDNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Engineering,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "App Company Fleet Panel",
                        color = HDNavyDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Upload Vehicle Health Score & Telemetry",
                        color = HDTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Health Score Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Overall Health Score:", color = HDNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${healthScore.toInt()} / 100", color = HDBluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = healthScore,
                        onValueChange = { healthScore = it },
                        valueRange = 40f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = HDBluePrimary,
                            activeTrackColor = HDBluePrimary,
                            inactiveTrackColor = HDChipBg
                        ),
                        modifier = Modifier.testTag("health_score_slider")
                    )
                    Text(
                        text = "Calculated Grade: $computedGrade",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider(color = HDBorder)

                // Subsystem Sliders
                Text("Subsystem Health Calibration:", color = HDTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Engine & Transmission: ${enginePercent.toInt()}%", fontSize = 11.sp, color = HDTextPrimary)
                    Text("Brakes: ${brakesPercent.toInt()}%", fontSize = 11.sp, color = HDTextPrimary)
                }
                Slider(
                    value = enginePercent,
                    onValueChange = { enginePercent = it },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = HDBluePrimary, activeTrackColor = HDBluePrimary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Medical Inverter: ${batteryPercent.toInt()}%", fontSize = 11.sp, color = HDTextPrimary)
                    Text("Bio-Sanitization: ${sanitizationPercent.toInt()}%", fontSize = 11.sp, color = HDTextPrimary)
                }
                Slider(
                    value = batteryPercent,
                    onValueChange = { batteryPercent = it },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = HDBluePrimary, activeTrackColor = HDBluePrimary)
                )

                // Status selection preset buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val statuses = listOf("Certified Roadworthy", "Maintenance Due", "Grounded")
                    statuses.forEach { st ->
                        val isSelected = statusText.contains(st, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) HDBluePrimary else HDChipBg)
                                .clickable { statusText = st }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = st.take(12),
                                color = if (isSelected) Color.White else HDTextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inspectorName,
                    onValueChange = { inspectorName = it },
                    label = { Text("Company Fleet Inspector ID") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HDTextPrimary,
                        unfocusedTextColor = HDTextPrimary,
                        focusedBorderColor = HDBluePrimary,
                        unfocusedBorderColor = HDBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fleetNotes,
                    onValueChange = { fleetNotes = it },
                    label = { Text("Company Maintenance Log Remarks") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HDTextPrimary,
                        unfocusedTextColor = HDTextPrimary,
                        focusedBorderColor = HDBluePrimary,
                        unfocusedBorderColor = HDBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        healthScore.toInt(),
                        computedGrade,
                        statusText,
                        enginePercent.toInt(),
                        brakesPercent.toInt(),
                        batteryPercent.toInt(),
                        sanitizationPercent.toInt(),
                        inspectorName,
                        fleetNotes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = HDNavyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_fleet_health_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("UPLOAD HEALTH SCORE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = HDTextSecondary)
            }
        }
    )
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(HDBlueLightBg)
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, color = HDTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = HDNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun VehicleSupplyRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    statusColor: Color,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HDBlueLightBg)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(HDBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = HDBluePrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = HDNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = HDTextSecondary, fontSize = 10.sp)
            }
        }

        Text(text = value, color = HDBluePrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}
