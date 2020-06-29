package com.mehwhatever.executor.service.commons

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Supplier

class ContextPassingExecutorService<T>(
    private val delegate: ExecutorService,
    private val contextSupplier: Supplier<T>,
    private val contextConsumer: Consumer<T>,
    private val contextCleaner: Consumer<T> = Consumer { }
) :
    ExecutorService by delegate {

    private fun wrapRunnable(runnable: Runnable): Runnable {
        val context = contextSupplier.get()
        return Runnable {
            contextConsumer.accept(context)
            try {
                runnable.run()
            } finally {
                contextCleaner.accept(context)
            }
        }
    }

    private fun <R> wrapCallable(callable: Callable<R>): Callable<R> {
        val context = contextSupplier.get()
        return Callable {
            contextConsumer.accept(context)
            try {
                callable.call()
            } finally {
                contextCleaner.accept(context)
            }
        }
    }

    private fun <R> wrapTasks(tasks: Collection<Callable<R>>): Collection<Callable<R>> {
        return tasks.map(this::wrapCallable)
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return delegate.submit(wrapRunnable(task), result)
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return delegate.submit(wrapCallable(task))
    }

    override fun submit(task: Runnable): Future<*> {
        return delegate.submit(wrapRunnable(task))
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return delegate.invokeAny(wrapTasks(tasks))
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return delegate.invokeAny(wrapTasks(tasks), timeout, unit)
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return delegate.invokeAll(tasks)
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        return delegate.invokeAll(wrapTasks(tasks), timeout, unit)
    }

    override fun execute(command: Runnable) {
        delegate.execute(wrapRunnable(command))
    }
}
