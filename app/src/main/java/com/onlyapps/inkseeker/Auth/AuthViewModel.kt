package com.onlyapps.inkseeker.Auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.ContentValues.TAG
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.internal.GoogleServices
import com.google.android.gms.tasks.Task
import com.google.android.play.integrity.internal.c
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AuthViewModel : ViewModel(){

    private val _repository: Repository = Repository()

    private val _googleSignIn: MutableLiveData<GoogleSignInAccount>? = MutableLiveData()
    val googleSignIn: LiveData<GoogleSignInAccount>? = _googleSignIn

    private val _firebaseUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val firebaseUser: LiveData<FirebaseUser>? = _firebaseUser

    private val _isCreated: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCreated: LiveData<Boolean> = _isCreated

    fun updateFirebaseUser(){
        viewModelScope.launch {
            _firebaseUser.postValue(_repository.getCurrentUser())
        }
    }

    fun signOut(gsc: GoogleSignInClient) {
        FirebaseAuth.getInstance().signOut()
        gsc.signOut()
    }

    fun googleSignIn(c:Context){
        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(c)
        _googleSignIn?.value = account?: null
    }

    fun newUser(email: String, password: String, userName: String){
        viewModelScope.launch {
            val success = _repository.createNewUser(email, password, userName)
            _isCreated.value = success
        }
    }

    fun handleSignIn(t: Task<GoogleSignInAccount>):Boolean{
        val success: Boolean = _repository.handleSignInResult(t)
        return success
    }

}