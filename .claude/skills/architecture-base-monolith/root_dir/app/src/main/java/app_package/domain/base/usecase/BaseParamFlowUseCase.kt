<package>

import kotlinx.coroutines.flow.Flow

abstract class BaseParamFlowUseCase<in Param, out Result> {

    operator fun invoke(param: Param): Flow<Result> = execute(param)

    protected abstract fun execute(param: Param): Flow<Result>
}
