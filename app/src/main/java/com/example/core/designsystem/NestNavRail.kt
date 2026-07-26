package com.example.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalNestColors

@Composable
fun NestNavRail(
    items: List<NestNavItem>,
    currentRoute: String,
    onItemSelected: (NestNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalNestColors.current

    Column(
        modifier = modifier
            .width(100.dp)
            .fillMaxHeight()
            .background(colors.background)
            .border(1.dp, colors.border)
            .statusBarsPadding()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Nest",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = colors.primaryAccent,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationRailItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = colors.primaryAccent,
                    selectedTextColor = colors.primaryAccent,
                    indicatorColor = colors.secondaryBackground,
                    unselectedIconColor = colors.secondaryText,
                    unselectedTextColor = colors.secondaryText
                ),
                modifier = Modifier
                    .testTag(item.testTag)
                    .padding(vertical = 4.dp)
            )
        }
    }
}
