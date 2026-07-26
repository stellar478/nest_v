package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalNestColors

@Composable
fun NestDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    testTag: String = "nest_dialog"
) {
    val colors = LocalNestColors.current
    val shape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
                .clip(shape)
                .background(colors.card)
                .border(1.dp, colors.border, shape)
                .padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = colors.primaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = colors.secondaryText
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NestButton(
                    text = dismissText,
                    onClick = onDismiss,
                    variant = NestButtonVariant.OUTLINE,
                    modifier = Modifier.weight(1f),
                    testTag = "${testTag}_dismiss"
                )

                NestButton(
                    text = confirmText,
                    onClick = onConfirm,
                    variant = NestButtonVariant.PRIMARY,
                    modifier = Modifier.weight(1f),
                    testTag = "${testTag}_confirm"
                )
            }
        }
    }
}
