package com.example.pricecomparable.ui

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pricecomparable.viewmodel.AccountViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountPage(
    onLogout: () -> Unit,
    viewModel: AccountViewModel = viewModel()
) {
    val context = LocalContext.current
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) viewModel.updateProfilePhoto(bitmap)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bmp = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, it)
                )
            }
            viewModel.updateProfilePhoto(bmp)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            // Check if permission was permanently denied
            val shouldShowRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (context as? android.app.Activity)?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        android.Manifest.permission.CAMERA
                    )
                } ?: false
            } else {
                false
            }

            if (!shouldShowRationale) {
                Toast.makeText(
                    context,
                    "Camera permission denied. Please enable it in app settings.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val bgTop = MaterialTheme.colorScheme.background
    val bgBottom = MaterialTheme.colorScheme.surface
    val glassWhite = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    val glassBorder = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val heading = MaterialTheme.colorScheme.onBackground
    val body = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val primaryDark = MaterialTheme.colorScheme.primaryContainer
    val navInactive = MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(bgTop, bgBottom)))
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.0f),


        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // --- Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .border(2.dp, primary, CircleShape)
                            .background(glassWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        val photo = viewModel.profilePhoto.value
                        if (photo != null) {
                            Image(
                                bitmap = photo.asImageBitmap(),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Photo", color = body)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    ElevatedButton(
                        onClick = {
                            if (hasCameraPermission) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = primary),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Take Picture", color = MaterialTheme.colorScheme.onPrimary) }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(primaryDark),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(primary, primaryDark))
                        )
                    ) { Text("Upload") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show error message if any
                viewModel.errorMessage.value?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Show success message if any
                viewModel.successMessage.value?.let { success ->
                    Text(
                        text = success,
                        color = Color(0xFF4CAF50), // Green color
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Clear success message after 3 seconds
                    LaunchedEffect(success) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.successMessage.value = null
                    }
                }

                Text(
                    text = viewModel.userName.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = heading
                )

                Spacer(modifier = Modifier.height(30.dp))

                SectionHeader("Personal Information")

                GlassCard(glassWhite, glassBorder) {
                    Text(viewModel.userEmail.value, color = body)
                }

                Spacer(modifier = Modifier.height(26.dp))

                SectionHeader("Settings")

                GlassCard(glassWhite, glassBorder) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                        SettingRow(
                            "Location",
                            viewModel.locationEnabled.value,
                            viewModel::toggleLocation,
                            heading, body, primary
                        )

                        Divider(color = glassBorder.copy(alpha = 0.4f))

                        SettingRow(
                            "Notifications",
                            viewModel.notificationsEnabled.value,
                            viewModel::toggleNotifications,
                            heading, body, primary
                        )
                    }
                }                
// Push logout button to bottom
                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = glassWhite,
                        contentColor = primary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(primary, primaryDark))
                    )
                ) {
                    Text(
                        text = "Log out",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun GlassCard(bg: Color, border: Color, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) { Column { content() } }
}

@Composable
private fun SettingRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    heading: Color,
    body: Color,
    primary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = heading)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primary.copy(alpha = 0.5f),
                uncheckedThumbColor = body,
                uncheckedTrackColor = body.copy(alpha = 0.3f)
            )
        )
    }
}



