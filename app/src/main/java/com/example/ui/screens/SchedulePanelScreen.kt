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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.ScheduledTrip
import com.example.data.model.TripStatus
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
fun SchedulePanelScreen(
    trips: List<ScheduledTrip>,
    activeEmergencyAlert: EmergencyAlert?,
    onStartTrip: (ScheduledTrip) -> Unit,
    onAdvanceTripStatus: (ScheduledTrip) -> Unit,
    onViewEmergencyDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmergencyBlocking = activeEmergencyAlert != null &&
            activeEmergencyAlert.status != AlertStatus.HANDOVER_COMPLETE &&
            activeEmergencyAlert.status != AlertStatus.DECLINED

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("schedule_panel_screen"),
        color = HDBackground
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SCHEDULED TRANSPORTS",
                            color = HDNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Planned Patient Transfers & Dialysis Runs",
                            color = HDTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(HDBlueLightBg)
                            .border(1.dp, HDBlueContainer, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${trips.count { it.status != TripStatus.COMPLETED }} QUEUED",
                            color = HDBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // CRITICAL PRIORITY WARNING: Emergency preemption banner
            if (isEmergencyBlocking) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, HDEmergencyBorder, RoundedCornerShape(24.dp))
                            .testTag("emergency_preemption_warning_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = HDEmergencyContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(HDEmergencyRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "SCHEDULED TRIPS PAUSED",
                                    color = HDEmergencyText,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Emergency Code Red dispatch takes 100% priority. Planned transports will resume once the active incident is resolved.",
                                color = HDEmergencySubtext,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onViewEmergencyDashboard,
                                colors = ButtonDefaults.buttonColors(containerColor = HDEmergencyRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .testTag("switch_to_emergency_btn")
                            ) {
                                Text(
                                    text = "SWITCH TO EMERGENCY RADAR",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Active In-Progress Scheduled Trip (if any)
            val activeTrip = trips.firstOrNull { it.status == TripStatus.EN_ROUTE_PICKUP || it.status == TripStatus.PATIENT_ABOARD || it.status == TripStatus.PAUSED_FOR_EMERGENCY }
            if (activeTrip != null) {
                item {
                    ActiveScheduledTripCard(
                        trip = activeTrip,
                        isEmergencyBlocking = isEmergencyBlocking,
                        onAdvance = { onAdvanceTripStatus(activeTrip) }
                    )
                }
            }

            // List of upcoming scheduled trips (Styled like High Density Card)
            val upcomingTrips = trips.filter { it.id != activeTrip?.id }
            if (upcomingTrips.isNotEmpty()) {
                item {
                    Text(
                        text = "UPCOMING SCHEDULED TASKS",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(upcomingTrips, key = { it.id }) { trip ->
                    ScheduledTripCard(
                        trip = trip,
                        isEmergencyBlocking = isEmergencyBlocking,
                        onStart = { onStartTrip(trip) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveScheduledTripCard(
    trip: ScheduledTrip,
    isEmergencyBlocking: Boolean,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusLabel = when (trip.status) {
        TripStatus.EN_ROUTE_PICKUP -> "EN ROUTE TO PICKUP"
        TripStatus.PATIENT_ABOARD -> "PATIENT ABOARD - EN ROUTE"
        TripStatus.PAUSED_FOR_EMERGENCY -> "PAUSED (EMERGENCY ACTIVE)"
        else -> "ACTIVE"
    }

    val actionButtonText = when (trip.status) {
        TripStatus.EN_ROUTE_PICKUP -> "MARK PATIENT LOADED"
        TripStatus.PATIENT_ABOARD -> "COMPLETE SCHEDULED TRANSPORT"
        TripStatus.PAUSED_FOR_EMERGENCY -> "RESUME SCHEDULED TRANSPORT"
        else -> "CONTINUE"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                if (trip.status == TripStatus.PAUSED_FOR_EMERGENCY) AmberWarning else HDBluePrimary,
                RoundedCornerShape(24.dp)
            )
            .testTag("active_scheduled_trip_card"),
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
                    Icon(
                        imageVector = if (trip.status == TripStatus.PAUSED_FOR_EMERGENCY) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (trip.status == TripStatus.PAUSED_FOR_EMERGENCY) AmberWarning else HDBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusLabel,
                        color = if (trip.status == TripStatus.PAUSED_FOR_EMERGENCY) AmberWarning else HDBluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = trip.tripNumber,
                    color = HDTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${trip.patientName} (${trip.patientAgeGender})",
                color = HDNavyDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = trip.transportReason,
                color = HDBluePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 Pickup: ${trip.pickupAddress}",
                color = HDTextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "🏥 Dropoff: ${trip.dropoffHospital}",
                color = HDTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = AmberWarning,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Equip: ${trip.equipmentRequired}",
                    color = AmberWarning,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAdvance,
                enabled = !isEmergencyBlocking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HDNavyDark,
                    disabledContainerColor = HDChipBg
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("advance_scheduled_trip_btn")
            ) {
                Text(
                    text = if (isEmergencyBlocking) "PAUSED (EMERGENCY ACTIVE)" else actionButtonText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isEmergencyBlocking) HDTextSecondary else Color.White
                )
            }
        }
    }
}

@Composable
fun ScheduledTripCard(
    trip: ScheduledTrip,
    isEmergencyBlocking: Boolean,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    // High Density Scheduled Task Card Pattern
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HDBorder, RoundedCornerShape(24.dp))
            .testTag("scheduled_trip_card_${trip.id}"),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Time Badge Container from High Density Design
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HDBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = trip.scheduledTimeFormatted.take(5),
                        color = HDNavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${trip.patientName} (${trip.patientAgeGender})",
                        color = HDTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${trip.pickupAddress} ➜ ${trip.dropoffHospital}",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (trip.status == TripStatus.COMPLETED) HDBlueLightBg
                            else HDChipBg
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (trip.status == TripStatus.COMPLETED) "COMPLETED" else "QUEUED",
                        color = if (trip.status == TripStatus.COMPLETED) HDBluePrimary else HDTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (trip.status == TripStatus.SCHEDULED) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onStart,
                    enabled = !isEmergencyBlocking,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HDBluePrimary,
                        disabledContainerColor = HDChipBg
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("start_scheduled_trip_btn_${trip.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEmergencyBlocking) "LOCKED (EMERGENCY RUN ACTIVE)" else "START TRANSPORT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isEmergencyBlocking) HDTextSecondary else Color.White
                        )
                    }
                }
            }
        }
    }
}
