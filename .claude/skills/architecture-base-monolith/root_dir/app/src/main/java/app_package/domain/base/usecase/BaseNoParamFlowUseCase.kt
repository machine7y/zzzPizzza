<package>

import kotlinx.coroutines.flow.Flow

abstract class BaseNoParamFlowUseCase<out Result> {

    operator fun invoke(): Flow<Result> = execute()

    protected abstract fun execute(): Flow<Result>
}
