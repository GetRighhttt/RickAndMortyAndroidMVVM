package com.example.rickandmortymvvm.core.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickandmortymvvm.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

inline infix fun Activity.setVisibilityOf(isLoading: () -> Boolean) =
    if (isLoading()) View.VISIBLE else View.GONE

fun Context.setToast(text: String, length: Int) = Toast.makeText(this, text, length).show()

val createSnackBar = { message: String, view: View ->
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

inline fun createSnackBarWithCoroutineAction(
    message: String,
    view: View,
    crossinline action: () -> Job,
    actionText: String
) = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    .setAction(actionText) { action() }
    .show()

fun Context.createPositiveDialog(title: String, message: String, buttonText: String): Any? =
    MaterialAlertDialogBuilder(this)
        .setTitle(title).setMessage(message)
        .setPositiveButton(buttonText) { dialog, _ -> dialog.dismiss() }.show()

fun Context.createNegativeDialog(title: String, message: String, buttonText: String): Any? =
    MaterialAlertDialogBuilder(this)
        .setTitle(title).setMessage(message)
        .setNegativeButton(buttonText) { dialog, _ -> dialog.dismiss() }.show()

suspend inline infix fun CoroutineScope.addDelay(timeUnit: () -> Long) = delay(timeUnit().milliseconds)

private data class InitialPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

fun View.applySystemBarsPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false,
    applyIme: Boolean = false
) {
    val initialPadding = InitialPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val bottomInset = maxOf(systemBars.bottom, if (applyIme) ime.bottom else 0)

        view.updatePadding(
            left = initialPadding.left + if (applyLeft) systemBars.left else 0,
            top = initialPadding.top + if (applyTop) systemBars.top else 0,
            right = initialPadding.right + if (applyRight) systemBars.right else 0,
            bottom = initialPadding.bottom + if (applyBottom) bottomInset else 0
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

fun ImageView.setImage(
    uri: String,
    imageView: ImageView
) = Glide.with(this)
    .load(uri)
    .placeholder(R.drawable.baseline_person_24)
    .circleCrop()
    .transition(DrawableTransitionOptions.withCrossFade())
    .into(imageView)
