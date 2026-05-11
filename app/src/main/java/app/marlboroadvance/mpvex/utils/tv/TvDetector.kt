package app.marlboroadvance.mpvex.utils.tv

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Detects whether the current device is an Android TV or similar TV-class device.
 *
 * Uses multiple signals for reliable detection:
 * 1. UiModeManager type == TELEVISION
 * 2. Leanback feature availability
 * 3. Touchscreen hardware absence
 *
 * Results are cached per-context to avoid repeated system queries.
 */
object TvDetector {
  @Volatile
  private var cachedResult: Boolean? = null

  /**
   * Returns true if the current device is a TV (Android TV, Fire TV, Google TV, etc.).
   * Result is cached after first call.
   */
  fun isTV(context: Context): Boolean {
    cachedResult?.let { return it }

    val result = detectTV(context)
    cachedResult = result
    return result
  }

  private fun detectTV(context: Context): Boolean {
    // Primary signal: UiModeManager reports TV mode
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
      return true
    }

    // Secondary signal: device has leanback feature
    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
      return true
    }

    // Tertiary signal: no touchscreen hardware (most TV boxes)
    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
      return true
    }

    // Fire TV detection via manufacturer/model
    if (Build.MANUFACTURER.equals("Amazon", ignoreCase = true) &&
      Build.MODEL.contains("AFT", ignoreCase = true)
    ) {
      return true
    }

    return false
  }

  /**
   * Clears the cached detection result. Useful for testing.
   */
  fun clearCache() {
    cachedResult = null
  }
}

/**
 * CompositionLocal providing whether the current device is a TV.
 * Defaults to false — set at the top-level theme or activity.
 */
val LocalIsTV = compositionLocalOf { false }

/**
 * Convenience accessor for checking TV mode from any composable.
 * Uses LocalContext for initial detection, then reads the CompositionLocal.
 *
 * Usage:
 * ```
 * if (isTV) {
 *   // TV-specific UI
 * } else {
 *   // Mobile UI
 * }
 * ```
 */
val isTV: Boolean
  @Composable
  @ReadOnlyComposable
  get() = LocalIsTV.current
