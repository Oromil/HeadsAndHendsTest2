package com.oromil.hhtest.ui.splash

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.oromil.hhtest.data.DataManager
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val dataManager: DataManager) : ViewModel() {

    val isUserLogged = MutableLiveData<Boolean>()

    fun checkLoggedUser() {
        isUserLogged.value = !dataManager.getLoginedUserEmail().isEmpty() &&
                !dataManager.getLoggedUserName().isEmpty()
    }
}