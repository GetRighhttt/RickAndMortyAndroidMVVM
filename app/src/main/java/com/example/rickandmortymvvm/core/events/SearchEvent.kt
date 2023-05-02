package com.example.rickandmortymvvm.core.events

/*
Used for ViewModel searchState.
 */
sealed class SearchEvent<T>(
    val results: T? = null,
    val errorMessage: String? = null
) {

    // We'll wrap our data in this 'Success'
    // class in case of success response from api
    open class Success<T>(data: T) : SearchEvent<T>(results = data)

    // We'll pass error message wrapped in this 'Error'
    // class to the UI in case of failure response
    open class Failure<T>(errorMessage: String) : SearchEvent<T>(errorMessage = errorMessage)

    // We'll just pass object of this Loading
    // class, just before making an api call
    open class Loading<T> : SearchEvent<T>()

    // Can be used to signify and empty or idle state
    // sets the initial state for state flows
    open class Idle<T> : SearchEvent<T>()
}