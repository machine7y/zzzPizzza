<package>

abstract class BaseNoParamUseCase<out Result> {

    suspend operator fun invoke(): Result = execute()

    protected abstract suspend fun execute(): Result
}
