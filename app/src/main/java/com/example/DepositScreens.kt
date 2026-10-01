package com.example

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NotificationEntity
import com.example.viewmodel.WalletViewModel

data class PaymentMethod(
    val name: String,
    val subtitle: String,
    val accountNumber: String,
    val accountTitle: String,
    val logoResId: Int,
    val isAvailable: Boolean = true
)

@Composable
fun DepositMethodsScreen(
    onBack: () -> Unit,
    onSelectMethod: (PaymentMethod) -> Unit
) {
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )
    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)

    val methods = listOf(
        PaymentMethod("Easypaisa", "Mobile wallet transfer", "03414897634", "Tahmeed Ullah", R.drawable.ic_easypaisa, true),
        PaymentMethod("JazzCash", "Mobile wallet transfer", "03015909606", "Gul Jamil", R.drawable.ic_jazzcash, true),
        PaymentMethod("NayaPay", "Digital wallet transfer", "03015909606", "Tahmeed", R.drawable.ic_nayapay, true),
        PaymentMethod("Bank Transfer", "IBFT / Raast transfer - Coming Soon", "000000000", "Loading", 0, false)
    )

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
                        text = "Deposit",
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "AutoBet Pro Deposit",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, goldBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "How to Deposit",
                                color = goldBorder,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            val instructionList = listOf(
                                "1. Choose your payment method\n   (Easypaisa, JazzCash, NayaPay or Bank Transfer)",
                                "2. Copy the account number shown and send the payment from your selected payment app.",
                                "3. Enter the amount and upload your payment screenshot.",
                                "4. Submit the deposit request and wait for verification."
                            )

                            instructionList.forEach { instruction ->
                                Text(
                                    text = instruction,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            HorizontalDivider(color = goldBorder.copy(alpha = 0.3f), thickness = 1.dp)

                            Text(
                                text = "Minimum Deposit: 500 PKR",
                                color = Color(0xFFF3E5AB),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "جمع کروانے کا طریقہ",
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            val urduInstructions = listOf(
                                "1. اپنا ادائیگی کا طریقہ منتخب کریں۔\n   (ایزی پیسہ، جاز کیش، نیا پے یا بینک ٹرانسفر)",
                                "2. دیا گیا اکاؤنٹ نمبر کاپی کریں اور اپنی منتخب کردہ ایپ سے ادائیگی بھیجیں۔",
                                "3. رقم لکھیں اور ادائیگی کی تصویر اپلوڈ کریں۔",
                                "4. درخواست جمع کروائیں اور منظوری کا انتظار کریں۔"
                            )

                            urduInstructions.forEach { urdu ->
                                Text(
                                    text = urdu,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            HorizontalDivider(color = Color(0xFF34D399).copy(alpha = 0.3f), thickness = 1.dp)

                            Text(
                                text = "کم از کم ڈپازٹ کی رقم 500 روپے ہے۔",
                                color = Color(0xFF34D399),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(methods) { method ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = method.isAvailable) { onSelectMethod(method) }
                            .border(1.dp, if (method.isAvailable) goldBorder.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (method.isAvailable) cardDarkBg else cardDarkBg.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (method.logoResId != 0) {
                                        Image(
                                            painter = painterResource(id = method.logoResId),
                                            contentDescription = method.name,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = method.name,
                                            tint = Color.DarkGray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = method.name,
                                        color = if (method.isAvailable) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = method.subtitle,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (method.isAvailable) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Select",
                                    tint = goldBorder,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Coming Soon",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepositFormScreen(
    method: PaymentMethod,
    onBack: () -> Unit,
    onSubmitSuccess: (Double) -> Unit,
    viewModel: WalletViewModel
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )
    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)

    var betProUsername by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedBonus by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var screenshotUri by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            screenshotUri = uri.toString()
            Toast.makeText(context, "Screenshot selected", Toast.LENGTH_SHORT).show()
        }
    }

    val quickBonuses = listOf(
        1000 to 50,
        2000 to 100,
        5000 to 250,
        10000 to 500,
        15000 to 750,
        20000 to 1000
    )

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
                        text = method.name,
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "AutoBet Pro deposit",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                item {
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
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (method.logoResId != 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White)
                                                .padding(4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = method.logoResId),
                                                contentDescription = method.name,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Send payment to this account:",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 13.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.3f))
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "${method.name.uppercase()} NUMBER",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = method.accountNumber,
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(method.accountNumber))
                                                Toast.makeText(context, "Account number copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = goldBorder),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Copy",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Account Title: ${method.accountTitle}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, goldBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Quick Bonus",
                                color = goldBorder,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Select a quick bonus amount or enter a matching preset value",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            val rows = quickBonuses.chunked(2)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { (amt, bonus) ->
                                        val isSelected = selectedBonus == Pair(amt, bonus)
                                        OutlinedButton(
                                            onClick = {
                                                selectedBonus = Pair(amt, bonus)
                                                amountText = amt.toString()
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(56.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isSelected) goldBorder.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f),
                                                contentColor = Color.White
                                            ),
                                            border = BorderStroke(1.dp, if (isSelected) goldBorder else goldBorder.copy(alpha = 0.3f))
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "${amt} PKR",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "+$bonus",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF34D399),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (selectedBonus != null) {
                                Text(
                                    text = "Selected Bonus: +${selectedBonus!!.second} PKR",
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "BetPro Username",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = betProUsername,
                            onValueChange = { betProUsername = it },
                            placeholder = { Text("Enter your BetPro Username", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = goldBorder,
                                unfocusedBorderColor = goldBorder.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = goldBorder
                            )
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Amount (PKR)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = { Text("Minimum 500", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = goldBorder,
                                unfocusedBorderColor = goldBorder.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = goldBorder
                            )
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Payment Screenshot",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, goldBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .background(cardDarkBg)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (screenshotUri != null) {
                                AsyncImage(
                                    model = screenshotUri,
                                    contentDescription = "Screenshot Preview",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Upload",
                                        tint = goldBorder,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "Tap to choose photo from gallery",
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (betProUsername.isBlank()) {
                                Toast.makeText(context, "Please enter your BetPro Username", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt < 500.0) {
                                Toast.makeText(context, "Minimum deposit amount is 500 PKR", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (screenshotUri == null) {
                                Toast.makeText(context, "Please upload payment screenshot", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (isSubmitting) return@Button

                            isSubmitting = true
                            val bonusAmt = selectedBonus?.second?.toDouble() ?: 0.0

                            viewModel.submitDeposit(
                                paymentMethod = method.name,
                                betProUsername = betProUsername,
                                amount = amt,
                                bonus = bonusAmt,
                                screenshotUri = screenshotUri!!
                            ) {
                                isSubmitting = false
                                onSubmitSuccess(amt)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(goldGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Submit Deposit Request",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, goldBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardDarkBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Send payment first, then upload screenshot — deposit won't approve without proof.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "پہلے ادائیگی بھیجیں، پھر تصویر لگائیں — بغیر تصویر کے جمع منظور نہیں ہوگی۔",
                                color = Color(0xFF34D399),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Verification normally takes 5–15 minutes. Keep your payment receipt until approved.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DepositSuccessScreen(amount: Double, onBackToHome: () -> Unit) {
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )
    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(24.dp),
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
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "Deposit Request Submitted Successfully!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Your deposit of PKR ${amount.toInt()} is pending review.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "آپ کی ${amount.toInt()} روپے کی ڈپازٹ درخواست کامیابی سے جمع ہوگئی ہے اور منظوری کی منتظر ہے۔",
                    color = Color(0xFF34D399),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onBackToHome,
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
                        Text(
                            text = "Back to Home",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onBack: () -> Unit,
    onMarkAsRead: (Long) -> Unit
) {
    val goldGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE5C158), Color(0xFFD4AF37), Color(0xFFF3E5AB))
    )
    val darkBg = Color(0xFF0B0E0F)
    val cardDarkBg = Color(0xFF141A1E)
    val goldBorder = Color(0xFFD4AF37)

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
                        text = "Notifications",
                        color = Color(0xFF111827),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notifications yet",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { if (!notif.isRead) onMarkAsRead(notif.id) }
                                .border(1.dp, if (notif.isRead) goldBorder.copy(alpha = 0.2f) else goldBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (notif.isRead) cardDarkBg else cardDarkBg.copy(alpha = 0.9f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = goldBorder,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEF4444))
                                        )
                                    }
                                }
                                Text(
                                    text = notif.message,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
