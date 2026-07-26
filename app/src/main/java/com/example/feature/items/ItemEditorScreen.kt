package com.example.feature.items

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.designsystem.NestButton
import com.example.core.designsystem.NestButtonVariant
import com.example.core.designsystem.NestCard
import com.example.core.designsystem.NestDropdown
import com.example.core.designsystem.NestTextField
import com.example.core.designsystem.NestTopBar
import com.example.core.model.VaultCategory
import com.example.ui.theme.LocalNestColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemEditorScreen(
    itemId: String?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ItemViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalNestColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            Toast.makeText(context, "Encrypted Secret Saved Successfully", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
        }
    }

    val selectedCategory = uiState.category

    // Container specific hints
    val (titleLabel, titlePlaceholder) = when (selectedCategory) {
        VaultCategory.EMAILS -> "Mailbox / Provider Title" to "e.g. Primary ProtonMail"
        VaultCategory.BANKING -> "Bank Name / Account Title" to "e.g. Nordic Federal Savings"
        VaultCategory.CARDS -> "Cardholder / Card Name" to "e.g. Platinum Metal Vault Card"
        VaultCategory.SOCIAL_MEDIA -> "Social Platform" to "e.g. X / Twitter"
        VaultCategory.GAMING -> "Gaming Network / Platform" to "e.g. Steam Hardware Vault"
        VaultCategory.APPS -> "App / Service API" to "e.g. GitHub Developer Key"
        VaultCategory.DOCUMENTS -> "Document Title" to "e.g. National Residence Permit"
        VaultCategory.RECOVERY_CODES -> "Service Name" to "e.g. Google 2FA Emergency Backup"
        VaultCategory.NOTES -> "Secure Note Title" to "e.g. Hardware Seed Recovery Phrase"
        VaultCategory.CONTACTS -> "Contact Full Name" to "e.g. Dr. Erik Vane"
        else -> "Title / Label" to "e.g. Master Secret Key"
    }

    val (subtitleLabel, subtitlePlaceholder) = when (selectedCategory) {
        VaultCategory.EMAILS -> "Email Address / Handle" to "e.g. vault.master@proton.me"
        VaultCategory.BANKING -> "Account Number / IBAN" to "e.g. NO93 8841 0029 4810"
        VaultCategory.CARDS -> "Card Number & Expiry" to "e.g. •••• 8842 | 09/29"
        VaultCategory.SOCIAL_MEDIA -> "Username / Handle" to "e.g. @vault_architect"
        VaultCategory.GAMING -> "GamerTag / Account ID" to "e.g. NordicGamerX"
        VaultCategory.APPS -> "Key ID / Client ID" to "e.g. ghp_9841203918239"
        VaultCategory.DOCUMENTS -> "Doc Registration No." to "e.g. NO-2026-88192"
        VaultCategory.RECOVERY_CODES -> "Code Summary / Count" to "e.g. 10 One-time Emergency Codes"
        VaultCategory.NOTES -> "Category / Context" to "e.g. 24-word BIP39 Seed"
        VaultCategory.CONTACTS -> "Role / Phone Number" to "e.g. Legal Advocate • +47 810 99"
        else -> "Subtitle / Details" to "e.g. User account info"
    }

    val (payloadLabel, payloadPlaceholder) = when (selectedCategory) {
        VaultCategory.EMAILS -> "Mailbox Password & App Key" to "Enter password, IMAP server or app token..."
        VaultCategory.BANKING -> "PIN, SWIFT, Routing & Secret" to "Enter PIN, BIC, Swift and security codes..."
        VaultCategory.CARDS -> "Full Card No, CVV & PIN" to "Enter 16-digit card number, CVV and PIN..."
        VaultCategory.SOCIAL_MEDIA -> "Account Password & 2FA Secret" to "Enter password and 2FA seed key..."
        VaultCategory.GAMING -> "Password & Steam Guard / Guard Code" to "Enter password and recovery guard code..."
        VaultCategory.APPS -> "Secret API Key / Client Secret" to "Enter OAuth client secret or private key..."
        VaultCategory.DOCUMENTS -> "Document Body / Hash / Content" to "Enter encrypted document text or file hash..."
        VaultCategory.RECOVERY_CODES -> "Emergency Backup Codes List" to "Enter space or comma separated emergency codes..."
        VaultCategory.NOTES -> "Encrypted Markdown / Note Content" to "Enter private notes, recovery seed or journal..."
        VaultCategory.CONTACTS -> "Encrypted Contact Details" to "Enter phone numbers, private signal handle, address..."
        else -> "Encrypted Secret Payload" to "Enter secret payload or key..."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        NestTopBar(
            title = if (itemId != null) "Edit Secret Container" else "New Secret Container",
            subtitle = "AES-256 Zero-Knowledge Encrypted",
            onBackClick = onBackClick,
            actions = {
                IconButton(
                    onClick = { viewModel.toggleFavorite() },
                    modifier = Modifier.testTag("editor_favorite_toggle")
                ) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (uiState.isFavorite) colors.error else colors.secondaryText
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Container Category Chooser
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Container Category",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )

                    NestDropdown(
                        label = "Select Container Type",
                        selectedOption = uiState.category,
                        options = VaultCategory.values().filterNot { it == VaultCategory.ALL },
                        optionLabel = { it.displayName },
                        onOptionSelected = { viewModel.updateCategory(it) },
                        testTag = "editor_category_dropdown"
                    )
                }
            }

            // Main Details Form Card
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Container Fields",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )

                    NestTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = titleLabel,
                        placeholder = titlePlaceholder,
                        testTag = "editor_title_input"
                    )

                    NestTextField(
                        value = uiState.subtitle,
                        onValueChange = { viewModel.updateSubtitle(it) },
                        label = subtitleLabel,
                        placeholder = subtitlePlaceholder,
                        testTag = "editor_subtitle_input"
                    )

                    NestTextField(
                        value = uiState.payload,
                        onValueChange = { viewModel.updatePayload(it) },
                        label = payloadLabel,
                        placeholder = payloadPlaceholder,
                        isPassword = true,
                        testTag = "editor_payload_input"
                    )
                }
            }

            // Tags Management Card
            NestCard(
                cornerRadius = 28.dp,
                padding = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Container Tags",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NestTextField(
                            value = uiState.newTagInput,
                            onValueChange = { viewModel.updateNewTagInput(it) },
                            label = "Add Tag",
                            placeholder = "e.g. Work, Finance, 2FA",
                            modifier = Modifier.weight(1f),
                            testTag = "editor_tag_input"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(colors.primaryAccent)
                                .clickable { viewModel.addTag() }
                                .testTag("add_tag_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Tag",
                                tint = colors.card
                            )
                        }
                    }

                    if (uiState.tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.tags.forEach { tag ->
                                TagChip(
                                    tag = tag,
                                    onRemove = { viewModel.removeTag(tag) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save Action Button
            NestButton(
                text = if (itemId != null) "Update & Save Revision" else "Encrypt & Save Container",
                onClick = { viewModel.saveItem(itemId) },
                variant = NestButtonVariant.PRIMARY,
                icon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                testTag = "editor_save_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit
) {
    val colors = LocalNestColors.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.secondaryBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Tag,
                contentDescription = null,
                tint = colors.primaryAccent,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tag,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove tag",
                tint = colors.secondaryText,
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onRemove() }
            )
        }
    }
}
