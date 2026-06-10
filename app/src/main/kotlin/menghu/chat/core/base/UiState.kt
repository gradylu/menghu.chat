package menghu.chat.core.base

/**
 * 通用 UI 状态密封接口
 * - Idle：初始空闲状态
 * - Loading：加载中
 * - Success：加载成功（可选泛型数据）
 * - Error：加载失败（含错误信息）
 */
sealed interface UiState {

    /** 初始空闲状态 */
    data object Idle : UiState

    /** 加载中状态 */
    data object Loading : UiState

    /** 加载成功状态（可携带任意数据） */
    data class Success<T>(val data: T? = null) : UiState

    /** 加载失败状态（含错误信息） */
    data class Error(val message: String) : UiState
}
