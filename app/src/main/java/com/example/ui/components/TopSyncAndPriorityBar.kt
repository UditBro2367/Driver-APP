package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.HDBackground
import com.example.ui.theme.HDBlueContainer
import com.example.ui.theme.HDBlueLightBg
import com.example.ui.theme.HDBluePrimary
import com.example.ui.theme.HDBorder
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

@Composable
fun TopSyncAndPriorityBar(
    isOnline: Boolean,
    isSimulatedOffline: Boolean,
    isSyncing: Boolean,
    pendingSyncCount: Int,
    isSirenOn: Boolean,
    unitId: String,
    driverName: String = "Officer J. Dawson",
    onToggleOfflineSimulation: () -> Unit,
    onForceSync: () -> Unit,
    onToggleSiren: () -> Unit,
    onTriggerTestAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HDSurface,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = HDBorder)
            .testTag("top_sync_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header Brand & Officer / Unit Info
                Column {
                    Text(
                        text = "AMBUDISPATCH v4.2 • $unitId",
                        color = HDTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = driverName,
                        color = HDNavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Sync & Online Status Section
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Online / Offline Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isOnline) HDBlueLightBg else HDEmergencyContainer)
                                .border(
                                    1.dp,
                                    if (isOnline) HDBlueContainer else HDEmergencyBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onToggleOfflineSimulation() }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("network_status_badge")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) HDBluePrimary else HDEmergencyRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    color = if (isOnline) HDNavyDark else HDEmergencyText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.3.sp
                                )
                                if (pendingSyncCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(HDEmergencyRed)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$pendingSyncCount",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }

                        // Force Sync button
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = HDBluePrimary,
                                strokeWidth = 2.dp
                            )
                        } else if (isOnline && pendingSyncCount > 0) {
                            IconButton(
                                onClick = onForceSync,
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("force_sync_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync Queue",
                                    tint = HDBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Siren Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSirenOn) HDEmergencyRed else HDBlueLightBg)
                                .border(
                                    1.dp,
                                    if (isSirenOn) HDEmergencyBorder else HDBlueContainer,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onToggleSiren() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("siren_toggle_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Siren",
                                    tint = if (isSirenOn) Color.White else HDBluePrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isSirenOn) "SIREN" else "SIREN",
                                    color = if (isSirenOn) Color.White else HDNavyDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // 911 SIM button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(HDSurface)
                                .border(1.dp, HDBorder, RoundedCornerShape(14.dp))
                                .clickable { onTriggerTestAlert() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("trigger_test_alert_button")
                        ) {
                            Text(
                                text = "+ 911 SIM",
                                color = HDBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSyncing) "Syncing..." else "Last Sync: Just Now",
                        color = HDTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyPriorityBanner(
    alert: EmergencyAlert,
    onAccept: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .border(1.dp, HDEmergencyBorder, RoundedCornerShape(24.dp))
            .testTag("emergency_priority_banner"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HDEmergencyContainer)
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
                fontSize = 20.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${alert.distanceKm}km • ${alert.locationAddress}",
                color = HDEmergencySubtext,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (alert.status == AlertStatus.PENDING) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HDEmergencyRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("accept_emergency_banner_btn")
                    ) {
                        Text("ACCEPT NOW", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
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
                        .testTag("view_details_banner_btn")
                ) {
                    Text("DETAILS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
