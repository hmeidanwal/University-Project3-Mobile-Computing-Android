package com.example.pricecomparable.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pricecomparable.R
import com.example.pricecomparable.ui.theme.*
import androidx.compose.ui.draw.clip


@Composable
fun WelcomeScreen(
    onSignUpClick: () -> Unit
) {
    Scaffold { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BgTop, BgBottom)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ---------- LOGO CIRCLE ----------
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            color = GlassWhite,
                            shape = CircleShape
                        )
                        .border(
                            width = 3.dp,
                            color = PrimaryMint,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pricelens_logo),
                        contentDescription = "PriceLens logo",
                        modifier = Modifier
                            .size(160.dp)    // match the outer circle
                            .padding(4.dp)
                            .clip(CircleShape),  // 👈 this makes the actual image ROUND
                        contentScale = ContentScale.Crop
                    )
                }


                // ---------- TITLE ----------
                Text(
                    text = "PriceLens - Find the cheapest deals in one click!",
                    fontSize = 22.sp,
                    color = HeadingText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ---------- SUBTEXT ----------
                Text(
                    text = "Search, compare, and locate nearby stores. All in one application!",
                    color = BodyText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp)) // moved down

                // ---------- SIGN UP BUTTON ----------
                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMint)
                ) {
                    Text(
                        text = "SIGN UP & START SAVING",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
