package com.github.mehwhatever.executor.service.commons

internal object ThreadLocalContext {
    val context: ThreadLocal<String> = ThreadLocal.withInitial { "" }

    fun getContext(): String {
        return context.get()
    }

    fun setContext(value: String) {
        context.set(value)
    }

    fun clearContext() {
        context.remove()
    }
}
