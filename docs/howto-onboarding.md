# How to add an onboarding screen

This guide adds a pre-login onboarding flow (welcome pages with Skip / Next / Get started) to an app generated from this template. It is written
against the `example` app, which is the source of truth; mirror the changes into `{{ cookiecutter.project_slug }}` afterwards.

## Approach

The app already has one top-level switch: `MainActivityContent` shows either the logged-in graph or the login graph, driven by
`MainActivityViewModel.loggedIn`. Onboarding lives underneath the logged-out branch as the first entry of the login back stack. That keeps it a
regular Navigation 3 screen with push/pop transitions, predictive back, its own ViewModel store, and the same `Screen` / `Actions` / `Content` /
preview structure as `LoginScreen`.

A persisted version number decides whether onboarding is shown. Bumping the constant re-shows onboarding after a major release.

If onboarding must also run for already-logged-in users (profile setup, permission requests after sign-in), model it as a third state of the top-level
switch instead: a sealed app state with `Onboarding` / `Login` / `LoggedIn` driving the `AnimatedContent`. The rest of this guide covers the pre-login
case.

## 1. Persist the flag in its own store

Do not put the flag into the existing app `DataStore` (`controller/DataStore.kt`). `LogoutHandler` clears that store whenever the session flow emits
`false`, which includes every cold start while logged out. A user who finished onboarding, landed on the login screen and relaunched the app would see
onboarding again.

Create `controller/OnboardingStore.kt` with a separate preferences file that survives logout:

```kotlin
private val Context.deviceSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "device-settings",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

@Singleton
class OnboardingStore @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStore = context.deviceSettingsDataStore

    val completedFlow: Flow<Boolean> = dataStore.flow { preferences ->
        (preferences[KEY_ONBOARDING_VERSION] ?: 0) >= ONBOARDING_VERSION
    }.distinctUntilChanged()

    suspend fun markCompleted() {
        dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_VERSION] = ONBOARDING_VERSION
        }
    }

    companion object {
        // bump to show onboarding again after a major release
        private const val ONBOARDING_VERSION = 1
        private val KEY_ONBOARDING_VERSION = intPreferencesKey("onboarding_version")
    }
}
```

`dataStore.flow { }` is the kaiteki helper already used by `SessionStore`.

## 2. Combine the flag into the start state

`MainActivityViewModel` currently exposes `loggedIn: StateFlow<Boolean?>`, and `MainActivity.fromSplash` keeps the splash screen visible until it is
non-null. The login back stack picks its start key from the onboarding flag, so the flag has to be part of that same gate. Replace `loggedIn` with a
combined state:

```kotlin
data class StartState(
    val loggedIn: Boolean,
    val onboardingCompleted: Boolean
)

// null = not yet loaded; the splash screen stays visible until it resolves
val startState: StateFlow<StartState?> = combine(
    sessionStore.loggedInFlow,
    onboardingStore.completedFlow,
    ::StartState
).stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = null
)
```

Update the two readers:

- `MainActivity.fromSplash`: `val sessionLoaded = viewModel.startState.value != null`
- `MainActivityContent`: collect `startState`, key the `AnimatedContent` on `state.loggedIn`, and pass `state.onboardingCompleted` down to
  `LoginContent`.

## 3. Add the nav key and entry

In `ui/navigation/LoginNavKey.kt`:

```kotlin
sealed interface LoginNavKey : NavKey {

    @Serializable
    data object Onboarding : LoginNavKey

    @Serializable
    data object Login : LoginNavKey

}

fun loginEntryProvider(
    backStack: NavBackStack<NavKey>
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<LoginNavKey.Onboarding> {
        OnboardingScreen(
            backStack = backStack
        )
    }
    entry<LoginNavKey.Login> {
        LoginScreen(
            backStack = backStack
        )
    }
}
```

## 4. Pick the start key

In `MainActivityContent.LoginContent`, choose the initial entry from the flag. `rememberNavBackStack` only uses the argument on first composition and
restores the stack through `rememberSerializable`, so process death and later flag changes do not reset the stack.

```kotlin
val backStack = rememberNavBackStack(
    if (onboardingCompleted) LoginNavKey.Login else LoginNavKey.Onboarding
)
val viewModelStoreProvider = rememberViewModelStoreProvider(key = LoginNavKey.Onboarding)
```

## 5. The screen

Create `ui/screens/OnboardingScreen.kt` following the `LoginScreen` layout. The content is a `HorizontalPager` over a fixed page list with a page
indicator and Skip / Next / Get started buttons.

```kotlin
@Composable
fun OnboardingScreen(backStack: NavBackStack<NavKey>) {
    val viewModel = hiltViewModel<OnboardingScreenViewModel>()

    val actions = OnboardingScreenActions(
        onFinish = {
            viewModel.markCompleted()
            backStack.clear()
            backStack.add(LoginNavKey.Login)
        }
    )

    OnboardingScreenContent(
        pages = OnboardingPages,
        actions = actions
    )
}

private data class OnboardingScreenActions(
    val onFinish: () -> Unit = {}
)

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val body: String
)

private val OnboardingPages = listOf(
    OnboardingPage(AppIcons.Cookie, "Welcome", "A short introduction to the app."),
    OnboardingPage(AppIcons.Cookie, "Stay in sync", "Your data follows you across devices."),
    OnboardingPage(AppIcons.Cookie, "Ready", "Sign in to get started.")
)

@Composable
private fun OnboardingScreenContent(
    pages: List<OnboardingPage>,
    actions: OnboardingScreenActions
) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(MaterialTheme.dimensions.medium)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val page = pages[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(page.icon, contentDescription = null, modifier = Modifier.size(96.dp))
                    Text(page.title, style = MaterialTheme.typography.headlineMedium)
                    Text(page.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
            }
            PageIndicator(pageCount = pages.size, currentPage = pagerState.currentPage)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = dropUnlessResumed { actions.onFinish() }) {
                    Text("Skip")
                }
                ButtonMedium(
                    onClick = dropUnlessResumed {
                        if (isLast) {
                            actions.onFinish()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                    text = if (isLast) "Get started" else "Next"
                )
            }
        }
    }
}

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val onboardingStore: OnboardingStore
) : ViewModel() {

    fun markCompleted() {
        viewModelScope.launch {
            onboardingStore.markCompleted()
        }
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    AppTheme {
        OnboardingScreenContent(
            pages = OnboardingPages,
            actions = OnboardingScreenActions()
        )
    }
}
```

`PageIndicator` is a row of small circles, one per page, with the current one filled in the primary color. Material 3 does not ship one, so it is a
private composable in the same file.

Onboarding is the root of the login stack, so back exits the app, which is the expected behaviour for a welcome flow.

## 6. Verify

1. Fresh install: onboarding appears after the splash, "Get started" lands on login.
2. Kill and relaunch while still logged out: login shows directly, onboarding does not return.
3. Log in, log out: login shows, onboarding does not return.
4. Bump `ONBOARDING_VERSION`: onboarding shows once more.
5. Rotate or trigger process death on the onboarding screen: the pager position and the screen are restored.

## Files touched

| File                             | Change                         |
|----------------------------------|--------------------------------|
| `controller/OnboardingStore.kt`  | new, separate preferences file |
| `ui/screens/OnboardingScreen.kt` | new screen, ViewModel, preview |
| `ui/navigation/LoginNavKey.kt`   | `Onboarding` key and entry     |
| `ui/MainActivityViewModel.kt`    | combined `startState`          |
| `ui/MainActivityContent.kt`      | start key from the flag        |
| `MainActivity.kt`                | splash waits for `startState`  |

After the example is done, apply the same edits to `{{ cookiecutter.project_slug }}` and confirm by generating a project with the example's values and
diffing it against `example/`.
