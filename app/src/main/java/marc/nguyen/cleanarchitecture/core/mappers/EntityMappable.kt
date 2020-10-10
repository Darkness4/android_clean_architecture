package marc.nguyen.cleanarchitecture.core.mappers

interface EntityMappable<out R> {
    fun asEntity(): R
}
