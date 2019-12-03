package meewii.core

/**
 * Enhanced enum to set the status of a View
 */
sealed class ViewStatus {
  object Idle : ViewStatus()
  data class Success(val message: String? = null) : ViewStatus()
  object Loading : ViewStatus()
  data class Error(val message: String? = null, val throwable: Throwable? = null) : ViewStatus()
  data class Warning(val message: String? = null) : ViewStatus()
}