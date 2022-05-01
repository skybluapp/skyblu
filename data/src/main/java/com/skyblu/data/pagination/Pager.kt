package com.skyblu.data.pagination

interface PaginatorInterface<Key, Item>{
    suspend fun loadNextItems()
    fun reset()
}

/**
 *
 */
class Pager<DocumentSnapshot, Item>(
    private val initialKey : DocumentSnapshot,
    private inline val onLoadUpdated : (Boolean) -> Unit,
    private inline val onRequest : suspend (nextKey : DocumentSnapshot) -> Result<Item>,
    private inline val getNextKey : suspend  (Item) -> DocumentSnapshot,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess : suspend (items: Item, newKey : DocumentSnapshot) -> Unit
) : PaginatorInterface<DocumentSnapshot, Item>{

    private var currentKey : DocumentSnapshot = initialKey
    private var isRequesting = false


    override suspend fun loadNextItems() {
        if(isRequesting){
            return
        }
        isRequesting = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isRequesting = false
        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(items)
        onSuccess(items, currentKey)
        onLoadUpdated(false)
    }
    override fun reset() {
        currentKey = initialKey
    }
}