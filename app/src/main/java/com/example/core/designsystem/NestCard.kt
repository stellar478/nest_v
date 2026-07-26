package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalNestColors

@Composable
fun NestCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    cornerRadius: Dp = 28.dp,
    padding: Dp = 20.dp,
    elevation: Dp = 1.dp,
    testTag: String = "nest_card",
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalNestColors.current
    val actualBg = backgroundColor ?: colors.card
    val actualBorder = borderColor ?: colors.border
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = colors.overlay.copy(alpha = 0.05f),
                spotColor = colors.overlay.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(actualBg)
            .border(1.dp, actualBorder, shape)
            .then(
                if (onClick != null) {
                    Modifier
                        .bentoPressEffect(interactionSource)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                } else Modifier
            )
            .padding(padding),
        content = content
    )
}
