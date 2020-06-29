package com.mehwhatever.executor.service.commons

import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import java.util.function.Supplier
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ContextPassingExecutorServiceTest {

    lateinit var executorServiceUnderTest: ContextPassingExecutorService<String>

    @BeforeEach
    fun setup() {
        executorServiceUnderTest =
            ContextPassingExecutorService(
                Executors.newSingleThreadExecutor(),
                Supplier { ThreadLocalContext.getContext() },
                Consumer { c -> ThreadLocalContext.setContext(c) },
                Consumer { ThreadLocalContext.clearContext() }
            )
    }

    @AfterEach
    fun cleanUp() {
        ThreadLocalContext.clearContext()
        executorServiceUnderTest.shutdownNow()
    }

    @Test
    fun `Test if context is being correctly passed to executorService on submit(Callable)`() {
        val dummyContext = UUID.randomUUID().toString()
        ThreadLocalContext.setContext(dummyContext)
        val future = executorServiceUnderTest.submit(Callable<String> {
            ThreadLocalContext.getContext()
        })
        await().until { future.isDone }
        Assertions.assertEquals(dummyContext, future.get())
    }

    @Test
    fun `Test if context is being correctly passed to executorService on submit(Runnable)`() {
        val dummyContext = UUID.randomUUID().toString()
        val completed = AtomicBoolean(false)
        ThreadLocalContext.setContext(dummyContext)
        val future = executorServiceUnderTest.submit {
            Assertions.assertEquals(dummyContext, ThreadLocalContext.getContext())
            completed.set(true)
        }
        await().until { future.isDone }
        Assertions.assertTrue(completed.get())
    }
}
