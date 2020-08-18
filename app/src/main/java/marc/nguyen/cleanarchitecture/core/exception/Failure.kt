package marc.nguyen.cleanarchitecture.core.exception

open class DataException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class CacheException(message: String? = null, cause: Throwable? = null) : DataException(message, cause)
open class NetworkException(message: String? = null, cause: Throwable? = null) : DataException(message, cause)

class NoNetworkException(message: String? = null, cause: Throwable? = null) : NetworkException(message, cause)
class ServerUnreachableException(message: String? = null, cause: Throwable? = null) : NetworkException(message, cause)
class HttpCallFailureException(message: String? = null, cause: Throwable? = null) : NetworkException(message, cause)
