package com.android.todohelper.utils

import com.android.todohelper.BaseViewModel
import com.android.todohelper.retrofit.Repository
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val firstModule = module {

    // ViewModel
    viewModel<BaseViewModel>()
    single { Repository() }


}