package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.GpsLocationData
import com.example.location.HospitalPOI
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
fun GpsNavigationScreen(
    locationData: GpsLocationData,
    nearbyHospitals: List<HospitalPOI>,
    onToggleSiren: () -> Unit,
    onRouteToHospital: (HospitalPOI) -> Unit,
    modifier: Modifier = Modifier
) {
    var isNightHudMode by remember { mutableStateOf(true) }
    var is3dPerspective by remember { mutableStateOf(false) }
    var zoomFactor by remember { mutableStateOf(1.0f) }
    var isGreenWaveActive by remember { mutableStateOf(true) }

    // Pulsing animations for emergency strobe & radar waves
    val infiniteTransition = rememberInfiniteTransition(label = "gps_nav_radar")
    val pulseRadarRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_radius"
    )
    val pulseRadarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_alpha"
    )
    val strobeColorLeft by infiniteTransition.animateColor(
        initialValue = Color(0xFFFF1744),
        targetValue = Color(0xFF00E5FF),
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe_left"
    )
    val strobeColorRight by infiniteTransition.animateColor(
        initialValue = Color(0xFF00E5FF),
        targetValue = Color(0xFFFF1744),
        animationSpec = infiniteRepeatable(
            animation = tween(220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe_right"
    )

    val mapBgColor = if (isNightHudMode) Color(0xFF0B131E) else Color(0xFFE3E7EE)
    val roadGridColor = if (isNightHudMode) Color(0xFF1B2A3D) else Color(0xFFD3DAE5)
    val mainArteryColor = if (isNightHudMode) Color(0xFF26394F) else Color(0xFFC0CAD9)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("gps_navigation_screen"),
        color = HDBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Live Siren & Strobe Bar when sirens are active
            if (locationData.isEmergencySirenOn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.5.dp, strobeColorLeft, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(strobeColorLeft)
                        )
                        Text(
                            text = "🚨 CODE-3 EVOC PRIORITY: SIRENS & LIGHTS ENGAGED",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(strobeColorRight)
                        )
                    }
                }
            }

            // Top Maneuver Cockpit Banner (Turn-by-Turn Medical Navigation)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (locationData.isEmergencySirenOn) HDEmergencyRed else HDBlueContainer,
                        RoundedCornerShape(22.dp)
                    )
                    .testTag("turn_guidance_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (locationData.isEmergencySirenOn) Color(0xFFFFF0F2) else HDSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Turn maneuver icon badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (locationData.isEmergencySirenOn) HDEmergencyRed else HDBluePrimary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TurnRight,
                            contentDescription = "Nav Direction",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "IN 350 METERS",
                                color = if (locationData.isEmergencySirenOn) HDEmergencyRed else HDBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isGreenWaveActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE6F7F0))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "⚡ GREEN WAVE",
                                        color = VitalGreenDark,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = locationData.currentNavInstruction,
                            color = HDNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                        Text(
                            text = "Onto ${locationData.streetName} → Trauma Corridor",
                            color = HDTextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    // Speedometer Dial / Gauge
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNightHudMode) Color(0xFF0F1A26) else HDBlueLightBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${locationData.speedKmh.toInt()}",
                                color = if (locationData.isEmergencySirenOn) HDEmergencyRed else HDNavyDark,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 22.sp
                            )
                            Text(
                                text = " km/h",
                                color = HDTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "EVOC EXEMPT",
                            color = VitalGreenDark,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Pro Medical GPS Map Canvas Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .border(1.5.dp, if (isNightHudMode) Color(0xFF2A3D54) else HDBorder, RoundedCornerShape(24.dp))
                    .testTag("gps_map_canvas_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = mapBgColor)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // 1. Draw City Block Grid (Urban Infrastructure)
                        drawCityBlocks(
                            w = w,
                            h = h,
                            blockColor = if (isNightHudMode) Color(0xFF132030) else Color(0xFFECF0F6),
                            roadColor = roadGridColor
                        )

                        // 2. Draw Major Arterial Highway Corridors
                        drawArterialHighways(
                            w = w,
                            h = h,
                            arteryColor = mainArteryColor
                        )

                        // 3. Draw Green-Wave Traffic Preemption Corridors
                        if (isGreenWaveActive) {
                            drawGreenWaveCorridor(
                                w = w,
                                h = h
                            )
                        }

                        // 4. Draw Active Navigation Route Polyline (Glow + Core)
                        drawActiveRoutePath(
                            w = w,
                            h = h,
                            isEmergency = locationData.isEmergencySirenOn
                        )

                        // 5. Draw Destination Hospital Beacon with Pulsing Radar Rings
                        val destX = w * 0.82f
                        val destY = h * 0.24f
                        drawHospitalDestinationBeacon(
                            destX = destX,
                            destY = destY,
                            radarRadius = pulseRadarRadius,
                            radarAlpha = pulseRadarAlpha
                        )

                        // 6. Draw Ambulance Vehicle Marker with Siren Waves & Heading
                        val vehX = w * 0.38f
                        val vehY = h * 0.58f
                        drawAmbulanceVehicleMarker(
                            vehX = vehX,
                            vehY = vehY,
                            heading = locationData.heading,
                            isSirenOn = locationData.isEmergencySirenOn,
                            strobeColor = strobeColorLeft
                        )
                    }

                    // Top-Left Tactical HUD Map Controls
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Night HUD / Daylight Mode Toggle
                        MapMiniPillButton(
                            icon = if (isNightHudMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            label = if (isNightHudMode) "DAY" else "NIGHT",
                            isNight = isNightHudMode,
                            onClick = { isNightHudMode = !isNightHudMode }
                        )

                        // 2D / 3D Perspective Toggle
                        MapMiniPillButton(
                            icon = Icons.Default.Explore,
                            label = if (is3dPerspective) "3D" else "2D",
                            isNight = isNightHudMode,
                            onClick = { is3dPerspective = !is3dPerspective }
                        )

                        // Green Wave Pre-emption Switch
                        MapMiniPillButton(
                            icon = Icons.Default.Traffic,
                            label = if (isGreenWaveActive) "WAVE ON" else "WAVE OFF",
                            isNight = isNightHudMode,
                            highlightColor = if (isGreenWaveActive) VitalGreen else null,
                            onClick = { isGreenWaveActive = !isGreenWaveActive }
                        )
                    }

                    // Top-Right Map Controls: Re-Center & Zoom
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isNightHudMode) Color(0xFF162536).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f))
                                .border(1.dp, if (isNightHudMode) Color(0xFF334E6B) else HDBorder, CircleShape)
                                .clickable { zoomFactor = (zoomFactor + 0.2f).coerceAtMost(2.0f) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = if (isNightHudMode) Color.White else HDNavyDark, modifier = Modifier.size(18.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isNightHudMode) Color(0xFF162536).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f))
                                .border(1.dp, if (isNightHudMode) Color(0xFF334E6B) else HDBorder, CircleShape)
                                .clickable { zoomFactor = (zoomFactor - 0.2f).coerceAtLeast(0.6f) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = if (isNightHudMode) Color.White else HDNavyDark, modifier = Modifier.size(18.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(HDBluePrimary)
                                .clickable { /* Recenter */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Re-center", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Bottom Floating Route Telemetry Glass Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isNightHudMode) Color(0xFF0F1A28).copy(alpha = 0.92f)
                                else Color.White.copy(alpha = 0.94f)
                            )
                            .border(
                                1.dp,
                                if (isNightHudMode) Color(0xFF263C54) else Color.White,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TARGET: ${locationData.activeNavTargetName ?: "St. Jude Emergency Center"}",
                                    color = if (isNightHudMode) Color(0xFF90B4D8) else HDTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "ETA: ${locationData.etaMinutesRemaining} mins  •  ${locationData.distanceRemainingKm} km remaining",
                                    color = if (isNightHudMode) Color.White else HDNavyDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Button(
                                onClick = onToggleSiren,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (locationData.isEmergencySirenOn) HDEmergencyRed else HDBluePrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (locationData.isEmergencySirenOn) "SIREN ON" else "SIREN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Nearest Trauma Hospitals Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = HDBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEAREST TRAUMA CENTERS & ICU BEDS",
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "${nearbyHospitals.size} READY",
                    color = VitalGreenDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // High Density List of Emergency Trauma Hospitals
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nearbyHospitals, key = { it.name }) { hospital ->
                    HospitalCard(
                        hospital = hospital,
                        onRoute = { onRouteToHospital(hospital) }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Canvas Map Drawing Sub-routines
// -----------------------------------------------------------------------------

private fun DrawScope.drawCityBlocks(w: Float, h: Float, blockColor: Color, roadColor: Color) {
    val blockStepX = 50f
    val blockStepY = 42f

    for (x in 0..w.toInt() step blockStepX.toInt()) {
        for (y in 0..h.toInt() step blockStepY.toInt()) {
            drawRoundRect(
                color = blockColor,
                topLeft = Offset(x + 4f, y + 4f),
                size = Size(blockStepX - 8f, blockStepY - 8f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

private fun DrawScope.drawArterialHighways(w: Float, h: Float, arteryColor: Color) {
    // Diagonal Highway 1
    drawLine(
        color = arteryColor,
        start = Offset(0f, h * 0.75f),
        end = Offset(w, h * 0.20f),
        strokeWidth = 18f,
        cap = StrokeCap.Round
    )
    // Cross Avenue Highway 2
    drawLine(
        color = arteryColor,
        start = Offset(w * 0.25f, 0f),
        end = Offset(w * 0.70f, h),
        strokeWidth = 16f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawGreenWaveCorridor(w: Float, h: Float) {
    // Green glowing priority corridor along the route
    val greenWavePath = Path().apply {
        moveTo(w * 0.15f, h * 0.88f)
        lineTo(w * 0.38f, h * 0.58f)
        lineTo(w * 0.58f, h * 0.46f)
        lineTo(w * 0.82f, h * 0.24f)
    }

    drawPath(
        path = greenWavePath,
        color = VitalGreen.copy(alpha = 0.18f),
        style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawActiveRoutePath(w: Float, h: Float, isEmergency: Boolean) {
    val routePath = Path().apply {
        moveTo(w * 0.15f, h * 0.88f)
        lineTo(w * 0.38f, h * 0.58f)
        lineTo(w * 0.58f, h * 0.46f)
        lineTo(w * 0.82f, h * 0.24f)
    }

    // Outer neon glow
    val glowColor = if (isEmergency) Color(0xFFFF1744).copy(alpha = 0.35f) else Color(0xFF00E5FF).copy(alpha = 0.3f)
    drawPath(
        path = routePath,
        color = glowColor,
        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Inner sharp navigation polyline
    val coreColor = if (isEmergency) Color(0xFFFF1744) else Color(0xFF0066FF)
    drawPath(
        path = routePath,
        color = coreColor,
        style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Turn waypoints dots
    drawCircle(Color.White, radius = 4f, center = Offset(w * 0.58f, h * 0.46f))
    drawCircle(coreColor, radius = 2.5f, center = Offset(w * 0.58f, h * 0.46f))
}

private fun DrawScope.drawHospitalDestinationBeacon(
    destX: Float,
    destY: Float,
    radarRadius: Float,
    radarAlpha: Float
) {
    val center = Offset(destX, destY)

    // Expanding radar waves
    drawCircle(
        color = HDEmergencyRed.copy(alpha = radarAlpha * 0.5f),
        radius = radarRadius,
        center = center,
        style = Stroke(width = 2f)
    )

    // Destination Pin Base
    drawCircle(HDEmergencyRed, radius = 14f, center = center)
    drawCircle(Color.White, radius = 10f, center = center)

    // Red Cross on Hospital Pin
    drawLine(
        color = HDEmergencyRed,
        start = Offset(destX - 5f, destY),
        end = Offset(destX + 5f, destY),
        strokeWidth = 3f,
        cap = StrokeCap.Square
    )
    drawLine(
        color = HDEmergencyRed,
        start = Offset(destX, destY - 5f),
        end = Offset(destX, destY + 5f),
        strokeWidth = 3f,
        cap = StrokeCap.Square
    )
}

private fun DrawScope.drawAmbulanceVehicleMarker(
    vehX: Float,
    vehY: Float,
    heading: Float,
    isSirenOn: Boolean,
    strobeColor: Color
) {
    val center = Offset(vehX, vehY)

    // Siren halo wave if on
    if (isSirenOn) {
        drawCircle(
            color = strobeColor.copy(alpha = 0.35f),
            radius = 26f,
            center = center
        )
    }

    // Vehicle circle halo
    drawCircle(
        color = HDBluePrimary.copy(alpha = 0.25f),
        radius = 18f,
        center = center
    )

    // Solid vehicle core
    drawCircle(
        color = if (isSirenOn) strobeColor else HDBluePrimary,
        radius = 10f,
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = 4f,
        center = center
    )

    // Heading arrow triangle
    val arrowPath = Path().apply {
        moveTo(vehX, vehY - 16f)
        lineTo(vehX - 6f, vehY - 6f)
        lineTo(vehX + 6f, vehY - 6f)
        close()
    }
    drawPath(path = arrowPath, color = if (isSirenOn) strobeColor else HDBluePrimary, style = Fill)
}

// -----------------------------------------------------------------------------
// Supporting Composables
// -----------------------------------------------------------------------------

@Composable
private fun MapMiniPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isNight: Boolean,
    highlightColor: Color? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isNight) Color(0xFF162536).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f))
            .border(1.dp, if (isNight) Color(0xFF334E6B) else HDBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = highlightColor ?: if (isNight) Color.White else HDNavyDark,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = highlightColor ?: if (isNight) Color.White else HDNavyDark,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HospitalCard(
    hospital: HospitalPOI,
    onRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, HDBorder, RoundedCornerShape(18.dp))
            .testTag("hospital_card_${hospital.name.take(10)}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HDSurface)
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(HDBlueLightBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = hospital.traumaLevel,
                            color = HDBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ICU: ${hospital.icuCapacityPercent}% Capacity",
                        color = if (hospital.icuCapacityPercent > 85) AmberWarning else VitalGreenDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${hospital.availableBeds} ICU Beds Available",
                    color = HDTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hospital.name,
                        color = HDNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = hospital.specialty,
                        color = HDTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onRoute,
                    colors = ButtonDefaults.buttonColors(containerColor = HDBluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("route_hospital_btn_${hospital.name.take(6)}"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ROUTE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
