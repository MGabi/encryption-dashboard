package com.example

import pl.treksoft.kvision.core.Display
import pl.treksoft.kvision.core.OverflowWrap
import pl.treksoft.kvision.html.h5
import pl.treksoft.kvision.html.span
import pl.treksoft.kvision.modal.Modal

fun showApiKeyDetails(index: Int) {
    val key = Model.apiKeys[index]
    val modal = Modal("Details for key: ${key.name}", closeButton = true)
    modal.apply {
        h5("Encryption type: ") {
            display = Display.INLINE
        }
        span(key.type)
        h5("API Key:")
        span(key.key) {
            overflowWrap = OverflowWrap.BREAKWORK
        }
    }
    modal.show()
}