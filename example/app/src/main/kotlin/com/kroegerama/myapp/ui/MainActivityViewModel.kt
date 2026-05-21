package com.kroegerama.myapp.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kroegerama.myapp.controller.ProgressController
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val progressController: ProgressController
) : ViewModel() {
    val loading = progressController.loading
}
