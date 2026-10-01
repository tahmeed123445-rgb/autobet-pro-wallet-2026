package com.example

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.WalletViewModel
import kotlinx.coroutines.delay

@Composable
fun AccountVerificationScreen(
    viewModel: WalletViewModel,
    onApproved: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )

    // Countdown timer starting at 30 minutes (1800 seconds)
    var timeLeftSeconds by remember { mutableStateOf(1800) }

    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, goldBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Loading/Checking Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(goldBorder.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = goldBorder,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(50.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Pending",
                        tint = goldBorder,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Status Badge
                val status = currentUser?.status ?: "PENDING_APPROVAL"
                val statusColor = if (status == "APPROVED") Color(0xFF10B981) else if (status == "REJECTED") Color(0xFFEF4444) else Color(0xFFF59E0B)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Status: $status",
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (status == "REJECTED") {
                    Text(
                        text = "Your account registration was not approved.",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Back to Welcome", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    return@Card
                }

                // Urdu Prominent Messages
                Text(
                    text = "براہِ کرم انتظار کریں",
                    color = goldBorder,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "آپ کی درخواست کامیابی سے جمع ہو گئی ہے۔",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "آپ کے اکاؤنٹ کی تفصیلات BetPro Member Verification کے لیے Admin Panel کو بھیج دی گئی ہیں۔\n\nAdmin کی طرف سے Approval ملتے ہی آپ کا اکاؤنٹ Open کر دیا جائے گا۔\n\nاس عمل میں تقریباً 30 منٹ لگ सकते ہیں۔ براہِ کرم اس دوران انتظار کریں۔",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                HorizontalDivider(color = goldBorder.copy(alpha = 0.3f))

                // English Subtitle
                Text(
                    text = "Your account details have been sent to the Admin Panel for BetPro Member verification. Your account will be opened as soon as Admin approval is received.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Countdown Section
                Text(
                    text = "Estimated Verification Time",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = if (timeLeftSeconds > 0) timeFormatted else "00:00",
                    color = goldBorder,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )

                if (timeLeftSeconds == 0) {
                    Text(
                        text = "Verification is still in progress.\nPlease wait for Admin approval.",
                        color = Color(0xFFF59E0B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Check Account Status Button
                Button(
                    onClick = {
                        currentUser?.userId?.let { uid ->
                            viewModel.refreshUserStatus(uid) { updatedUser ->
                                if (updatedUser?.status == "APPROVED") {
                                    Toast.makeText(context, "Account Approved!", Toast.LENGTH_SHORT).show()
                                    onApproved()
                                } else {
                                    Toast.makeText(context, "Verification is still in progress. Please wait for Admin approval.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } ?: run {
                            Toast.makeText(context, "Please wait for Admin approval.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(goldGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Check", tint = Color.Black)
                            Text(
                                text = "Check Account Status",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                TextButton(onClick = onLogout) {
                    Text(text = "Log Out / Back", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}
