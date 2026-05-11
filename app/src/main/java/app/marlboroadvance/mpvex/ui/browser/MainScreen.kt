package app.marlboroadvance.mpvex.ui.browser

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.marlboroadvance.mpvex.presentation.Screen
import app.marlboroadvance.mpvex.ui.browser.folderlist.FolderListScreen
import app.marlboroadvance.mpvex.ui.browser.networkstreaming.NetworkStreamingScreen
import app.marlboroadvance.mpvex.ui.browser.playlist.PlaylistScreen
import app.marlboroadvance.mpvex.ui.browser.recentlyplayed.RecentlyPlayedScreen
import app.marlboroadvance.mpvex.ui.browser.selection.SelectionManager
import app.marlboroadvance.mpvex.ui.focus.TvFocusConstants
import app.marlboroadvance.mpvex.ui.focus.tvFocusable
import app.marlboroadvance.mpvex.utils.tv.isTV
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Serializable
object MainScreen : Screen {
  // Use a companion object to store state more persistently
  // Internal visibility so nested objects can access
  internal var persistentSelectedTab: Int = 0

  // Shared state that can be updated by FileSystemBrowserScreen
  @Volatile
  internal var isInSelectionModeShared: Boolean = false  // Controls FAB visibility

  @Volatile
  internal var shouldHideNavigationBar: Boolean = false  // Controls navigation bar visibility

  @Volatile
  internal var isBrowserBottomBarVisible: Boolean = false  // Tracks browser bottom bar visibility

  @Volatile
  internal var sharedVideoSelectionManager: Any? = null

  // Check if the selection contains only videos and update navigation bar visibility accordingly
  @Volatile
  internal var onlyVideosSelected: Boolean = false

  // Track when permission denied screen is showing to hide FAB
  @Volatile
  internal var isPermissionDenied: Boolean = false
  
  /**
   * Update selection state and navigation bar visibility
   * This method should be called whenever selection changes
   */
  fun updateSelectionState(
    isInSelectionMode: Boolean,
    isOnlyVideosSelected: Boolean,
    selectionManager: Any?
  ) {
    this.isInSelectionModeShared = isInSelectionMode
    this.onlyVideosSelected = isOnlyVideosSelected
    this.sharedVideoSelectionManager = selectionManager
    
    // Only hide navigation bar when videos are selected AND in selection mode
    // This fixes the issue where bottom bar disappears when only videos are selected
    this.shouldHideNavigationBar = isInSelectionMode && isOnlyVideosSelected
  }
  
  /**
   * Update permission state to control FAB visibility
   */
  fun updatePermissionState(isDenied: Boolean) {
    this.isPermissionDenied = isDenied
  }

  /**
   * Get current permission denied state
   */
  fun getPermissionDeniedState(): Boolean = isPermissionDenied

  /**
   * Update bottom navigation bar visibility based on floating bottom bar state
   */
  fun updateBottomBarVisibility(shouldShow: Boolean) {
    // Hide bottom navigation when floating bottom bar is visible
    this.shouldHideNavigationBar = !shouldShow
  }

  @Composable
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun Content() {
    if (isTV) {
      MainScreenTV.Content()
    } else {
      MainScreenMobile.Content()
    }
  }
}

/**
 * Mobile layout with bottom navigation bar.
 */
private object MainScreenMobile {
  @Composable
  fun Content() {
    var selectedTab by remember {
      mutableIntStateOf(persistentSelectedTab)
    }

    val density = LocalDensity.current

    // Shared state (across the app)
    val isInSelectionMode = remember { mutableStateOf(isInSelectionModeShared) }
    val hideNavigationBar = remember { mutableStateOf(shouldHideNavigationBar) }
    val videoSelectionManager = remember { mutableStateOf<SelectionManager<*, *>?>(sharedVideoSelectionManager as? SelectionManager<*, *>) }

    // Check for state changes to ensure UI updates
    LaunchedEffect(Unit) {
      while (true) {
        // Update FAB visibility state
        if (isInSelectionMode.value != isInSelectionModeShared) {
          isInSelectionMode.value = isInSelectionModeShared
        }

        // Update navigation bar visibility state
        if (hideNavigationBar.value != shouldHideNavigationBar) {
          hideNavigationBar.value = shouldHideNavigationBar
        }

        // Update selection manager
        val currentManager = sharedVideoSelectionManager as? SelectionManager<*, *>
        if (videoSelectionManager.value != currentManager) {
          videoSelectionManager.value = currentManager
        }

        delay(16)
      }
    }

    // Update persistent state whenever tab changes
    LaunchedEffect(selectedTab) {
      persistentSelectedTab = selectedTab
    }

    // Scaffold with bottom navigation bar
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
        AnimatedVisibility(
          visible = !hideNavigationBar.value,
          enter = slideInVertically(
            animationSpec = tween(durationMillis = 300),
            initialOffsetY = { fullHeight -> fullHeight }
          ),
          exit = slideOutVertically(
            animationSpec = tween(durationMillis = 300),
            targetOffsetY = { fullHeight -> fullHeight }
          )
        ) {
          NavigationBar(
            modifier = Modifier
              .clip(
                RoundedCornerShape(
                  topStart = 28.dp,
                  topEnd = 28.dp,
                  bottomStart = 0.dp,
                  bottomEnd = 0.dp
                )
              )
          ) {
            NavigationBarItem(
              icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
              label = { Text("Home") },
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 }
            )
            NavigationBarItem(
              icon = { Icon(Icons.Filled.History, contentDescription = "Recents") },
              label = { Text("Recents") },
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 }
            )
            NavigationBarItem(
              icon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = "Playlists") },
              label = { Text("Playlists") },
              selected = selectedTab == 2,
              onClick = { selectedTab = 2 }
            )
            NavigationBarItem(
              icon = { Icon(Icons.Filled.Language, contentDescription = "Network") },
              label = { Text("Network") },
              selected = selectedTab == 3,
              onClick = { selectedTab = 3 }
            )
          }
        }
      }
    ) { paddingValues ->
      Box(modifier = Modifier.fillMaxSize()) {
        val fabBottomPadding = 80.dp

        AnimatedContent(
          targetState = selectedTab,
          transitionSpec = {
            val slideDistance = with(density) { 48.dp.roundToPx() }
            val animationDuration = 250

            if (targetState > initialState) {
              (slideInHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                initialOffsetX = { slideDistance }
              ) + fadeIn(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                targetOffsetX = { -slideDistance }
              ) + fadeOut(
                animationSpec = tween(durationMillis = animationDuration / 2, easing = FastOutSlowInEasing)
              ))
            } else {
              (slideInHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                initialOffsetX = { -slideDistance }
              ) + fadeIn(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                targetOffsetX = { slideDistance }
              ) + fadeOut(
                animationSpec = tween(durationMillis = animationDuration / 2, easing = FastOutSlowInEasing)
              ))
            }
          },
          label = "tab_animation"
        ) { targetTab ->
          CompositionLocalProvider(
            LocalNavigationBarHeight provides fabBottomPadding
          ) {
            when (targetTab) {
              0 -> FolderListScreen.Content()
              1 -> RecentlyPlayedScreen.Content()
              2 -> PlaylistScreen.Content()
              3 -> NetworkStreamingScreen.Content()
            }
          }
        }
      }
    }
  }
}

/**
 * TV layout with side rail navigation and focus support.
 */
private object MainScreenTV {
  data class TabItem(
    val index: Int,
    val icon: @Composable () -> Unit,
    val label: String,
  )

  @Composable
  fun Content() {
    var selectedTab by remember { mutableIntStateOf(persistentSelectedTab) }
    val density = LocalDensity.current

    // Focus requesters for navigation items
    val focusRequesters = remember { List(4) { FocusRequester() } }

    // Request focus on first item when launched
    LaunchedEffect(Unit) {
      delay(100) // Small delay for composition to settle
      focusRequesters[selectedTab].requestFocus()
    }

    // Update persistent state whenever tab changes
    LaunchedEffect(selectedTab) {
      persistentSelectedTab = selectedTab
    }

    val tabs = listOf(
      TabItem(0, { Icon(Icons.Filled.Home, contentDescription = null) }, "Home"),
      TabItem(1, { Icon(Icons.Filled.History, contentDescription = null) }, "Recents"),
      TabItem(2, { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null) }, "Playlists"),
      TabItem(3, { Icon(Icons.Filled.Language, contentDescription = null) }, "Network"),
    )

    Row(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(start = TvFocusConstants.OverscanPadding),
    ) {
      // Side rail navigation
      Surface(
        modifier = Modifier
          .width(120.dp)
          .fillMaxHeight()
          .padding(vertical = TvFocusConstants.OverscanPadding),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .selectableGroup(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Spacer(modifier = Modifier.height(16.dp))

          tabs.forEach { tab ->
            val isSelected = selectedTab == tab.index

            TvNavigationItem(
              icon = tab.icon,
              label = tab.label,
              selected = isSelected,
              onClick = { selectedTab = tab.index },
              focusRequester = focusRequesters[tab.index],
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
          }

          Spacer(modifier = Modifier.weight(1f))
        }
      }

      // Content area
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(TvFocusConstants.OverscanPadding)
      ) {
        AnimatedContent(
          targetState = selectedTab,
          transitionSpec = {
            val slideDistance = with(density) { 48.dp.roundToPx() }
            val animationDuration = 250

            if (targetState > initialState) {
              (slideInHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                initialOffsetX = { slideDistance }
              ) + fadeIn(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                targetOffsetX = { -slideDistance }
              ) + fadeOut(
                animationSpec = tween(durationMillis = animationDuration / 2, easing = FastOutSlowInEasing)
              ))
            } else {
              (slideInHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                initialOffsetX = { -slideDistance }
              ) + fadeIn(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing)
              )) togetherWith (slideOutHorizontally(
                animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
                targetOffsetX = { slideDistance }
              ) + fadeOut(
                animationSpec = tween(durationMillis = animationDuration / 2, easing = FastOutSlowInEasing)
              ))
            }
          },
          label = "tv_tab_animation"
        ) { targetTab ->
          CompositionLocalProvider(
            LocalNavigationBarHeight provides 0.dp
          ) {
            when (targetTab) {
              0 -> FolderListScreen.Content()
              1 -> RecentlyPlayedScreen.Content()
              2 -> PlaylistScreen.Content()
              3 -> NetworkStreamingScreen.Content()
            }
          }
        }
      }
    }
  }

  @Composable
  private fun TvNavigationItem(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
  ) {
    val backgroundColor = if (selected) {
      MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
      Color.Transparent
    }

    Box(
      modifier = modifier
        .height(TvFocusConstants.MinTouchTargetSize)
        .clip(RoundedCornerShape(12.dp))
        .background(backgroundColor)
        .selectable(
          selected = selected,
          onClick = onClick,
          role = Role.Tab,
        )
        .focusRequester(focusRequester)
        .tvFocusable(
          onClick = onClick,
          contentDescription = label,
        ),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = if (selected) {
            MaterialTheme.colorScheme.primary
          } else {
            MaterialTheme.colorScheme.onSurface
          },
        )
      }
    }
  }
}

// CompositionLocal for navigation bar height
val LocalNavigationBarHeight = compositionLocalOf { 0.dp }