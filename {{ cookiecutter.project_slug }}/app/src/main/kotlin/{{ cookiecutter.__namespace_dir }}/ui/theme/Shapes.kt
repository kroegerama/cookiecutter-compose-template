package {{ cookiecutter.namespace }}.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.0.dp),
    small = RoundedCornerShape(8.0.dp),
    medium = RoundedCornerShape(12.0.dp),
    large = RoundedCornerShape(16.0.dp),
    largeIncreased = RoundedCornerShape(20.0.dp),
    extraLarge = RoundedCornerShape(28.0.dp),
    extraLargeIncreased = RoundedCornerShape(32.0.dp),
    extraExtraLarge = RoundedCornerShape(48.0.dp)
)

@Preview(device = "spec:width=1080px,height=3500px,dpi=440")
@Composable
private fun ShapesPreview() {
    AppTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                listOf(
                    "extraSmall" to MaterialTheme.shapes.extraSmall,
                    "small" to MaterialTheme.shapes.small,
                    "medium" to MaterialTheme.shapes.medium,
                    "large" to MaterialTheme.shapes.large,
                    "extraLarge" to MaterialTheme.shapes.extraLarge,
                    "largeIncreased" to MaterialTheme.shapes.largeIncreased,
                    "extraLargeIncreased" to MaterialTheme.shapes.extraLargeIncreased,
                    "extraExtraLarge" to MaterialTheme.shapes.extraExtraLarge,
                ).forEach { (name, shape) ->
                    Card(
                        shape = shape,
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .padding(8.dp)
                        )
                        Text(
                            shape.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 8.dp,
                            )
                        )
                    }
                }
            }
        }
    }
}
