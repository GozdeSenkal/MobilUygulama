package com.example.yeniproje

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val getAllUsers : LiveData<List<User>>
    private val userRepository : UserRepository

    init{
        val userDao = UserDatabase.getUserDatabase(application).userDao()
        userRepository = UserRepository(userDao)
        getAllUsers = userRepository.getAllUsers
    }

    fun insertUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(user)
        }
    }
    fun updateUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.updateUser(user)
        }
    }
    fun deleteUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteUser(user)
        }
    }
    fun deleteAllUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUsers()
        }
    }
}
