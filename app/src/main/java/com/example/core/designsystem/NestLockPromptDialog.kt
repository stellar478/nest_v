package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalNestColors

@Composable
fun NestLockPromptDialog(
    onUnlockWithPin: (String) -> Unit,
    onUnlockWithBiometric: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null
) {
    val colors = LocalNestColors.current
    var pinText by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(20.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .testTag("lock_prompt_dialog")
                .fillMaxWidth()
                .clip(shape)
                .background(colors.card)
                .border(1.dp, colors.border, shape)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = colors.primaryAccent,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nest Digital Vault Locked",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Enter master PIN or use hardware biometric key to unlock",
                fontSize = 13.sp,
                color = colors.secondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            NestTextField(
                value = pinText,
                onValueChange = { pinText = it },
                label = "Master PIN",
                placeholder = "••••",
                isPassword = true,
                testTag = "lock_prompt_pin_input"
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = colors.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            NestButton(
                text = "Unlock Vault",
                onClick = { onUnlockWithPin(pinText) },
                variant = NestButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
                testTag = "lock_prompt_submit"
            )

            Spacer(modifier = Modifier.height(8.dp))

            NestButton(
                text = "Hardware Biometric Pass",
                onClick = onUnlockWithBiometric,
                variant = NestButtonVariant.GHOST,
                modifier = Modifier.fillMaxWidth(),
                testTag = "lock_prompt_biometric"
            )
        }
    }
}
