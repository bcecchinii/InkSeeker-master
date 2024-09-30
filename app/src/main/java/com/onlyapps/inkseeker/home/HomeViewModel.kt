package com.onlyapps.inkseeker.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.launch
import org.json.JSONArray

class HomeViewModel : ViewModel() {

    private val _repo: Repository = Repository()

    private val _studioData: MutableLiveData<ArrayList<HomeDataClass>> = MutableLiveData()
    val studioData: LiveData<ArrayList<HomeDataClass>> get() = _studioData

    fun getStudiosData(){
        viewModelScope.launch {
          _studioData.value = _repo.getStudiodata()
        }
    }

}