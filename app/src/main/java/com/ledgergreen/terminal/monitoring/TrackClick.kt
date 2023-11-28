@file:OptIn(ExperimentalTrackingApi::class)

package com.ledgergreen.terminal.monitoring

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.datadog.android.compose.ExperimentalTrackingApi
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@OptIn(ExperimentalTrackingApi::class)
@Composable
fun trackClick(
    targetName: String,
    attributes: ImmutableMap<String, Any?> = remember { persistentMapOf() },
    onClick: () -> Unit,
): () -> Unit =
    com.datadog.android.compose.trackClick(
        targetName = targetName,
        attributes = attributes,
        onClick = onClick,
    )
