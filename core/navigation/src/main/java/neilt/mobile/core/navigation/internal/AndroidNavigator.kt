package neilt.mobile.core.navigation.internal

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.receiveAsFlow
import neilt.mobile.core.navigation.Destination
import neilt.mobile.core.navigation.NavOptions
import neilt.mobile.core.navigation.NavigationAction
import neilt.mobile.core.navigation.Navigator

/**
 * Internal implementation of [Navigator] for Android platform.
 *
 * This class handles navigation actions and provides a flow of [NavigationAction] events.
 *
 * @property startDestination The starting destination for the navigation.
 */
internal class AndroidNavigator(override val startDestination: Destination) : Navigator {

    private val _navigationActions = Channel<NavigationAction>(Channel.RENDEZVOUS)
    override val navigationActions = _navigationActions.receiveAsFlow()

    private var lastAction: NavigationAction? = null

    /**
     * Handles navigation actions, ensuring that duplicate actions are not processed.
     *
     * @param action The navigation action to handle.
     * @param block The block to execute with the given action.
     */
    private inline fun handleAction(action: NavigationAction, block: (NavigationAction) -> Unit) {
        if (action != lastAction) {
            lastAction = action.also(block)
        }
    }

    /**
     * Navigates to the specified destination with the given [NavOptions].
     *
     * @param destination The target [Destination].
     * @param navOptions Additional navigation options.
     */
    override suspend fun navigateTo(destination: Destination, navOptions: NavOptions) {
        handleAction(NavigationAction.NavigateTo(destination, navOptions)) {
            _navigationActions.trySend(it).onFailure { error ->
                Log.e("AndroidNavigator", "Failed to enqueue navigation action: $error")
            }
        }
    }

    /**
     * Navigates up in the navigation stack.
     */
    override suspend fun navigateUp() {
        handleAction(NavigationAction.NavigateUp) {
            _navigationActions.trySend(it).onFailure { error ->
                Log.e("AndroidNavigator", "Failed to enqueue navigate up action: $error")
            }
        }
    }
}
