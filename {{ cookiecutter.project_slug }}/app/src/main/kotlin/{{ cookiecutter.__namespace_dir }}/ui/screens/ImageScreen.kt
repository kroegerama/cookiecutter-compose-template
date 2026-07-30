package {{ cookiecutter.namespace }}.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.kroegerama.kmp.kaiteki.compose.components.OutlinedButtonSmall
import com.kroegerama.kmp.kaiteki.compose.feature.blurHash
import {{ cookiecutter.namespace }}.ui.navigation.Navigator
import {{ cookiecutter.namespace }}.ui.theme.AppTheme
import kotlin.time.Clock

@Composable
fun ImageScreen(
    navigator: Navigator
) {
    val actions = ImageScreenActions(
        onNavigate = navigator::navigate,
        onNavigateBack = navigator::goBack
    )
    ImageScreenContent(
        actions = actions
    )
}

data class ImageScreenActions(
    val onNavigate: (NavKey) -> Unit = {},
    val onNavigateBack: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ImageScreenContent(
    actions: ImageScreenActions
) {
    val scaffoldSceneScope = LocalListDetailSceneScope.current
    val needsBackButton = scaffoldSceneScope == null

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.safeDrawingPadding()) {
            if (needsBackButton) {
                OutlinedButtonSmall(
                    onClick = dropUnlessResumed { actions.onNavigateBack() },
                    text = "Back"
                )
            }
            Text("Details")

            AsyncImage(
                model = "",
                contentDescription = null
            )

            SubcomposeAsyncImage(
                model = "https://images-assets.nasa.gov/image/SLS_KSC_Artemis%20II%20Rollout%201172026_20/SLS_KSC_Artemis%20II%20Rollout%201172026_20~orig.jpg?r=${Clock.System.now().epochSeconds}",
                contentDescription = null,
                alignment = Alignment.Center,
                loading = {
                    LoadingIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(alignment)
                    )
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.33f)
                    .blurHash("VCABCqOu9ZbJv{0uxaxYR-oe*KM{t6s+S%o_t6jEayRj")
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageScreenPreview() {
    AppTheme {
        ImageScreenContent(
            actions = ImageScreenActions()
        )
    }
}
