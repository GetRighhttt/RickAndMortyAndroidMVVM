package com.example.rickandmortymvvm.core.events

/*
Generic wrapper class from google usually used to outline classes that define the state of
a response.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    // We'll wrap our data in this 'Success'
    // class in case of success response from api
    open class Success<T>(data: T) : Resource<T>(data = data)

    // We'll pass error message wrapped in this 'Error'
    // class to the UI in case of failure response
    open class Error<T>(errorMessage: String) : Resource<T>(message = errorMessage)

    // We'll just pass object of this Loading
    // class, just before making an api call
    open class Loading<T>(data: T) : Resource<T>()
}
