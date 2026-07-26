package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNestColors

data class NestColumn<T>(
    val title: String,
    val weight: Float = 1f,
    val cellContent: @Composable (T) -> Unit
)

@Composable
fun <T> NestTable(
    headers: List<String>,
    items: List<T>,
    columns: List<NestColumn<T>>,
    onRowClick: ((T) -> Unit)? = null,
    modifier: Modifier = Modifier,
    testTag: String = "nest_table"
) {
    val colors = LocalNestColors.current
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clip(shape)
            .background(colors.card)
            .border(1.dp, colors.border, shape)
            .padding(16.dp)
    ) {
        // Table Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            columns.forEach { col ->
                Text(
                    text = col.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.secondaryText,
                    modifier = Modifier.weight(col.weight)
                )
            }
        }

        HorizontalDivider(color = colors.border, thickness = 1.dp)

        Spacer(modifier = Modifier.height(4.dp))

        // Table Rows
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (onRowClick != null) Modifier.clickable { onRowClick(item) }
                        else Modifier
                    )
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.forEach { col ->
                    Column(modifier = Modifier.weight(col.weight)) {
                        col.cellContent(item)
                    }
                }
            }

            if (index < items.size - 1) {
                HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}
