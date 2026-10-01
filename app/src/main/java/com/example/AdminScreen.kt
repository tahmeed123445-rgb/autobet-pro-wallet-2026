package com.example

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.WalletViewModel

@Composable
fun AdminScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )
    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)

    val depositRequests by viewModel.allRequests.collectAsState()
    val withdrawalRequests by viewModel.allWithdrawals.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val quickOptions by viewModel.allQuickOptions.collectAsState()
    val paymentMethods by viewModel.allPaymentMethods.collectAsState()
    val banners by viewModel.allBanners.collectAsState()
    val settings by viewModel.allSettings.collectAsState()

    var selectedSection by remember { mutableStateOf(0) }

    val sections = listOf(
        "1. Dashboard",
        "2. Home Settings",
        "3. Welcome Settings",
        "4. Login & Register",
        "5. User Management",
        "6. Pending Users",
        "7. Approved Users",
        "8. Rejected Users",
        "9. BetPro Management",
        "10. Deposit System",
        "11. Withdrawal System",
        "12. Payment Methods",
        "13. Bonus Settings",
        "14. Quick Options",
        "15. Notifications",
        "16. Support / WhatsApp",
        "17. App Links",
        "18. Text & Instructions",
        "19. Banner Management",
        "20. Transactions",
        "21. App Visibility",
        "22. App Settings",
        "23. Admin Security",
        "24. Logout"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(goldGradient)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF111827),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Admin Control Center",
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Logout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Section Tabs Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sections.indices.toList()) { index ->
                    val isSelected = selectedSection == index
                    Button(
                        onClick = {
                            if (index == 23) {
                                onLogout()
                            } else {
                                selectedSection = index
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) goldBorder else cardDarkBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        border = if (!isSelected) BorderStroke(1.dp, goldBorder.copy(alpha = 0.4f)) else null
                    ) {
                        Text(
                            text = sections[index],
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Content per Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedSection) {
                    0 -> DashboardSection(users, depositRequests, withdrawalRequests, cardDarkBg, goldBorder)
                    1 -> HomeSettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    2 -> WelcomeSettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    3 -> LoginRegisterSettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    4 -> UserManagementSection(users, viewModel, cardDarkBg, goldBorder)
                    5 -> PendingUsersSection(users, viewModel, cardDarkBg, goldBorder)
                    6 -> ApprovedUsersSection(users, cardDarkBg, goldBorder)
                    7 -> RejectedUsersSection(users, cardDarkBg, goldBorder)
                    8 -> BetProManagementSection(users, viewModel, cardDarkBg, goldBorder)
                    9 -> DepositSystemSection(viewModel, depositRequests, cardDarkBg, goldBorder)
                    10 -> WithdrawalSystemSection(viewModel, withdrawalRequests, cardDarkBg, goldBorder)
                    11 -> PaymentMethodsSection(paymentMethods, viewModel, cardDarkBg, goldBorder)
                    12 -> BonusSettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    13 -> QuickOptionsSection(quickOptions, viewModel, cardDarkBg, goldBorder)
                    14 -> NotificationsSection(viewModel, users, cardDarkBg, goldBorder)
                    15 -> SupportWhatsAppSection(viewModel, settings, cardDarkBg, goldBorder)
                    16 -> AppLinksSection(viewModel, settings, cardDarkBg, goldBorder)
                    17 -> TextInstructionsSection(viewModel, settings, cardDarkBg, goldBorder)
                    18 -> BannerManagementSection(banners, viewModel, cardDarkBg, goldBorder)
                    19 -> TransactionManagementSection(viewModel, depositRequests, withdrawalRequests, cardDarkBg, goldBorder)
                    20 -> AppVisibilitySettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    21 -> AppSettingsSection(viewModel, settings, cardDarkBg, goldBorder)
                    22 -> AdminSecuritySection(viewModel, cardDarkBg, goldBorder)
                    23 -> onLogout()
                }
            }
        }
    }
}

@Composable
fun DashboardSection(users: List<com.example.data.UserEntity>, deposits: List<com.example.data.DepositRequestEntity>, withdrawals: List<com.example.data.WithdrawalRequestEntity>, cardDarkBg: Color, goldBorder: Color) {
    val totalUsers = users.size
    val pendingUsers = users.count { it.status == "PENDING_APPROVAL" || it.status == "PENDING" }
    val approvedUsers = users.count { it.status == "APPROVED" }
    val rejectedUsers = users.count { it.status == "REJECTED" }

    val pendingDeps = deposits.count { it.status == "PENDING" }
    val approvedDeps = deposits.count { it.status == "APPROVED" }
    val rejectedDeps = deposits.count { it.status == "REJECTED" }

    val pendingWds = withdrawals.count { it.status == "PENDING" }
    val approvedWds = withdrawals.count { it.status == "APPROVED" }
    val rejectedWds = withdrawals.count { it.status == "REJECTED" }
    val totalTx = deposits.size + withdrawals.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Dashboard Overview", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Total Users", totalUsers.toString(), cardDarkBg, goldBorder, Modifier.weight(1f))
                MetricCard("Pending Users", pendingUsers.toString(), cardDarkBg, Color(0xFFF59E0B), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Approved Users", approvedUsers.toString(), cardDarkBg, Color(0xFF10B981), Modifier.weight(1f))
                MetricCard("Rejected Users", rejectedUsers.toString(), cardDarkBg, Color(0xFFEF4444), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Pending Deposits", pendingDeps.toString(), cardDarkBg, Color(0xFFF59E0B), Modifier.weight(1f))
                MetricCard("Approved Deposits", approvedDeps.toString(), cardDarkBg, Color(0xFF10B981), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Pending Withdrawals", pendingWds.toString(), cardDarkBg, Color(0xFFF59E0B), Modifier.weight(1f))
                MetricCard("Total Transactions", totalTx.toString(), cardDarkBg, goldBorder, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, bg: Color, borderColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, color = Color.Gray, fontSize = 12.sp)
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
    }
}

@Composable
fun HomeSettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var appName by remember { mutableStateOf(settings.find { it.settingKey == "home_app_name" }?.settingValue ?: "AutoBet Pro Wallet") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Home Screen Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("App Header Name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("home_app_name", appName)
                    Toast.makeText(context, "Home settings saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Home Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WelcomeSettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var heading by remember { mutableStateOf(settings.find { it.settingKey == "welcome_heading" }?.settingValue ?: "AutoBet Pro") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Welcome Screen Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = heading,
                onValueChange = { heading = it },
                label = { Text("Welcome Heading", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("welcome_heading", heading)
                    Toast.makeText(context, "Welcome settings saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Welcome Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LoginRegisterSettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var regEnabled by remember { mutableStateOf(settings.find { it.settingKey == "reg_enabled" }?.settingValue ?: "true") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Login & Register Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Registration", color = Color.White)
                Switch(
                    checked = regEnabled.toBoolean(),
                    onCheckedChange = {
                        regEnabled = it.toString()
                        viewModel.saveSetting("reg_enabled", it.toString())
                    }
                )
            }
        }
    }
}

@Composable
fun UserManagementSection(users: List<com.example.data.UserEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    ApprovedUsersSection(users, cardDarkBg, goldBorder)
}

@Composable
fun PendingUsersSection(users: List<com.example.data.UserEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val pendingUsers = users.filter { it.status == "PENDING_APPROVAL" || it.status == "PENDING" }
    val context = LocalContext.current

    if (pendingUsers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending users", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(pendingUsers) { user ->
                var bpLink by remember { mutableStateOf("https://betproexch.com") }
                var bpUser by remember { mutableStateOf("") }
                var bpPass by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = user.fullName, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = user.status, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                        Text(text = "App Username: ${user.username}", color = Color.White)
                        Text(text = "Mobile: ${user.mobileNumber}", color = Color.White.copy(alpha = 0.9f))

                        OutlinedTextField(
                            value = bpLink,
                            onValueChange = { bpLink = it },
                            label = { Text("BetPro Link", color = Color.Gray) },
                            placeholder = { Text("Enter BetPro Website Link", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = bpUser,
                            onValueChange = { bpUser = it },
                            label = { Text("BetPro Username", color = Color.Gray) },
                            placeholder = { Text("Enter BetPro Username", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = bpPass,
                            onValueChange = { bpPass = it },
                            label = { Text("BetPro Password", color = Color.Gray) },
                            placeholder = { Text("Enter BetPro Password", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Button(
                            onClick = {
                                if (bpLink.isBlank() || bpUser.isBlank() || bpPass.isBlank()) {
                                    Toast.makeText(context, "Enter Link, Username and Password", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.approveUser(user.id, bpLink, bpUser, bpPass, user.userId)
                                Toast.makeText(context, "User approved & BetPro assigned!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save & Approve", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovedUsersSection(users: List<com.example.data.UserEntity>, cardDarkBg: Color, goldBorder: Color) {
    val approvedUsers = users.filter { it.status == "APPROVED" }
    if (approvedUsers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No approved users", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(approvedUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = user.fullName, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Username: ${user.username}", color = Color.White)
                        Text(text = "BetPro Link: ${user.betProLink.ifEmpty { "N/A" }}", color = Color(0xFF34D399))
                        Text(text = "BetPro Username: ${user.betProUsername}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                        Text(text = "BetPro Password: ${user.betProPassword}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RejectedUsersSection(users: List<com.example.data.UserEntity>, cardDarkBg: Color, goldBorder: Color) {
    val rejectedUsers = users.filter { it.status == "REJECTED" }
    if (rejectedUsers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No rejected users", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(rejectedUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = user.fullName, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Username: ${user.username}", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BetProManagementSection(users: List<com.example.data.UserEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val approvedUsers = users.filter { it.status == "APPROVED" }
    val context = LocalContext.current

    if (approvedUsers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No approved users for BetPro management", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(approvedUsers) { user ->
                var linkText by remember { mutableStateOf(user.betProLink.ifEmpty { "https://betproexch.com" }) }
                var userText by remember { mutableStateOf(user.betProUsername) }
                var passText by remember { mutableStateOf(user.betProPassword) }

                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Edit User: ${user.fullName} (${user.username})", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        OutlinedTextField(
                            value = linkText,
                            onValueChange = { linkText = it },
                            label = { Text("BetPro Link", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = userText,
                            onValueChange = { userText = it },
                            label = { Text("BetPro Username", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = passText,
                            onValueChange = { passText = it },
                            label = { Text("BetPro Password", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Button(
                            onClick = {
                                viewModel.approveUser(user.id, linkText, userText, passText, user.userId)
                                Toast.makeText(context, "BetPro Account updated successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = goldBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Update BetPro Account", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepositSystemSection(viewModel: WalletViewModel, deposits: List<com.example.data.DepositRequestEntity>, cardDarkBg: Color, goldBorder: Color) {
    DepositRequestsSection(deposits, viewModel, cardDarkBg, goldBorder)
}

@Composable
fun DepositRequestsSection(deposits: List<com.example.data.DepositRequestEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    if (deposits.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No deposit requests", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(deposits) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = req.paymentMethod, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = req.status, color = if (req.status == "APPROVED") Color(0xFF10B981) else Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                        Text(text = "BetPro Username: ${req.betProUsername}", color = Color.White)
                        Text(text = "Amount: PKR ${req.amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                        if (req.status == "PENDING") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { viewModel.updateStatus(req.id, "APPROVED", null, req.amount, req.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                                    Text("Approve", color = Color.White)
                                }
                                Button(onClick = { viewModel.updateStatus(req.id, "REJECTED", "Invalid", req.amount, req.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                                    Text("Reject", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawalSystemSection(viewModel: WalletViewModel, withdrawals: List<com.example.data.WithdrawalRequestEntity>, cardDarkBg: Color, goldBorder: Color) {
    if (withdrawals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No withdrawal requests", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(withdrawals) { w ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = w.method, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = w.status, color = if (w.status == "APPROVED") Color(0xFF10B981) else Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                        }
                        Text(text = "Account: ${w.accountNumber}", color = Color.White)
                        Text(text = "Amount: PKR ${w.amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                        if (w.status == "PENDING") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { viewModel.updateWithdrawalStatus(w.id, "APPROVED", null, w.amount, w.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                                    Text("Approve", color = Color.White)
                                }
                                Button(onClick = { viewModel.updateWithdrawalStatus(w.id, "REJECTED", "Invalid", w.amount, w.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                                    Text("Reject", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodsSection(paymentMethods: List<com.example.data.PaymentMethodEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var accNum by remember { mutableStateOf("") }
    var accTitle by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Add Payment Method", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Method Name", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    OutlinedTextField(value = accNum, onValueChange = { accNum = it }, placeholder = { Text("Account Number", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    OutlinedTextField(value = accTitle, onValueChange = { accTitle = it }, placeholder = { Text("Account Title", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Button(
                        onClick = {
                            if (name.isBlank() || accNum.isBlank()) {
                                Toast.makeText(context, "Enter Name and Number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addPaymentMethod(name, accNum, accTitle, "Send funds and upload proof", true, 1)
                            name = ""
                            accNum = ""
                            accTitle = ""
                            Toast.makeText(context, "Payment Method Added", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
                    ) {
                        Text("Save Method", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(paymentMethods) { pm ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = pm.name, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Acc: ${pm.accountNumber} (${pm.accountTitle})", color = Color.White)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { viewModel.deletePaymentMethod(pm.id) }) {
                            Text("Delete", color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BonusSettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var bonusRate by remember { mutableStateOf(settings.find { it.settingKey == "bonus_rate" }?.settingValue ?: "5.0") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Bonus Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = bonusRate,
                onValueChange = { bonusRate = it },
                label = { Text("Default Bonus Percentage (%)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("bonus_rate", bonusRate)
                    Toast.makeText(context, "Bonus settings saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Bonus Settings", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuickOptionsSection(quickOptions: List<com.example.data.QuickOptionEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var nameInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Add Quick Option", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, placeholder = { Text("Name", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    OutlinedTextField(value = urlInput, onValueChange = { urlInput = it }, placeholder = { Text("URL", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Button(
                        onClick = {
                            if (nameInput.isBlank() || urlInput.isBlank()) return@Button
                            viewModel.addQuickOption(nameInput, urlInput, true, 1)
                            nameInput = ""
                            urlInput = ""
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
                    ) {
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(quickOptions) { opt ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = opt.name, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = opt.url, color = Color.Gray, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { viewModel.deleteQuickOption(opt.id) }) {
                            Text("Delete", color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsSection(viewModel: WalletViewModel, users: List<com.example.data.UserEntity>, cardDarkBg: Color, goldBorder: Color) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Notifications Management", color = Color.Gray)
    }
}

@Composable
fun SupportWhatsAppSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var whatsappNum by remember { mutableStateOf(settings.find { it.settingKey == "whatsapp_number" }?.settingValue ?: "+923001234567") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "WhatsApp & Support", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = whatsappNum,
                onValueChange = { whatsappNum = it },
                label = { Text("WhatsApp Number", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("whatsapp_number", whatsappNum)
                    Toast.makeText(context, "Support saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Support", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AppLinksSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var apkUrl by remember { mutableStateOf(settings.find { it.settingKey == "apk_download_url" }?.settingValue ?: "https://autobetpro.com/apk") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "App Links", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = apkUrl,
                onValueChange = { apkUrl = it },
                label = { Text("APK Download URL", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("apk_download_url", apkUrl)
                    Toast.makeText(context, "Links saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Links", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TextInstructionsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var depositInst by remember { mutableStateOf(settings.find { it.settingKey == "deposit_instructions" }?.settingValue ?: "Send funds to official account") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Text & Instructions", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = depositInst,
                onValueChange = { depositInst = it },
                label = { Text("Deposit Instructions", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    viewModel.saveSetting("deposit_instructions", depositInst)
                    Toast.makeText(context, "Instructions saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Save Instructions", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BannerManagementSection(banners: List<com.example.data.BannerEntity>, viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Add Banner", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(value = title, onValueChange = { title = it }, placeholder = { Text("Banner Title", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    OutlinedTextField(value = link, onValueChange = { link = it }, placeholder = { Text("Link URL", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            viewModel.addBanner(title, "", link, true, 1)
                            title = ""
                            link = ""
                            Toast.makeText(context, "Banner Added", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
                    ) {
                        Text("Save Banner", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(banners) { b ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = b.title, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { viewModel.deleteBanner(b.id) }) {
                            Text("Delete", color = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionManagementSection(viewModel: WalletViewModel, deposits: List<com.example.data.DepositRequestEntity>, withdrawals: List<com.example.data.WithdrawalRequestEntity>, cardDarkBg: Color, goldBorder: Color) {
    DepositRequestsSection(deposits, viewModel, cardDarkBg, goldBorder)
}

@Composable
fun AppVisibilitySettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var showQuickOptions by remember { mutableStateOf(settings.find { it.settingKey == "vis_quick_options" }?.settingValue ?: "true") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "App Visibility Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Show Quick Options", color = Color.White)
                Switch(
                    checked = showQuickOptions.toBoolean(),
                    onCheckedChange = {
                        showQuickOptions = it.toString()
                        viewModel.saveSetting("vis_quick_options", it.toString())
                    }
                )
            }
        }
    }
}

@Composable
fun AppSettingsSection(viewModel: WalletViewModel, settings: List<com.example.data.AdminSettingEntity>, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var maintenanceMode by remember { mutableStateOf(settings.find { it.settingKey == "maintenance_mode" }?.settingValue ?: "false") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "App General Settings", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Maintenance Mode", color = Color.White)
                Switch(
                    checked = maintenanceMode.toBoolean(),
                    onCheckedChange = {
                        maintenanceMode = it.toString()
                        viewModel.saveSetting("maintenance_mode", it.toString())
                    }
                )
            }
        }
    }
}

@Composable
fun AdminSecuritySection(viewModel: WalletViewModel, cardDarkBg: Color, goldBorder: Color) {
    val context = LocalContext.current
    var newPass by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(text = "Admin Security & Password", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("New Admin Password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldBorder, unfocusedBorderColor = goldBorder.copy(alpha = 0.4f), focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Button(
                onClick = {
                    if (newPass.isBlank()) return@Button
                    viewModel.saveSetting("admin_password", newPass)
                    Toast.makeText(context, "Admin password updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldBorder)
            ) {
                Text("Update Password", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
