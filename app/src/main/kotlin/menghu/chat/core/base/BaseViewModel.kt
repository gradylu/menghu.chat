package menghu.chat.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * 所有 ViewModel 的基类
 * - 提供统一的日志标签
 * - 暴露全局 UI 状态流
 * - 所有子类通过 viewModelScope 管理协程生命周期
 */
open class BaseViewModel : ViewModel() {

    // 内部可变状态（子类可修改）
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)

    // 对外暴露只读状态流
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * 更新全局 UI 状态（供子类调用）
     */
    protected fun updateUiState(state: UiState) {
        _uiState.value = state
        Timber.d("[BaseViewModel] UI 状态更新为: %s", state.javaClass.simpleName)
    }

    /**
     * ViewModel 销毁时统一清理日志
     */
    override fun onCleared() {
        super.onCleared()
        Timber.d("[BaseViewModel] %s 已清理", this.javaClass.simpleName)
    }
}
