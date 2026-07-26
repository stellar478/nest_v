package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.SecurityGrade
import com.example.core.model.SyncStatus
import com.example.ui.theme.LocalNestColors

@Composable
fun NestBadge(
    text: String,
    badgeColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    testTag: String = "nest_badge"
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(shape)
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Text(
                text = text,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NestSecurityBadge(
    grade: SecurityGrade,
    modifier: Modifier = Modifier
) {
    val colors = LocalNestColors.current
    val badgeColor = when (grade) {
        SecurityGrade.STRONG -> colors.success
        SecurityGrade.MODERATE -> colors.warning
        SecurityGrade.WEAK, SecurityGrade.COMPROMISED -> colors.error
    }

    NestBadge(
        text = grade.label,
        badgeColor = badgeColor,
        modifier = modifier,
        testTag = "security_badge_${grade.name.lowercase()}"
    )
}

@Composable
fun NestSyncBadge(
    syncStatus: SyncStatus,
    modifier: Modifier = Modifier
) {
    val colors = LocalNestColors.current
    val badgeColor = when (syncStatus) {
        SyncStatus.OFFLINE_ONLY -> colors.primaryAccent
        SyncStatus.SYNCED -> colors.success
        SyncStatus.PENDING -> colors.warning
    }

    NestBadge(
        text = syncStatus.label,
        badgeColor = badgeColor,
        modifier = modifier,
        testTag = "sync_badge_${syncStatus.name.lowercase()}"
    )
}
