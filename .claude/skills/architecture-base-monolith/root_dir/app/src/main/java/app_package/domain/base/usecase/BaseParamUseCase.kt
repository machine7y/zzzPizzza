<package>

abstract class BaseParamUseCase<in Param, out Result> {

    suspend operator fun invoke(param: Param): Result = execute(param)

    protected abstract suspend fun execute(param: Param): Result
}
