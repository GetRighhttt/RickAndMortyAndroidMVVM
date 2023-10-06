package com.example.rickandmortymvvm.core.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

infix fun Activity.setVisibilityOf(isLoading: Boolean) = if (isLoading) View.VISIBLE else View.GONE
fun Context.setToast(text: String, length: Int) = Toast.makeText(this, text, length).show()

fun Activity.createSnackBar(message: String, view: View) = Snackbar.make(
    view, message, Snackbar.LENGTH_SHORT
).show()

fun Activity.createSnackBarWithCoroutineAction(
    message: String,
    view: View,
    action: Job,
    actionText: String
) = Snackbar.make(
    view, message, Snackbar.LENGTH_SHORT
)
    .setAction(actionText) {
        action
    }
    .show()

fun Context.createPositiveDialog(title: String, message: String, buttonText: String) =
    MaterialAlertDialogBuilder(this)
        .setTitle(title).setMessage(message)
        .setPositiveButton(buttonText) { dialog, _ -> dialog.dismiss() }.show()!!

fun Context.createNegativeDialog(title: String, message: String, buttonText: String) =
    MaterialAlertDialogBuilder(this)
        .setTitle(title).setMessage(message)
        .setNegativeButton(buttonText) { dialog, _ -> dialog.dismiss() }.show()!!

suspend infix fun CoroutineScope.addDelay(timeUnit: Long) = delay(timeUnit)

fun ImageView.setImage(
    uri: String,
    imageView: ImageView
) = Glide.with(this)
    .load(uri)
    .placeholder(com.example.rickandmortymvvm.R.drawable.baseline_person_24)
    .circleCrop()
    .transition(DrawableTransitionOptions.withCrossFade())
    .into(imageView)