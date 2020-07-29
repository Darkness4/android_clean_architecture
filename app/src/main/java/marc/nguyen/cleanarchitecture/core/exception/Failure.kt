package marc.nguyen.cleanarchitecture.core.exception

open class DataException(error: Throwable) : RuntimeException(error)

class CacheException(error: Throwable) : DataException(error)
open class NetworkException(error: Throwable) : DataException(error)

class NoNetworkException(error: Throwable) : NetworkException(error)
class ServerUnreachableException(error: Throwable) : NetworkException(error)
class HttpCallFailureException(error: Throwable) : NetworkException(error)


