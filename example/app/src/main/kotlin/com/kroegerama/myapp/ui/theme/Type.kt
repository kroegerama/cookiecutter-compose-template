package com.kroegerama.myapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@Preview(device = "spec:width=2000px,height=3500px,dpi=440")
@Composable
private fun Typography() {
    AppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text("Default", Modifier.align(Alignment.CenterHorizontally))
                    listOf(
                        "displayLarge" to MaterialTheme.typography.displayLarge,
                        "displayMedium" to MaterialTheme.typography.displayMedium,
                        "displaySmall" to MaterialTheme.typography.displaySmall,
                        "headlineLarge" to MaterialTheme.typography.headlineLarge,
                        "headlineMedium" to MaterialTheme.typography.headlineMedium,
                        "headlineSmall" to MaterialTheme.typography.headlineSmall,
                        "titleLarge" to MaterialTheme.typography.titleLarge,
                        "titleMedium" to MaterialTheme.typography.titleMedium,
                        "titleSmall" to MaterialTheme.typography.titleSmall,
                        "bodyLarge" to MaterialTheme.typography.bodyLarge,
                        "bodyMedium" to MaterialTheme.typography.bodyMedium,
                        "bodySmall" to MaterialTheme.typography.bodySmall,
                        "labelLarge" to MaterialTheme.typography.labelLarge,
                        "labelMedium" to MaterialTheme.typography.labelMedium,
                        "labelSmall" to MaterialTheme.typography.labelSmall,
                    ).forEach { (name, style) ->
                        Text(
                            text = name,
                            style = style,
                            maxLines = 1,
                            overflow = TextOverflow.MiddleEllipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "(${style.fontSize}, w${style.fontWeight?.weight})",
                            fontSize = 12.sp
                        )
                        HorizontalDivider(modifier = Modifier.height(4.dp))
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text("Emphasized", Modifier.align(Alignment.CenterHorizontally))
                    listOf(
                        "displayLargeEmphasized" to MaterialTheme.typography.displayLargeEmphasized,
                        "displayMediumEmphasized" to MaterialTheme.typography.displayMediumEmphasized,
                        "displaySmallEmphasized" to MaterialTheme.typography.displaySmallEmphasized,
                        "headlineLargeEmphasized" to MaterialTheme.typography.headlineLargeEmphasized,
                        "headlineMediumEmphasized" to MaterialTheme.typography.headlineMediumEmphasized,
                        "headlineSmallEmphasized" to MaterialTheme.typography.headlineSmallEmphasized,
                        "titleLargeEmphasized" to MaterialTheme.typography.titleLargeEmphasized,
                        "titleMediumEmphasized" to MaterialTheme.typography.titleMediumEmphasized,
                        "titleSmallEmphasized" to MaterialTheme.typography.titleSmallEmphasized,
                        "bodyLargeEmphasized" to MaterialTheme.typography.bodyLargeEmphasized,
                        "bodyMediumEmphasized" to MaterialTheme.typography.bodyMediumEmphasized,
                        "bodySmallEmphasized" to MaterialTheme.typography.bodySmallEmphasized,
                        "labelLargeEmphasized" to MaterialTheme.typography.labelLargeEmphasized,
                        "labelMediumEmphasized" to MaterialTheme.typography.labelMediumEmphasized,
                        "labelSmallEmphasized" to MaterialTheme.typography.labelSmallEmphasized,
                    ).forEach { (name, style) ->
                        Text(
                            text = name,
                            style = style,
                            maxLines = 1,
                            overflow = TextOverflow.MiddleEllipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "(${style.fontSize}, w${style.fontWeight?.weight})",
                            fontSize = 12.sp
                        )
                        HorizontalDivider(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
