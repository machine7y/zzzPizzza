<package>

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<
    State : BaseState,
    InternalState : BaseInternalState,
    Event : BaseEvent,
    Label : BaseLabel,
    >(
    initialState: State,
    initialInternalState: InternalState,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(initialState)
    val stateFlow = _stateFlow.asStateFlow()
    val state: State get() = _stateFlow.value

    private val _labelFlow = MutableSharedFlow<Label>()
    val labelFlow = _labelFlow.asSharedFlow()

    private val mutableInternalStateFlow = MutableStateFlow(initialInternalState)
    protected val internalStateFlow = mutableInternalStateFlow.asStateFlow()
    protected val internalState: InternalState get() = mutableInternalStateFlow.value

    protected fun updateUiState(action: State.() -> State) {
        _stateFlow.update { it.action() }
    }

    protected fun updateInternalState(action: InternalState.() -> InternalState) {
        mutableInternalStateFlow.update { it.action() }
    }

    protected suspend fun publishLabel(label: Label) {
        _labelFlow.emit(label)
    }

    protected fun launch(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(block = action)
    }

    open fun onEvent(event: Event) {
    }
}
