package digital.lamp.mindlamp.model

/**
 * Data class representing an action with a name and an optional associated page.
 *
 * @param name The name of the action.
 * @param page The associated page for the action (can be null).
 */
data class ActionData(
    val name : String,
    val page : String?
)