package com.skyblu.data.memory

import com.skyblu.models.jump.Jump
import javax.inject.Inject

class SavedSkydive @Inject constructor() : SavedSkydiveInterface {
    override var skydive : Jump? = null
}