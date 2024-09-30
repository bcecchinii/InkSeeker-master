package com.onlyapps.inkseeker.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.launch
import org.json.JSONArray

class ProfileViewModel : ViewModel() {

    private val _userEmail: MutableLiveData<String> = MutableLiveData()
    val userEmail: LiveData<String> get() = _userEmail

    private val _userData: MutableLiveData<JSONArray> = MutableLiveData()
    val userData: LiveData<JSONArray> get() = _userData

    private val _repo: Repository = Repository()

    fun updateUserEmailGoog(c: Context){
            val googleUser = GoogleSignIn.getLastSignedInAccount(c)
            _userEmail.value = googleUser?.email.orEmpty()
    }

    fun updateUserEmailFire(){
            val firebaseUser = Firebase.auth.currentUser
            _userEmail.value = firebaseUser?.email.orEmpty()
    }

    fun getUserData() {
        viewModelScope.launch {
            try {
                _userData.value = _repo.getUserData(_userEmail.value.toString())
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user data", e)
                _userData.value = JSONArray()
            }
        }
    }


}