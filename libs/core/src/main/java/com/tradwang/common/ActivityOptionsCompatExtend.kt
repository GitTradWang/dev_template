package com.tradwang.common

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View

fun Activity.makeSceneTransitionAnimation(vararg sharedElements: Pair<View, String>): Bundle? {
    val elements: MutableList<androidx.core.util.Pair<View, String>> = mutableListOf()
    sharedElements.forEach { elements.add(androidx.core.util.Pair(it.first, it.second)) }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *elements.toTypedArray()).toBundle()
}

fun Activity.makeSceneTransitionAnim(vararg sharedElements: Pair<View, String>): ActivityOptionsCompat? {
    val elements: MutableList<androidx.core.util.Pair<View, String>> = mutableListOf()
    sharedElements.forEach { elements.add(androidx.core.util.Pair(it.first, it.second)) }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *elements.toTypedArray())
}

fun <T : ViewModel> AppCompatActivity.getViewModel(clazz: Class<T>): T {
    return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)).get(clazz)
}

fun <T : ViewModel> androidx.fragment.app.Fragment.getViewModel(clazz: Class<T>): T {
    return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.activity!!.application)).get(clazz)
}