package com.ledgergreen.terminal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = 0xFF00386D.toInt()

        setContent {
            LedgerGreenTheme {
                TerminalNavGraph()
            }
//            CompositionLocalProvider(LocalIdleLocker provides idleLocker) {
//
//            }
        }
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
}
