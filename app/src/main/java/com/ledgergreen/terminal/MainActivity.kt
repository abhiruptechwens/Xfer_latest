package com.ledgergreen.terminal

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.app.AppState1
import com.ledgergreen.terminal.app.Changelog
import com.ledgergreen.terminal.idle.IdleLocker
import com.ledgergreen.terminal.idle.LocalIdleLocker
import com.ledgergreen.terminal.sfx.SfxEffect
import com.ledgergreen.terminal.ui.navigation.TerminalNavGraph
import com.ledgergreen.terminal.ui.theme.LedgerGreenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var idleLocker: IdleLocker

    @Inject
    lateinit var sfxEffect: SfxEffect

//    val packageInfo = packageManager.getPackageInfo(packageName, 0)
//
//    val versionName = packageInfo.versionName
//    @RequiresApi(Build.VERSION_CODES.P)
//    val versionCode = packageInfo.longVersionCode


    // changelog update
//    private val changelog = Changelog(
//        version = "594",
//        changes = listOf(
//            "Various bug fixes"
//        )
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = 0xFF00386D.toInt()

//        NewRelic.withApplicationToken("GENERATED_TOKEN").start(this.getApplication());

        setContent {
            LedgerGreenTheme {
                TerminalNavGraph()

                //changelog update
//                val isFirstLaunchAfterUpdate by remember{ mutableStateOf(checkIfFirstLaunchAfterUpdate())}
//
//                // Display the changelog dialog for the first launch after an update
//                if (isFirstLaunchAfterUpdate) {
//                    ChangelogDialog(changelog = changelog) {
//                        // Update the preferences to indicate that the changelog has been displayed
//                        updateChangelogDisplayed()
//                    }
//                }


            }
//            CompositionLocalProvider(LocalIdleLocker provides idleLocker) {
//
//            }
        }
    }

    //changelog update
//    private fun checkIfFirstLaunchAfterUpdate(): Boolean {
//        val lastVersion = getStoredAppVersion()
//        return lastVersion != changelog.version
//    }

//    private fun getStoredAppVersion(): String {
//        // Retrieve the stored app version from preferences
//        return "1.1.0" // Replace with your actual code to retrieve the stored version
//    }

    private fun updateChangelogDisplayed() {
        // Update preferences to indicate that the changelog has been displayed for the current version
        // Store the current version for future reference
        // ...
    }






    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // intercept keyboard events and treat it as user activity
//        idleLocker.onActivityDetected()
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // intercept touch events and treat it as user activity
//        idleLocker.onActivityDetected()
        return super.dispatchTouchEvent(event)
    }

//    companion object {
//        private const val PREFS_NAME = "ChangelogPrefs"
//        private const val KEY_LAST_VERSION = "last_version"
//    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun ChangelogDialog(changelog: Changelog, onDismiss: () -> Unit) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text(
//                text = stringResource(id = R.string.changlog_title, changelog.version),
//                style = MaterialTheme.typography.h6,
//            )
//        },
//        text = {
//            Column {
//                changelog.changes.forEach { change ->
//                    ListItem(
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Default.Info,
//                                contentDescription = null,
//                                modifier = Modifier.padding(end = 8.dp)
//                            )
//                        },
//                        text = {
//                            Text(text = change)
//                        }
//                    )
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(
//                onClick = onDismiss
//            ) {
//                Text(text = stringResource(id = R.string.ok))
//            }
//        }
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ChangelogDialogPreview() {
//    val changelog = Changelog(
//        version = "1.2.0",
//        changes = listOf(
//            "New feature: Added dark mode support.",
//            "Bug fixes and improvements."
//        )
//    )
//    ChangelogDialog(changelog = changelog, onDismiss = {})
//}
