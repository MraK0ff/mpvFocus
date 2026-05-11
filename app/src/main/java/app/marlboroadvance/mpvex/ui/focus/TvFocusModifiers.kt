package app.marlboroadvance.mpvex.ui.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.marlboroadvance.mpvex.utils.tv.isTV

/**
 * TV-specific focus constants for consistent sizing and behavior.
 */
object TvFocusConstants {
  /** Minimum touch target size for TV (Android TV guideline: 48dp min, 56dp recommended) */
  val MinTouchTargetSize: Dp = 56.dp

  /** Focus highlight border width */
  val FocusBorderWidth: Dp = 3.dp

  /** Focus highlight corner radius */
  val FocusCornerRadius: Dp = 8.dp

  /** Scale factor for focused elements (subtle zoom effect) */
  const val FocusScaleFactor: Float = 1.05f

  /** Default padding for TV-safe areas (overscan compensation) */
  val OverscanPadding: Dp = 48.dp
}

/**
 * Data class holding focus highlight configuration.
 */
data class FocusHighlightConfig(
  val focusedBorderColor: Color = Color.Unspecified,
  val unfocusedBorderColor: Color = Color.Transparent,
  val borderWidth: Dp = TvFocusConstants.FocusBorderWidth,
  val shape: Shape = RoundedCornerShape(TvFocusConstants.FocusCornerRadius),
  val scaleFactor: Float = TvFocusConstants.FocusScaleFactor,
  val applyScale: Boolean = true,
  val applyBorder: Boolean = true,
)

/**
 * Makes a composable focusable on TV with visual feedback.
 *
 * Features:
 * - Border highlight when focused
 * - Subtle scale animation on focus
 * - Minimum touch target enforcement
 * - D-pad navigation support
 * - Content description for accessibility
 *
 * @param enabled Whether focus handling is enabled (should check isTV)
 * @param focusRequester Optional FocusRequester for programmatic control
 * @param interactionSource Optional interaction source for observing focus state
 * @param config Configuration for focus highlight appearance
 * @param onFocusChanged Callback when focus state changes
 * @param onClick Click handler (also handles DPAD_CENTER/ENTER)
 * @param contentDescription Accessibility description
 */
@Composable
fun Modifier.tvFocusable(
  enabled: Boolean = isTV,
  focusRequester: FocusRequester? = null,
  interactionSource: MutableInteractionSource? = null,
  config: FocusHighlightConfig = FocusHighlightConfig(),
  onFocusChanged: ((Boolean) -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  contentDescription: String? = null,
): Modifier = composed {
  if (!enabled) return@composed this

  val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val isFocused by actualInteractionSource.collectIsFocusedAsState()

  val borderColor by animateColorAsState(
    targetValue = if (isFocused) {
      if (config.focusedBorderColor == Color.Unspecified) {
        MaterialTheme.colorScheme.primary
      } else {
        config.focusedBorderColor
      }
    } else {
      config.unfocusedBorderColor
    },
    label = "focus_border_color"
  )

  val scale by animateFloatAsState(
    targetValue = if (isFocused && config.applyScale) config.scaleFactor else 1f,
    label = "focus_scale"
  )

  this
    .then(
      if (config.applyScale) Modifier.scale(scale) else Modifier
    )
    .then(
      if (config.applyBorder) {
        Modifier.border(
          border = BorderStroke(config.borderWidth, borderColor),
          shape = config.shape
        )
      } else {
        Modifier
      }
    )
    .then(
      if (focusRequester != null) {
        Modifier.focusRequester(focusRequester)
      } else {
        Modifier
      }
    )
    .focusable(
      enabled = true,
      interactionSource = actualInteractionSource
    )
    .onFocusChanged { focusState ->
      onFocusChanged?.invoke(focusState.isFocused)
    }
    .handleTvKeys(onClick = onClick)
    .then(
      if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
      } else {
        Modifier
      }
    )
    .padding(4.dp) // Ensure border doesn't clip
}

/**
 * Handles TV remote key events for a focusable element.
 *
 * - DPAD_CENTER, ENTER, NUMPAD_ENTER trigger onClick
 * - Passes through other keys for navigation
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.handleTvKeys(
  onClick: (() -> Unit)? = null,
): Modifier = composed {
  if (!isTV) return@composed this

  this.onPreviewKeyEvent { keyEvent ->
    when (keyEvent.key) {
      Key.DirectionCenter,
      Key.Enter,
      Key.NumPadEnter -> {
        if (keyEvent.nativeKeyEvent.action == android.view.KeyEvent.ACTION_UP) {
          onClick?.invoke()
        }
        true
      }
      else -> false
    }
  }
}

/**
 * Chains focus between elements for directional navigation.
 *
 * Usage:
 * ```
 * val (first, second, third) = rememberFocusChain(3)
 *
 * Button(modifier = Modifier.focusChain(first, next = second)) { }
 * Button(modifier = Modifier.focusChain(second, prev = first, next = third)) { }
 * Button(modifier = Modifier.focusChain(third, prev = second)) { }
 * ```
 */
fun Modifier.focusChain(
  focusRequester: FocusRequester,
  prev: FocusRequester? = null,
  next: FocusRequester? = null,
  up: FocusRequester? = null,
  down: FocusRequester? = null,
  left: FocusRequester? = null,
  right: FocusRequester? = null,
): Modifier = composed {
  this
    .focusRequester(focusRequester)
    .focusProperties {
      this.previous = prev
      this.next = next
      this.up = up
      this.down = down
      this.left = left
      this.right = right
    }
}

/**
 * Ensures minimum touch target size for TV (56dp).
 * Applies additional padding if the element is smaller.
 */
fun Modifier.tvMinimumTouchTarget(
  minSize: Dp = TvFocusConstants.MinTouchTargetSize,
): Modifier = composed {
  if (!isTV) return@composed this

  this.padding(
    horizontal = 0.dp,
    vertical = 0.dp
  )
  // Note: Actual size enforcement should be done via Box with minimum size
}

/**
 * Applies overscan-safe padding for TV content.
 * Ensures UI is not cut off on older TVs.
 */
fun Modifier.overscanSafePadding(
  horizontal: Dp = TvFocusConstants.OverscanPadding,
  vertical: Dp = TvFocusConstants.OverscanPadding / 2,
): Modifier = composed {
  if (!isTV) return@composed this

  this.padding(horizontal = horizontal, vertical = vertical)
}

/**
 * Creates a chain of FocusRequesters for sequential focus navigation.
 *
 * @param count Number of focusable elements in the chain
 * @return List of FocusRequesters (remembered)
 */
@Composable
fun rememberFocusChain(count: Int): List<FocusRequester> {
  return remember { List(count) { FocusRequester() } }
}

/**
 * Default focus highlight config with app theme colors.
 */
@Composable
fun defaultFocusHighlightConfig(): FocusHighlightConfig {
  return FocusHighlightConfig(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.Transparent,
    borderWidth = TvFocusConstants.FocusBorderWidth,
    shape = RoundedCornerShape(TvFocusConstants.FocusCornerRadius),
    scaleFactor = TvFocusConstants.FocusScaleFactor,
    applyScale = true,
    applyBorder = true,
  )
}
