package com.ledgergreen.terminal.ui.common.ledgergreenicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.ledgergreen.terminal.ui.common.LedgerGreenIcons

public val LedgerGreenIcons.IcScan: ImageVector
    get() {
        if (_icScan != null) {
            return _icScan!!
        }
        _icScan = Builder(name = "IcScan", defaultWidth = 27.0.dp, defaultHeight = 26.0.dp,
                viewportWidth = 27.0f, viewportHeight = 26.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(2.4529f, 9.9938f)
                curveTo(2.4525f, 10.5331f, 2.0076f, 10.9702f, 1.4588f, 10.9702f)
                curveTo(0.9096f, 10.9702f, 0.4646f, 10.5332f, 0.4646f, 9.9938f)
                verticalLineTo(5.1123f)
                curveTo(0.4646f, 3.8176f, 0.9882f, 2.576f, 1.9203f, 1.6602f)
                curveTo(2.8524f, 0.7448f, 4.1169f, 0.2306f, 5.4352f, 0.2306f)
                horizontalLineTo(10.4053f)
                curveTo(10.9545f, 0.2306f, 11.3995f, 0.6676f, 11.3995f, 1.2069f)
                curveTo(11.3995f, 1.7463f, 10.9545f, 2.1833f, 10.4053f, 2.1833f)
                horizontalLineTo(5.4352f)
                curveTo(4.6441f, 2.1833f, 3.8854f, 2.4918f, 3.3261f, 3.041f)
                curveTo(2.7669f, 3.5903f, 2.4528f, 4.3354f, 2.4528f, 5.1124f)
                lineTo(2.4529f, 9.9938f)
                close()
                moveTo(24.3223f, 9.9938f)
                verticalLineTo(5.1123f)
                curveTo(24.3223f, 4.3354f, 24.0082f, 3.5903f, 23.449f, 3.041f)
                curveTo(22.8897f, 2.4917f, 22.1311f, 2.1833f, 21.3399f, 2.1833f)
                horizontalLineTo(16.3698f)
                curveTo(15.8206f, 2.1833f, 15.3756f, 1.7463f, 15.3756f, 1.2069f)
                curveTo(15.3756f, 0.6675f, 15.8206f, 0.2305f, 16.3698f, 0.2305f)
                horizontalLineTo(21.3403f)
                horizontalLineTo(21.3399f)
                curveTo(22.6582f, 0.2305f, 23.9224f, 0.7447f, 24.8548f, 1.6602f)
                curveTo(25.7869f, 2.576f, 26.3105f, 3.8176f, 26.3105f, 5.1123f)
                verticalLineTo(9.9937f)
                curveTo(26.3105f, 10.533f, 25.8655f, 10.9701f, 25.3163f, 10.9701f)
                curveTo(24.7671f, 10.9701f, 24.3222f, 10.5331f, 24.3222f, 9.9937f)
                lineTo(24.3223f, 9.9938f)
                close()
                moveTo(2.4529f, 15.8519f)
                verticalLineTo(20.7336f)
                verticalLineTo(20.7333f)
                curveTo(2.4529f, 21.5103f, 2.7669f, 22.2554f, 3.3262f, 22.8047f)
                curveTo(3.8854f, 23.3539f, 4.6441f, 23.6624f, 5.4352f, 23.6624f)
                horizontalLineTo(10.4054f)
                curveTo(10.669f, 23.6624f, 10.9221f, 23.7651f, 11.1085f, 23.9482f)
                curveTo(11.2949f, 24.1316f, 11.3999f, 24.3799f, 11.3999f, 24.6387f)
                curveTo(11.3999f, 24.8976f, 11.2949f, 25.1462f, 11.1085f, 25.3293f)
                curveTo(10.9221f, 25.5124f, 10.669f, 25.6151f, 10.4054f, 25.6151f)
                horizontalLineTo(5.4352f)
                curveTo(4.117f, 25.6151f, 2.8525f, 25.1009f, 1.9204f, 24.1854f)
                curveTo(0.9882f, 23.2699f, 0.4647f, 22.028f, 0.4647f, 20.7333f)
                verticalLineTo(15.8519f)
                curveTo(0.4647f, 15.3125f, 0.9096f, 14.8758f, 1.4588f, 14.8758f)
                curveTo(2.0076f, 14.8758f, 2.4526f, 15.3125f, 2.453f, 15.8519f)
                horizontalLineTo(2.4529f)
                close()
                moveTo(24.3223f, 15.8519f)
                curveTo(24.3223f, 15.3125f, 24.7672f, 14.8755f, 25.3164f, 14.8755f)
                curveTo(25.8656f, 14.8755f, 26.3106f, 15.3125f, 26.3106f, 15.8519f)
                verticalLineTo(20.7336f)
                verticalLineTo(20.7333f)
                curveTo(26.3106f, 22.028f, 25.787f, 23.2699f, 24.8549f, 24.1854f)
                curveTo(23.9224f, 25.1009f, 22.6583f, 25.6151f, 21.34f, 25.6151f)
                horizontalLineTo(16.3698f)
                curveTo(16.1059f, 25.6151f, 15.8531f, 25.5124f, 15.6667f, 25.3293f)
                curveTo(15.4803f, 25.1462f, 15.3754f, 24.8976f, 15.3754f, 24.6387f)
                curveTo(15.3754f, 24.3799f, 15.4803f, 24.1316f, 15.6667f, 23.9482f)
                curveTo(15.8531f, 23.7651f, 16.1059f, 23.6624f, 16.3698f, 23.6624f)
                horizontalLineTo(21.3404f)
                horizontalLineTo(21.34f)
                curveTo(22.1311f, 23.6624f, 22.8898f, 23.3539f, 23.4491f, 22.8047f)
                curveTo(24.0083f, 22.2554f, 24.3224f, 21.5103f, 24.3224f, 20.7333f)
                lineTo(24.3223f, 15.8519f)
                close()
                moveTo(1.4588f, 11.9465f)
                horizontalLineTo(25.3164f)
                curveTo(25.58f, 11.9465f, 25.8331f, 12.0492f, 26.0195f, 12.2322f)
                curveTo(26.2059f, 12.4153f, 26.3109f, 12.6639f, 26.3109f, 12.9228f)
                curveTo(26.3109f, 13.1817f, 26.2059f, 13.4303f, 26.0195f, 13.6134f)
                curveTo(25.8331f, 13.7965f, 25.58f, 13.8992f, 25.3164f, 13.8992f)
                horizontalLineTo(1.4588f)
                curveTo(0.9096f, 13.8992f, 0.465f, 13.4618f, 0.465f, 12.9228f)
                curveTo(0.465f, 12.3838f, 0.9096f, 11.9464f, 1.4588f, 11.9464f)
                verticalLineTo(11.9465f)
                close()
            }
        }
        .build()
        return _icScan!!
    }

private var _icScan: ImageVector? = null
