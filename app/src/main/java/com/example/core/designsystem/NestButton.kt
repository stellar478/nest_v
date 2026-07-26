package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNestColors

enum class NestButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    GHOST,
    ACCENT_PILL
}

@Composable
fun NestButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NestButtonVariant = NestButtonVariant.PRIMARY,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "nest_button"
) {
    val colors = LocalNestColors.current
    val shape = RoundedCornerShape(24.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val (bgColor, textColor, borderColor) = when (variant) {
        NestButtonVariant.PRIMARY -> Triple(
            if (enabled) colors.primaryAccent else colors.secondaryBackground,
            if (enabled) colors.card else colors.secondaryText,
            Color.Transparent
        )
        NestButtonVariant.SECONDARY -> Triple(
            if (enabled) colors.secondaryBackground else colors.background,
            if (enabled) colors.primaryText else colors.secondaryText,
            Color.Transparent
        )
        NestButtonVariant.OUTLINE -> Triple(
            Color.Transparent,
            if (enabled) colors.primaryText else colors.secondaryText,
            if (enabled) colors.border else colors.secondaryBackground
        )
        NestButtonVariant.GHOST -> Triple(
            Color.Transparent,
            if (enabled) colors.primaryAccent else colors.secondaryText,
            Color.Transparent
        )
        NestButtonVariant.ACCENT_PILL -> Triple(
            if (enabled) colors.secondaryBackground else colors.background,
            if (enabled) colors.primaryAccent else colors.secondaryText,
            if (enabled) colors.border else Color.Transparent
        )
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minWidth = 84.dp, minHeight = 48.dp)
            .clip(shape)
            .background(bgColor)
            .then(
                if (borderColor != Color.Transparent) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            )
            .bentoPressEffect(interactionSource)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
