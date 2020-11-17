package com.fantasmaplasma.beta

import android.util.Log
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo

object RouteRepository {

    fun saveClimbToAWS() {
        val item: Todo = Todo.builder()
            .name("cake in s")
            .priority(Priority.HIGH)
            .description("Build an Android application using Amplify")
            .build()
        Amplify.DataStore.save(
            item,
            { success -> Log.v(Constant.TAG, success.item().name.toString()) },
            { error -> error.message?.also {
                Log.v(Constant.TAG, it)
            }
            }
        )
    }

    fun query() {
        Amplify.DataStore.query(
            Todo::class.java,
            { todos ->
                while (todos.hasNext()) {
                    val todo = todos.next()
                    val name = todo.name;
                    val priority: Priority? = todo.priority
                    val description: String? = todo.description

                    Log.i("Tutorial", "==== Todo ====")
                    Log.i("Tutorial", "Name: $name")

                    if (priority != null) {
                        Log.i("Tutorial", "Priority: $priority")
                    }

                    if (description != null) {
                        Log.i("Tutorial", "Description: $description")
                    }
                }
            },
            { failure -> Log.e("Tutorial", "Could not query DataStore", failure) }
        )
    }
}