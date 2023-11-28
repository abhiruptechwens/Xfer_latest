package com.ledgergreen.terminal.ui.common.ledgergreenicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.ui.common.LedgerGreenIcons

public val LedgerGreenIcons.IcScanner: ImageVector
    get() {
        if (_icScanner != null) {
            return _icScanner!!
        }
        _icScanner = Builder(
            name = "IcScanner", defaultWidth = 512.0.dp, defaultHeight = 512.0.dp,
            viewportWidth = 512.0f, viewportHeight = 512.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 32.0f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(336.0f, 448.0f)
                horizontalLineToRelative(56.0f)
                arcToRelative(56.0f, 56.0f, 0.0f, false, false, 56.0f, -56.0f)
                verticalLineTo(336.0f)
            }
            path(
                fill = SolidColor(Color(0x00FFFFFF)), stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 32.0f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(448.0f, 176.0f)
                verticalLineTo(120.0f)
                arcToRelative(56.0f, 56.0f, 0.0f, false, false, -56.0f, -56.0f)
                horizontalLineTo(336.0f)
            }
            path(
                fill = SolidColor(Color(0x00FFFFFF)), stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 32.0f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(176.0f, 448.0f)
                horizontalLineTo(120.0f)
                arcToRelative(56.0f, 56.0f, 0.0f, false, true, -56.0f, -56.0f)
                verticalLineTo(336.0f)
            }
            path(
                fill = SolidColor(Color(0x00FFFFFF)), stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 32.0f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.0f, 176.0f)
                verticalLineTo(120.0f)
                arcToRelative(56.0f, 56.0f, 0.0f, false, true, 56.0f, -56.0f)
                horizontalLineToRelative(56.0f)
            }
        }
            .build()
        return _icScanner!!
    }

private var _icScanner: ImageVector? = null
