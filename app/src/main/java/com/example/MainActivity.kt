package com.example

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.DepositRequestEntity
import com.example.data.NotificationEntity
import com.example.data.WithdrawalRequestEntity
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AutoBetApp()
      }
    }
  }
}

sealed class Screen {
  object Welcome : Screen()
  object SignIn : Screen()
  object Register : Screen()
  object AccountVerification : Screen()
  object AdminLogin : Screen()
  object Home : Screen()
  object DepositMethods : Screen()
  data class DepositForm(val method: PaymentMethod) : Screen()
  data class DepositSuccess(val amount: Double) : Screen()
  object WithdrawalMethods : Screen()
  data class WithdrawalForm(val method: WithdrawalMethod) : Screen()
  data class WithdrawalSuccess(val amount: Double) : Screen()
  object Notifications : Screen()
  object Admin : Screen()
}

@Composable
fun AutoBetApp(viewModel: WalletViewModel = viewModel()) {
  var currentScreen by remember { mutableStateOf<Screen>(Screen.Welcome) }
  val notifications by viewModel.notifications.collectAsState()
  val unreadCount = notifications.count { !it.isRead }

  BackHandler(enabled = currentScreen != Screen.Welcome) {
    currentScreen = when (currentScreen) {
      is Screen.SignIn, is Screen.Register, is Screen.AccountVerification, is Screen.AdminLogin -> Screen.Welcome
      is Screen.Admin -> Screen.AdminLogin
      else -> Screen.Home
    }
  }

  when (val screen = currentScreen) {
    is Screen.Welcome -> {
      WelcomeScreen(
        onSignInClick = { currentScreen = Screen.SignIn },
        onRegisterClick = { currentScreen = Screen.Register },
        onAdminAccessRequest = { currentScreen = Screen.AdminLogin }
      )
    }
    is Screen.AdminLogin -> {
      AdminLoginScreen(
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Welcome },
        onAdminLoginSuccess = { currentScreen = Screen.Admin }
      )
    }
    is Screen.Admin -> {
      AdminScreen(
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Home },
        onLogout = { currentScreen = Screen.AdminLogin }
      )
    }
    is Screen.SignIn -> {
      SignInScreen(
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Welcome },
        onLoginSuccess = {
          val user = viewModel.currentUser.value
          if (user != null && user.status == "APPROVED") {
            currentScreen = Screen.Home
          } else {
            currentScreen = Screen.AccountVerification
          }
        }
      )
    }
    is Screen.Register -> {
      RegisterScreen(
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Welcome },
        onRegisterSuccess = {
          currentScreen = Screen.AccountVerification
        }
      )
    }
    is Screen.AccountVerification -> {
      AccountVerificationScreen(
        viewModel = viewModel,
        onApproved = { currentScreen = Screen.Home },
        onLogout = {
          viewModel.setCurrentUser(null)
          currentScreen = Screen.Welcome
        }
      )
    }
    is Screen.Home -> {
      AutoBetWalletScreen(
        viewModel = viewModel,
        unreadCount = unreadCount,
        onNavigateToDeposit = { currentScreen = Screen.DepositMethods },
        onNavigateToWithdrawal = { currentScreen = Screen.WithdrawalMethods },
        onNavigateToNotifications = { currentScreen = Screen.Notifications },
        onOpenAdmin = { currentScreen = Screen.Admin }
      )
    }
    is Screen.DepositMethods -> {
      DepositMethodsScreen(
        onBack = { currentScreen = Screen.Home },
        onSelectMethod = { method ->
          if (method.isAvailable) {
            currentScreen = Screen.DepositForm(method)
          }
        }
      )
    }
    is Screen.DepositForm -> {
      DepositFormScreen(
        method = screen.method,
        onBack = { currentScreen = Screen.DepositMethods },
        onSubmitSuccess = { amount ->
          currentScreen = Screen.DepositSuccess(amount)
        },
        viewModel = viewModel
      )
    }
    is Screen.DepositSuccess -> {
      DepositSuccessScreen(
        amount = screen.amount,
        onBackToHome = { currentScreen = Screen.Home }
      )
    }
    is Screen.WithdrawalMethods -> {
      WithdrawalMethodsScreen(
        onBack = { currentScreen = Screen.Home },
        onSelectMethod = { method ->
          currentScreen = Screen.WithdrawalForm(method)
        }
      )
    }
    is Screen.WithdrawalForm -> {
      WithdrawalFormScreen(
        method = screen.method,
        onBack = { currentScreen = Screen.WithdrawalMethods },
        onSubmitSuccess = { amount ->
          currentScreen = Screen.WithdrawalSuccess(amount)
        },
        viewModel = viewModel
      )
    }
    is Screen.WithdrawalSuccess -> {
      WithdrawalSuccessScreen(
        amount = screen.amount,
        onBackToHome = { currentScreen = Screen.Home }
      )
    }
    is Screen.Notifications -> {
      NotificationsScreen(
        notifications = notifications,
        onBack = { currentScreen = Screen.Home },
        onMarkAsRead = { id -> viewModel.markNotificationRead(id) }
      )
    }
    is Screen.Admin -> {
      AdminScreen(
        viewModel = viewModel,
        onBack = { currentScreen = Screen.Home }
      )
    }
  }
}

@Composable
fun AutoBetWalletScreen(
  viewModel: WalletViewModel,
  unreadCount: Int,
  onNavigateToDeposit: () -> Unit,
  onNavigateToWithdrawal: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onOpenAdmin: () -> Unit
) {
  val goldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
  )
  val darkBg = Color(0xFF0B0E0F)
  val cardDarkBg = Color(0xFF141A1E)
  val goldBorder = Color(0xFFD4AF37)
  val cyanColor = Color(0xFF22D3EE)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(darkBg)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 72.dp)
    ) {
      // 1. Top Header
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clickable { onOpenAdmin() }
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(cyanColor),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "b",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
              )
            }
            Text(
              text = "AutoBet Pro Wallet",
              color = Color(0xFF111827),
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box {
              IconButton(
                onClick = onNavigateToNotifications,
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.25f))
              ) {
                Icon(
                  imageVector = Icons.Default.Notifications,
                  contentDescription = "Notifications",
                  tint = Color(0xFF111827),
                  modifier = Modifier.size(20.dp)
                )
              }
              if (unreadCount > 0) {
                Box(
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = unreadCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            IconButton(
              onClick = onOpenAdmin,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
            ) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                tint = Color(0xFF111827),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // Scrollable Content
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          val context = LocalContext.current
          val currentUser by viewModel.currentUser.collectAsState()
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.5.dp, goldBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "BetPro Account",
                  color = goldBorder,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                val status = currentUser?.status ?: "APPROVED"
                val statusColor = if (status == "APPROVED") Color(0xFF10B981) else Color(0xFFF59E0B)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "Status: $status",
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (currentUser == null || currentUser?.status == "APPROVED" || currentUser?.userId == "user_demo_1") {
                var showPassword by remember { mutableStateOf(false) }
                val clipboard = LocalClipboardManager.current
                val bpUser = currentUser?.betProUsername?.takeIf { it.isNotEmpty() } ?: "Bitpro999973"
                val bpPass = currentUser?.betProPassword?.takeIf { it.isNotEmpty() } ?: "Bitpro999973"

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(text = "Username:", color = Color.Gray, fontSize = 12.sp)
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color.Black.copy(alpha = 0.4f))
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = bpUser,
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp
                    )
                    OutlinedButton(
                      onClick = {
                        clipboard.setText(AnnotatedString(bpUser))
                        Toast.makeText(context, "Username copied!", Toast.LENGTH_SHORT).show()
                      },
                      shape = RoundedCornerShape(6.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                      Text("Copy", fontSize = 12.sp, color = goldBorder)
                    }
                  }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(text = "Password:", color = Color.Gray, fontSize = 12.sp)
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color.Black.copy(alpha = 0.4f))
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = if (showPassword) bpPass else "••••••••",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      IconButton(
                        onClick = { showPassword = !showPassword },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(
                          imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                          contentDescription = "Toggle Password",
                          tint = Color.Gray,
                          modifier = Modifier.size(18.dp)
                        )
                      }
                      OutlinedButton(
                        onClick = {
                          clipboard.setText(AnnotatedString(bpPass))
                          Toast.makeText(context, "Password copied!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text("Copy", fontSize = 12.sp, color = goldBorder)
                      }
                    }
                  }
                }
              } else {
                Text(
                  text = "Your account is pending admin approval. BetPro credentials will appear here once approved by admin.",
                  color = Color.Gray,
                  fontSize = 13.sp
                )
              }
            }
          }
        }

        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, goldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
          ) {
            Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(goldGradient),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Download,
                      contentDescription = "Download",
                      tint = Color.Black,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                  Column {
                    Text(
                      text = "Download App",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp
                    )
                    Text(
                      text = "AutoBet Pro APK for Android",
                      color = Color.Gray,
                      fontSize = 12.sp
                    )
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = goldBorder),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = "Download",
                      color = Color.Black,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                  }
                  IconButton(
                    onClick = {},
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Close",
                      tint = Color.Gray,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }

        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, goldBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(goldGradient),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = "Play",
                  tint = Color.Black,
                  modifier = Modifier.size(28.dp)
                )
              }
              Column {
                Text(
                  text = "Welcome Voice",
                  color = Color(0xFFF3E5AB),
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = "Tap to listen",
                  color = Color.Gray,
                  fontSize = 13.sp
                )
              }
            }
          }
        }

        item {
          Box(modifier = Modifier.fillMaxWidth()) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, goldBorder, RoundedCornerShape(16.dp)),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = cardDarkBg)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFF10B981),
                        Color(0xFF059669),
                        Color(0xFF047857)
                      )
                    )
                  )
                  .padding(16.dp)
              ) {
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cyanColor),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "b",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                      )
                    }
                    Column {
                      Text(
                        text = "AutoBet Pro",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                      )
                      Text(
                        text = "Platform Login",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                      )
                    }
                  }

                  AccountFieldBox(
                    label = "USERNAME",
                    value = "DemoUser"
                  )

                  AccountFieldBox(
                    label = "PASSWORD",
                    value = "********"
                  )
                }
              }
            }

            Box(
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
                .border(2.dp, Color.White, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Chat",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }

        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(goldBorder, RoundedCornerShape(2.dp))
            )
            Text(
              text = "Quick Actions",
              color = Color(0xFFF3E5AB),
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            QuickActionButton(
              title = "Deposit",
              icon = Icons.Default.AccountBalanceWallet,
              gradient = goldGradient,
              modifier = Modifier.weight(1f),
              onClick = onNavigateToDeposit
            )
            QuickActionButton(
              title = "Withdrawal",
              icon = Icons.Default.SwapVert,
              gradient = goldGradient,
              modifier = Modifier.weight(1f),
              onClick = onNavigateToWithdrawal
            )
            QuickActionButton(
              title = "Support",
              icon = Icons.Default.HeadsetMic,
              gradient = goldGradient,
              modifier = Modifier.weight(1f),
              onClick = {}
            )
          }
        }

        item {
          val quickOptions by viewModel.allQuickOptions.collectAsState()
          val enabledOptions = quickOptions.filter { it.isEnabled }
          if (enabledOptions.isNotEmpty()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              for (option in enabledOptions.take(3)) {
                PlatformButton(
                  title = option.name,
                  cyanColor = cyanColor,
                  gradient = goldGradient,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }

        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, goldBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .width(72.dp)
                  .background(Color.Black.copy(alpha = 0.4f))
                  .padding(vertical = 14.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFEF4444))
                  )
                  Text(
                    text = "OUT",
                    color = Color(0xFFF3E5AB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }

              Spacer(
                modifier = Modifier
                  .width(1.dp)
                  .height(48.dp)
                  .background(goldBorder.copy(alpha = 0.3f))
              )

              Row(
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "aeem Kazmi",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "Gujrat",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                  )
                }
                Text(
                  text = "withdrew Rs 55",
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 12.sp
                )
              }
            }
          }
        }

        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFF10B981).copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardDarkBg)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .width(72.dp)
                  .background(Color.Black.copy(alpha = 0.4f))
                  .padding(vertical = 14.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF10B981))
                  )
                  Text(
                    text = "IN",
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }

              Spacer(
                modifier = Modifier
                  .width(1.dp)
                  .height(48.dp)
                  .background(goldBorder.copy(alpha = 0.3f))
              )

              Row(
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "50,000",
                  color = Color(0xFF10B981),
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = "45s ago",
                  color = Color.Gray,
                  fontSize = 12.sp
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.ArrowDownward,
                      contentDescription = "In",
                      tint = Color(0xFF10B981),
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = "Daailadr",
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }

        item {
          // Recent Transactions Section (displays both deposits & withdrawals)
          RecentTransactionsSection(viewModel = viewModel, goldBorder = goldBorder, cardDarkBg = cardDarkBg)
        }
      }
    }

    // 8. Bottom Navigation Bar
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .background(goldGradient)
        .padding(vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        BottomNavItem(icon = Icons.Default.Home, label = "Home", isSelected = true, onClick = {})
        BottomNavItem(icon = Icons.Default.AccountCircle, label = "Account", isSelected = false, onClick = {})
        BottomNavItem(icon = Icons.Default.AccountBalanceWallet, label = "Deposit", isSelected = false, onClick = onNavigateToDeposit)
        BottomNavItem(icon = Icons.Default.SwapVert, label = "Withdrawal", isSelected = false, onClick = onNavigateToWithdrawal)
      }
    }
  }
}

@Composable
fun RecentTransactionsSection(viewModel: WalletViewModel, goldBorder: Color, cardDarkBg: Color) {
  val requests by viewModel.userRequests.collectAsState()
  val withdrawals by viewModel.userWithdrawals.collectAsState()

  val allItems = mutableListOf<TransactionItem>()
  requests.forEach { req ->
    allItems.add(TransactionItem(id = req.id, type = "Deposit (${req.paymentMethod})", amount = req.amount, status = req.status, timestamp = req.timestamp))
  }
  withdrawals.forEach { w ->
    allItems.add(TransactionItem(id = w.id, type = "Withdrawal (${w.method})", amount = w.amount, status = w.status, timestamp = w.timestamp))
  }
  allItems.sortByDescending { id -> id.timestamp }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(18.dp)
          .background(goldBorder, RoundedCornerShape(2.dp))
      )
      Text(
        text = "Recent Transactions",
        color = Color(0xFFF3E5AB),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    }

    if (allItems.isEmpty()) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .border(1.dp, goldBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardDarkBg)
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No transactions yet",
            color = Color.Gray,
            fontSize = 14.sp
          )
        }
      }
    } else {
      allItems.take(5).forEach { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, goldBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = cardDarkBg)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = item.type,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "PKR ${item.amount.toInt()}",
                color = goldBorder,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
            Text(
              text = when (item.status) {
                "APPROVED" -> "Approved"
                "REJECTED" -> "Rejected"
                else -> "Pending Review"
              },
              color = when (item.status) {
                "APPROVED" -> Color(0xFF10B981)
                "REJECTED" -> Color(0xFFEF4444)
                else -> Color(0xFFF59E0B)
              },
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}

data class TransactionItem(
  val id: Long,
  val type: String,
  val amount: Double,
  val status: String,
  val timestamp: Long
)

@Composable
fun AdminScreen(
  viewModel: WalletViewModel,
  onBack: () -> Unit
) {
  val goldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
  )
  val darkBg = Color(0xFF0B0E0F)
  val cardDarkBg = Color(0xFF141A1E)
  val goldBorder = Color(0xFFD4AF37)

  val depositRequests by viewModel.allRequests.collectAsState()
  val withdrawalRequests by viewModel.allWithdrawals.collectAsState()

  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(0) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(darkBg)
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(goldGradient)
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
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

          Spacer(modifier = Modifier.width(16.dp))

          Text(
            text = "Admin Panel",
            color = Color(0xFF111827),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        }
      }

      val users by viewModel.allUsers.collectAsState()
      val pendingUsersCount = users.count { it.status == "PENDING" }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { selectedTab = 0 },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 0) goldBorder else cardDarkBg),
          contentPadding = PaddingValues(4.dp)
        ) {
          Text("Deposits (${depositRequests.size})", color = if (selectedTab == 0) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Button(
          onClick = { selectedTab = 1 },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 1) goldBorder else cardDarkBg),
          contentPadding = PaddingValues(4.dp)
        ) {
          Text("Withdrawals (${withdrawalRequests.size})", color = if (selectedTab == 1) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Button(
          onClick = { selectedTab = 2 },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 2) goldBorder else cardDarkBg),
          contentPadding = PaddingValues(4.dp)
        ) {
          Text("New Users ($pendingUsersCount)", color = if (selectedTab == 2) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      if (selectedTab == 0) {
        if (depositRequests.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
            Text("No deposit requests", color = Color.Gray)
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(depositRequests) { req ->
              Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
              ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = req.paymentMethod, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = req.status, color = if (req.status == "APPROVED") Color(0xFF10B981) else if (req.status == "REJECTED") Color(0xFFEF4444) else Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                  }
                  Text(text = "BetPro Username: ${req.betProUsername}", color = Color.White, fontWeight = FontWeight.Bold)
                  Text(text = "Amount: PKR ${req.amount.toInt()} (+${req.bonus.toInt()} bonus)", color = Color.White)
                  val dateFormat = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()) }
                  val dateStr = dateFormat.format(java.util.Date(req.timestamp))
                  Text(text = "Date/Time: $dateStr", color = Color.Gray, fontSize = 12.sp)
                  Text(text = "User ID: ${req.userId}", color = Color.Gray, fontSize = 12.sp)
                  if (req.screenshotUri.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black)) {
                      AsyncImage(model = req.screenshotUri, contentDescription = "Proof", modifier = Modifier.fillMaxSize())
                    }
                  }
                  if (req.status == "PENDING") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                      Button(onClick = { viewModel.updateStatus(req.id, "APPROVED", null, req.amount, req.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                        Text("Approve", color = Color.White)
                      }
                      Button(onClick = { viewModel.updateStatus(req.id, "REJECTED", "Invalid screenshot", req.amount, req.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                        Text("Reject", color = Color.White)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } else if (selectedTab == 1) {
        if (withdrawalRequests.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
            Text("No withdrawal requests", color = Color.Gray)
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(withdrawalRequests) { w ->
              Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
              ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Method: ${w.method}", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = w.status, color = if (w.status == "APPROVED") Color(0xFF10B981) else if (w.status == "REJECTED") Color(0xFFEF4444) else Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                  }
                  Text(text = "BetPro Username: ${w.betProUsername}", color = Color.White, fontWeight = FontWeight.Bold)
                  Text(text = "Account Holder Name: ${w.accountHolderName}", color = Color.White.copy(alpha = 0.9f))
                  Text(text = "Account/Mobile Number: ${w.accountNumber}", color = Color.White.copy(alpha = 0.9f))
                  if (!w.bankName.isNullOrEmpty()) {
                    Text(text = "Bank Name: ${w.bankName}", color = Color.White.copy(alpha = 0.9f))
                  }
                  Text(text = "Amount: PKR ${w.amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                  val dateFormat = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()) }
                  val dateStr = dateFormat.format(java.util.Date(w.timestamp))
                  Text(text = "Date/Time: $dateStr", color = Color.Gray, fontSize = 12.sp)

                  if (w.status == "PENDING") {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                      Button(onClick = { viewModel.updateWithdrawalStatus(w.id, "APPROVED", null, w.amount, w.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                        Text("Approve", color = Color.White)
                      }
                      Button(onClick = { viewModel.updateWithdrawalStatus(w.id, "REJECTED", "Invalid account details", w.amount, w.userId) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                        Text("Reject", color = Color.White)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        if (users.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
            Text("No registered users", color = Color.Gray)
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            items(users) { user ->
              var bpUserText by remember { mutableStateOf(user.betProUsername) }
              var bpPassText by remember { mutableStateOf(user.betProPassword) }

              Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, goldBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardDarkBg)
              ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = user.fullName, color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    val statusColor = if (user.status == "APPROVED") Color(0xFF10B981) else Color(0xFFF59E0B)
                    Text(text = user.status, color = statusColor, fontWeight = FontWeight.Bold)
                  }
                  Text(text = "App Username: ${user.username}", color = Color.White)
                  Text(text = "Mobile Number: ${user.mobileNumber}", color = Color.White.copy(alpha = 0.9f))
                  val dateFormat = remember { java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()) }
                  val dateStr = dateFormat.format(java.util.Date(user.registrationTimestamp))
                  Text(text = "Registration Date: $dateStr", color = Color.Gray, fontSize = 12.sp)

                  if (user.status == "PENDING") {
                    HorizontalDivider(color = goldBorder.copy(alpha = 0.3f))
                    Text(text = "Assign BetPro Credentials", color = goldBorder, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    OutlinedTextField(
                      value = bpUserText,
                      onValueChange = { bpUserText = it },
                      placeholder = { Text("Enter BetPro Username", color = Color.Gray) },
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(8.dp),
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldBorder,
                        unfocusedBorderColor = goldBorder.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = goldBorder
                      )
                    )

                    OutlinedTextField(
                      value = bpPassText,
                      onValueChange = { bpPassText = it },
                      placeholder = { Text("Enter BetPro Password", color = Color.Gray) },
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(8.dp),
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = goldBorder,
                        unfocusedBorderColor = goldBorder.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = goldBorder
                      )
                    )

                    Button(
                      onClick = {
                        if (bpUserText.isBlank() || bpPassText.isBlank()) {
                          Toast.makeText(context, "Please enter both BetPro Username and Password", Toast.LENGTH_SHORT).show()
                          return@Button
                        }
                        viewModel.approveUser(user.id, bpUserText, bpPassText, user.userId)
                        Toast.makeText(context, "Credentials saved & user approved!", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.fillMaxWidth(),
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text("Save & Approve", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                  } else {
                    Text(text = "Assigned BetPro Username: ${user.betProUsername}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                    Text(text = "Assigned BetPro Password: ${user.betProPassword}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                  }
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
fun AccountFieldBox(label: String, value: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Color.Black.copy(alpha = 0.25f))
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label,
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = value,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
      IconButton(
        onClick = {},
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy $label",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun QuickActionButton(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  gradient: Brush,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(gradient)
        .padding(vertical = 16.dp, horizontal = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = Color(0xFF111827),
          modifier = Modifier.size(24.dp)
        )
        Text(
          text = title,
          color = Color(0xFF111827),
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}

@Composable
fun PlatformButton(
  title: String,
  cyanColor: Color,
  gradient: Brush,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(gradient)
        .padding(vertical = 16.dp, horizontal = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(cyanColor),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "b",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }
        Text(
          text = title,
          color = Color(0xFF111827),
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun BottomNavItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (isSelected) Color(0xFF111827) else Color(0xFF111827).copy(alpha = 0.6f),
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      color = if (isSelected) Color(0xFF111827) else Color(0xFF111827).copy(alpha = 0.6f),
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
