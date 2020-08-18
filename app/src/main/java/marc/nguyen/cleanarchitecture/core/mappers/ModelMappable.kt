package marc.nguyen.cleanarchitecture.core.mappers

interface ModelMappable<out R> {
    fun asModel(): R
}
