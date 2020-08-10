package marc.nguyen.cleanarchitecture.core.mappers

interface DomainMappable<out R> {
    fun asEntity(): R
}