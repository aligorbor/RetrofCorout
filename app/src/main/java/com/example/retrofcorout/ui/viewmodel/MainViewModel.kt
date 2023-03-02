package com.example.retrofcorout.ui.viewmodel

import androidx.lifecycle.*
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.data.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel(),
    CoroutineScope by MainScope() {

    private var _newUser = MutableLiveData<ResponseState<User>>()
    val newUser: LiveData<ResponseState<User>>
        get() = _newUser

    private var _editUser = MutableLiveData<ResponseState<User>>()
    val editUser: LiveData<ResponseState<User>>
        get() = _editUser

    private var _listUsers = MutableLiveData<ResponseState<List<User>>>()
    val listUsers: LiveData<ResponseState<List<User>>>
        get() = _listUsers

    private var _delUser = MutableLiveData<ResponseState<User>>()
    val delUser: LiveData<ResponseState<User>>
        get() = _delUser

    val listUsersDao: LiveData<ResponseState<List<User>>> =
        mainRepository.getUsersDao.asLiveData().map {
            ResponseState.Success(it)
        }

//    fun getUsers() = liveData(Dispatchers.IO) {
//        emit(ResponseState.Loading)
//        try {
//            emit(ResponseState.Success(mainRepository.getUsers()))
//        } catch (exception: Exception) {
//            emit(ResponseState.Error(exception))
//        }
//    }

    fun getUsers() {
        _listUsers.value = ResponseState.Loading
        launch(Dispatchers.IO) {
            try {
                _listUsers.postValue(ResponseState.Success(mainRepository.getUsers()))
            } catch (exception: Exception) {
                _listUsers.postValue(ResponseState.Error(exception))
            }
        }
    }

    fun getUser(userId: String) = liveData(Dispatchers.IO) {
        emit(ResponseState.Loading)
        try {
            emit(ResponseState.Success(mainRepository.getUser(userId)))
        } catch (exception: Exception) {
            emit(ResponseState.Error(exception))
        }
    }

    fun deleteUser(userId: String) {
        _delUser.value = ResponseState.Loading
        launch(Dispatchers.IO) {
            try {
                _delUser.postValue(ResponseState.Success(mainRepository.delUser(userId)))
            } catch (exception: Exception) {
                _delUser.postValue(ResponseState.Error(exception))
            }
        }
    }

    fun postNewUser(user: User) {
        _newUser.value = ResponseState.Loading
        launch(Dispatchers.IO) {
            try {
                _newUser.postValue(ResponseState.Success(mainRepository.postUser(user)))
            } catch (exception: Exception) {
                _newUser.postValue(ResponseState.Error(exception))
            }
        }
    }

    fun putEditUser(user: User) {
        _editUser.value = ResponseState.Loading
        launch(Dispatchers.IO) {
            try {
                _editUser.postValue(ResponseState.Success(mainRepository.putUser(user)))
            } catch (exception: Exception) {
                _editUser.postValue(ResponseState.Error(exception))
            }
        }
    }

    fun insertUserDao(user: User) = viewModelScope.launch {
        mainRepository.insertUserDao(user)
    }

    fun getUserDao(userId: String): LiveData<ResponseState<User>> =
        mainRepository.getUserDao(userId).asLiveData().map {
            ResponseState.Success(it)
        }

    fun updateUserDao(user: User) = viewModelScope.launch {
        mainRepository.updateUserDao(user)
    }

    fun deleteUserDao(user: User) = viewModelScope.launch {
        mainRepository.deleteUserDao(user)
    }

    fun deleteAllUsersDao() = viewModelScope.launch {
        mainRepository.deleteAllUsersDao()
    }
}