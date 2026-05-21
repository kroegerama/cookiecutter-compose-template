package {{ cookiecutter.namespace }}.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import {{ cookiecutter.namespace }}.ui.theme.dimensions

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.medium),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .width(IntrinsicSize.Max)
            .heightIn(min = ButtonDefaults.MediumContainerHeight)
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = checked,
                enabled = enabled,
                role = Role.Switch
            ) {
                onCheckedChange(!checked)
            }
            .padding(
                horizontal = MaterialTheme.dimensions.medium
            )
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

@Preview
@Composable
private fun SwitchPreview() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Switch(
                    checked = false,
                    onCheckedChange = {},
                    label = "Hello World",
                    modifier = Modifier.fillMaxWidth()
                )
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    label = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
