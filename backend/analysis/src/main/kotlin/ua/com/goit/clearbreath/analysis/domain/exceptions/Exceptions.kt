package ua.com.goit.clearbreath.analysis.domain.exceptions

open class ModuleExceptions(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : ModuleExceptions(message)
class UserExistsException(message: String) : ModuleExceptions(message)
class AuthenticationException(message: String) : ModuleExceptions(message)
class ConvertionFileException(message: String) : ModuleExceptions(message)

