package com.mnuel.dev.notes.domain

interface BaseUseCase<T> {

    suspend fun execute(): T

}