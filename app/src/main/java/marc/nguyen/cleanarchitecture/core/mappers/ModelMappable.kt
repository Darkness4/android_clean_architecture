package marc.nguyen.cleanarchitecture.core.mappers

interface ModelMappable<R> {
    fun asModel(): R
}