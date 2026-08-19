package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertStatus
import com.example.data.model.EmergencyAlert
import com.example.data.model.PriorityLevel
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
import com.example.ui.theme.HDSurfaceVariant
import com.example.ui.theme.HDTextMuted
import com.example.ui.theme.HDTextPrimary
import com.example.ui.theme.HDTextSecondary
import com.example.ui.theme.VitalGreen
import com.example.ui.theme.VitalGreenDark

@Composable
fun RequestDashboardScreen(
    alerts: List<EmergencyAlert>,
    activeEmergencyAlert: EmergencyAlert?,
    onAcceptAlert: (EmergencyAlert) -> Unit,
    onAdvanceAlertStep: (alertId: String, currentStatus: AlertStatus) -> Unit,
    onDeclineAlert: (alertId: String, reason: String) -> Unit,
    onSelectAlertForDetails: (EmergencyAlert?) -> Unit,
    selectedAlertForDetails: EmergencyAlert?,
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeclineDialogForAlertId by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("request_dashboard_screen"),
        color = HDBackground
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Emergency Radar Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DISPATCH RADAR",
                            color = HDNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Real-Time 911 Priority CAD Stream",
                            color = HDTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(HDEmergencyContainer)
                            .border(1.dp, HDEmergencyBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${alerts.count { it.status == AlertStatus.PENDING }} PENDING",
                            color = HDEmergencyRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Active In-Progress Mission Stepper (if driver accepted an alert)
            if (activeEmergencyAlert != null && activeEmergencyAlert.status != AlertStatus.PENDING && activeEmergencyAlert.status != AlertStatus.HANDOVER_COMPLETE && activeEmergencyAlert.status != AlertStatus.DECLINED) {
                item {
                    ActiveMissionStepCard(
                        alert = activeEmergencyAlert,
                        onAdvanceStep = {
                            onAdvanceAlertStep(activeEmergencyAlert.id, activeEmergencyAlert.status)
                        },
                        onNavigate = onNavigateToMap
                    )
                }
            }

            // High Density Emergency Alert Cards (Design style: #FFDAD6 container, #BA1A1A accent)
            val pendingAlerts = alerts.filter { it.status == AlertStatus.PENDING }
            if (pendingAlerts.isNotEmpty()) {
                item {
                    Text(
                        text = "HIGH-PRIORITY INCOMING ALERTS",
                        color = HDEmergencyRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                items(pendingAlerts, key = { it.id }) { alert ->
                    EmergencyAlertCard(
                        alert = alert,
                        onAccept = { onAcceptAlert(alert) },
                        onDecline = { showDeclineDialogForAlertId = alert.id },
                        onViewDetails = { onSelectAlertForDetails(alert) }
                    )
                }
            } else if (activeEmergencyAlert == null) {
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
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(HDBlueLightBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = HDBluePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Queue Clear • Unit Standing By",
                                color = HDNavyDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Scheduled transport tasks may proceed without interruption.",
                                color = HDTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Recent/Completed History Section
            val historicalAlerts = alerts.filter { it.status == AlertStatus.HANDOVER_COMPLETE || it.status == AlertStatus.DECLINED }
            if (historicalAlerts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "RECENT LOG",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(historicalAlerts, key = { it.id }) { alert ->
                    HistoricalAlertCard(alert = alert)
                }
            }
        }
    }

    // Modal Details Dialog
    if (selectedAlertForDetails != null) {
        AlertDetailsDialog(
            alert = selectedAlertForDetails,
            onDismiss = { onSelectAlertForDetails(null) },
            onAccept = {
                onAcceptAlert(selectedAlertForDetails)
                onSelectAlertForDetails(null)
            }
        )
    }

    // Decline Confirmation Dialog
    if (showDeclineDialogForAlertId != null) {
        val alertId = showDeclineDialogForAlertId!!
        AlertDialog(
            onDismissRequest = { showDeclineDialogForAlertId = null },
            title = { Text("Reroute / Decline Alert", color = HDNavyDark, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "State reason to CAD Central: Ambulance unit will remain available or reassigned.",
                    color = HDTextSecondary
                )
            },
            containerColor = HDSurface,
            confirmButton = {
                Button(
                    onClick = {
                        onDeclineAlert(alertId, "Unit re-routed by driver")
                        showDeclineDialogForAlertId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HDEmergencyRed)
                ) {
                    Text("Confirm Reroute", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialogForAlertId = null }) {
                    Text("Cancel", color = HDTextSecondary)
                }
            }
        )
    }
}

@Composable
fun EmergencyAlertCard(
    alert: EmergencyAlert,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Exact High Density Design Pattern
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, HDEmergencyBorder, RoundedCornerShape(24.dp))
            .testTag("alert_card_${alert.id}"),
        colors = CardDefaults.cardColors(containerColor = HDEmergencyContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Emergency Alert Title & Priority 1 Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HDEmergencyRed)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "EMERGENCY ALERT",
                        color = HDEmergencyText,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "PRIORITY 1",
                        color = HDEmergencyRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.title,
                color = HDEmergencyText,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${alert.distanceKm}km • ${alert.locationAddress}",
                color = HDEmergencySubtext,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Patient: ${alert.patientAgeGender} • ${alert.patientCondition}",
                color = HDEmergencyText.copy(alpha = 0.85f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HDEmergencyRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(44.dp)
                        .testTag("accept_alert_btn_${alert.id}")
                ) {
                    Text("ACCEPT NOW", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.45f),
                        contentColor = HDEmergencyText
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HDEmergencyRed.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("view_details_btn_${alert.id}")
                ) {
                    Text("DETAILS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onDecline,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HDEmergencySubtext),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HDEmergencyBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("decline_alert_btn_${alert.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Decline", tint = HDEmergencySubtext, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ActiveMissionStepCard(
    alert: EmergencyAlert,
    onAdvanceStep: () -> Unit,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        AlertStatus.ACCEPTED to "Dispatched",
        AlertStatus.EN_ROUTE to "En Route",
        AlertStatus.ON_SCENE to "On Scene",
        AlertStatus.PATIENT_LOADED to "Loaded",
        AlertStatus.TRANSPORTING to "Transport",
        AlertStatus.HANDOVER_COMPLETE to "Handover"
    )

    val currentStepIndex = steps.indexOfFirst { it.first == alert.status }.coerceAtLeast(0)
    val nextActionLabel = when (alert.status) {
        AlertStatus.ACCEPTED -> "CONFIRM EN ROUTE"
        AlertStatus.EN_ROUTE -> "MARK ARRIVED ON SCENE"
        AlertStatus.ON_SCENE -> "PATIENT SECURED & LOADED"
        AlertStatus.PATIENT_LOADED -> "COMMENCE TRANSPORT"
        AlertStatus.TRANSPORTING -> "COMPLETE HOSPITAL HANDOVER"
        else -> "MISSION COMPLETED"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, HDBluePrimary, RoundedCornerShape(24.dp))
            .testTag("active_mission_card"),
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(HDEmergencyRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE EMERGENCY MISSION",
                        color = HDBluePrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Button(
                    onClick = onNavigate,
                    colors = ButtonDefaults.buttonColors(containerColor = HDBluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(30.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ROUTE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = alert.title,
                color = HDNavyDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Target: ${alert.locationAddress}",
                color = HDTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step Indicator Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, pair ->
                    val isDone = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrent) HDEmergencyRed
                                    else if (isDone) HDBluePrimary
                                    else HDChipBg
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone && !isCurrent) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isDone || isCurrent) Color.White else HDTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = pair.second,
                            color = if (isCurrent) HDNavyDark else HDTextSecondary,
                            fontSize = 8.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Advance Step Action
            Button(
                onClick = onAdvanceStep,
                colors = ButtonDefaults.buttonColors(containerColor = HDNavyDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("advance_mission_step_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "▶  $nextActionLabel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HistoricalAlertCard(
    alert: EmergencyAlert,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = HDSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, HDBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${alert.incidentCode} • ${alert.title}",
                    color = HDNavyDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Hospital: ${alert.destinationHospital}",
                    color = HDTextSecondary,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (alert.status == AlertStatus.HANDOVER_COMPLETE) HDBlueLightBg
                        else HDChipBg
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (alert.status == AlertStatus.HANDOVER_COMPLETE) "HANDOVER COMPLETE" else "REROUTED",
                    color = if (alert.status == AlertStatus.HANDOVER_COMPLETE) HDBluePrimary else HDTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AlertDetailsDialog(
    alert: EmergencyAlert,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HDSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HDEmergencyRed)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = alert.incidentCode,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CAD Incident Briefing",
                    color = HDNavyDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = alert.title,
                    color = HDNavyDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )

                Text(
                    text = "Patient Condition:",
                    color = HDBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${alert.patientAgeGender} - ${alert.patientCondition}",
                    color = HDTextPrimary,
                    fontSize = 13.sp
                )

                Text(
                    text = "Caller / Gate Access Notes:",
                    color = HDBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = alert.callerNotes,
                    color = HDTextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "Location & Destination:",
                    color = HDBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "📍 Pickup: ${alert.locationAddress}\n🏥 Dropoff: ${alert.destinationHospital}",
                    color = HDTextSecondary,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            if (alert.status == AlertStatus.PENDING) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = HDEmergencyRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ACCEPT DISPATCH", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = HDTextSecondary)
            }
        }
    )
}
