package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.HDBackground
import com.example.ui.theme.HDBlueContainer
import com.example.ui.theme.HDBlueLightBg
import com.example.ui.theme.HDBluePrimary
import com.example.ui.theme.HDBorder
import com.example.ui.theme.HDChipBg
import com.example.ui.theme.HDEmergencyContainer
import com.example.ui.theme.HDEmergencyRed
import com.example.ui.theme.HDEmergencyText
import com.example.ui.theme.HDNavyDark
import com.example.ui.theme.HDSurface
import com.example.ui.theme.HDTextMuted
import com.example.ui.theme.HDTextPrimary
import com.example.ui.theme.HDTextSecondary
import com.example.ui.theme.VitalGreen
import com.example.ui.theme.VitalGreenDark
import com.example.ui.viewmodel.AuthState

@Composable
fun LoginScreen(
    authState: AuthState,
    onDriverNameChanged: (String) -> Unit,
    onBadgeChanged: (String) -> Unit,
    onLicenseChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onUnitChanged: (String) -> Unit,
    onStationChanged: (String) -> Unit,
    onVehiclePlateChanged: (String) -> Unit,
    onPinChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onQuickProfileSelect: (
        name: String,
        badge: String,
        license: String,
        phone: String,
        unit: String,
        station: String,
        plate: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        color = HDBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // High Density Medical Dispatch Badge & Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(HDBlueLightBg)
                    .border(2.dp, HDBlueContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "AmbuDispatch Portal",
                    tint = HDBluePrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "CAD DISPATCH & FLEET GATEWAY",
                color = HDTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Driver & Ambulance Login",
                color = HDNavyDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Real-time Database Broadcast & Sync Notification
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F4FD))
                    .border(1.dp, Color(0xFFBEDCFE), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = HDBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Synchronized database stores driver & unit data for companion & patient apps.",
                        color = HDNavyDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 1: Paramedic & Driver Credentials
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HDSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder)
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
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = HDBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1. DRIVER CREDENTIALS",
                                color = HDNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE6F7F0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "REQUIRED",
                                color = VitalGreenDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Full Name
                    OutlinedTextField(
                        value = authState.driverNameInput,
                        onValueChange = onDriverNameChanged,
                        label = { Text("Paramedic Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = HDBluePrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_driver_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Badge ID & Paramedic License in Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = authState.badgeInput,
                            onValueChange = onBadgeChanged,
                            label = { Text("Badge ID") },
                            placeholder = { Text("MED-9042") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("login_badge_input")
                        )

                        OutlinedTextField(
                            value = authState.licenseInput,
                            onValueChange = onLicenseChanged,
                            label = { Text("State License") },
                            placeholder = { Text("CA-EMS-994120") },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedFieldColors(),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("login_license_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = authState.phoneInput,
                        onValueChange = onPhoneChanged,
                        label = { Text("Driver Contact Phone") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = HDBluePrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_phone_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card 2: Ambulance Vehicle & Station Dispatch Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HDSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder)
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
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = HDBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. AMBULANCE & DISPATCH UNIT",
                                color = HDNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HDBlueLightBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MICU / ALS",
                                color = HDNavyDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Unit Call Sign
                    OutlinedTextField(
                        value = authState.unitInput,
                        onValueChange = onUnitChanged,
                        label = { Text("Ambulance Unit / Call Sign") },
                        placeholder = { Text("MEDIC-402 (MICU)") },
                        leadingIcon = {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = HDBluePrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_unit_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Station / Trauma Hub
                    OutlinedTextField(
                        value = authState.stationInput,
                        onValueChange = onStationChanged,
                        label = { Text("Station / Base Trauma Hub") },
                        placeholder = { Text("Station 12 - Metro Trauma Hub") },
                        leadingIcon = {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = HDBluePrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_station_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vehicle Plate & Security PIN in Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = authState.vehiclePlateInput,
                            onValueChange = onVehiclePlateChanged,
                            label = { Text("Vehicle Plate/VIN") },
                            placeholder = { Text("CA-EMS-402") },
                            leadingIcon = {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = HDBluePrimary)
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedFieldColors(),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("login_plate_input")
                        )

                        OutlinedTextField(
                            value = authState.pinInput,
                            onValueChange = onPinChanged,
                            label = { Text("Dispatch PIN") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = HDBluePrimary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = outlinedFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("login_pin_input")
                        )
                    }
                }
            }

            // Error display
            if (authState.loginError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HDEmergencyContainer)
                        .border(1.dp, HDEmergencyRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "⚠️ ${authState.loginError}",
                        color = HDEmergencyRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Action Button: Verify & Open Driver Portal
            Button(
                onClick = onLoginClick,
                enabled = !authState.isVerifying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HDBluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("login_submit_btn")
            ) {
                if (authState.isVerifying) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VERIFYING CREDENTIALS...", fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VERIFY & OPEN DRIVER PORTAL",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Demo Profiles Selection
            Text(
                text = "OR FAST-LOAD ACTIVE DUTY CREW",
                color = HDTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DemoDriverCard(
                    name = "Capt. Marcus Vance, NRP",
                    badge = "MED-9042",
                    unit = "MEDIC-402 (MICU)",
                    station = "Station 12 - Metro Trauma Hub",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onQuickProfileSelect(
                            "Capt. Marcus Vance, NRP",
                            "MED-9042",
                            "CA-EMS-994120",
                            "+1 (555) 392-8811",
                            "MEDIC-402 (MICU)",
                            "Station 12 - Metro Trauma Hub",
                            "CA-EMS-402"
                        )
                    }
                )

                DemoDriverCard(
                    name = "Lt. Elena Rossi, EMT-P",
                    badge = "MED-7712",
                    unit = "RESCUE-18 (Trauma)",
                    station = "Station 4 - Central EMS Base",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onQuickProfileSelect(
                            "Lt. Elena Rossi, EMT-P",
                            "MED-7712",
                            "CA-EMS-881903",
                            "+1 (555) 441-2090",
                            "RESCUE-18 (Trauma)",
                            "Station 4 - Central EMS Base",
                            "CA-EMS-771"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = HDTextPrimary,
    unfocusedTextColor = HDTextPrimary,
    focusedBorderColor = HDBluePrimary,
    unfocusedBorderColor = HDBorder,
    focusedLabelColor = HDBluePrimary,
    unfocusedLabelColor = HDTextSecondary
)

@Composable
private fun DemoDriverCard(
    name: String,
    badge: String,
    unit: String,
    station: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("demo_profile_${badge}"),
        colors = CardDefaults.cardColors(containerColor = HDSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(VitalGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badge,
                        color = HDBluePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VitalGreen,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                color = HDNavyDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = unit,
                color = HDTextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
            Text(
                text = station,
                color = HDTextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}
