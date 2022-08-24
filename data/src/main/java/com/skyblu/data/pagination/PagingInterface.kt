package com.skyblu.data.pagination

/**
 * An interface to manage paged content
 */
interface PagingInterface<Key, Item>{
    suspend fun loadNextItems()
    fun reset()
}