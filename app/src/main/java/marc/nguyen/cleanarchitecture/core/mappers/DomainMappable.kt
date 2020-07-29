package marc.nguyen.cleanarchitecture.core.mappers

interface DomainMappable<R> {
    fun asEntity(): R
}