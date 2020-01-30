package com.android.todohelper.utils

import com.android.todohelper.activity.viewModel.BaseViewModel
import com.android.todohelper.activity.viewModel.RegisterActivityViewModel
import com.android.todohelper.retrofit.Repository
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val firstModule = module {

    // ViewModel
    viewModel<BaseViewModel>()
    viewModel<RegisterActivityViewModel>()

    single { Repository() }


}