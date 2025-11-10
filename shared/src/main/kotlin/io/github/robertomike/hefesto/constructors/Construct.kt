package io.github.robertomike.hefesto.constructors

abstract class Construct<T> : AbstractMutableList<T>() {

    protected val items: MutableList<T> = ArrayList()

    override fun add(element: T): Boolean = items.add(element)

    fun set(item: T) {
        items.clear()
        items.add(item)
    }

    fun addAll(vararg item: T) {
        items.addAll(item)
    }

    override fun get(index: Int): T = items[index]

    override val size: Int
        get() = items.size

    override fun add(index: Int, element: T) {
        items.add(index, element)
    }

    override fun removeAt(index: Int): T = items.removeAt(index)

    override fun set(index: Int, element: T): T = items.set(index, element)
}
