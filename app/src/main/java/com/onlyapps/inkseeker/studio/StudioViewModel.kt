package com.onlyapps.inkseeker.studio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.launch
import org.json.JSONArray

class StudioViewModel : ViewModel() {

    private val _repo: Repository = Repository()

    private val _studioData: MutableLiveData<JSONArray> = MutableLiveData()
    val studioData: LiveData<JSONArray> get() = _studioData

    private val _studioName: MutableLiveData<String> = MutableLiveData()
    val studioName: LiveData<String> get() = _studioName

    private val _studioAddress: MutableLiveData<String> = MutableLiveData()
    val studioAddress: LiveData<String> = _studioAddress

    private val _studioPic: MutableLiveData<String> = MutableLiveData()
    val studioPic: LiveData<String> = _studioPic

    private val _studioStars: MutableLiveData<String> = MutableLiveData()
    val studioStars: LiveData<String> get() = _studioStars

    private val _studioPiercing: MutableLiveData<String> = MutableLiveData()
    val studioPiercing: LiveData<String> get() = _studioPiercing

    private val _studioStyles: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val studioStyles: LiveData<ArrayList<String>> get() = _studioStyles

    private val _tatooersData: MutableLiveData<JSONArray> = MutableLiveData()
    val tatooersData: LiveData<JSONArray> get() = _tatooersData



    fun getStudioData(id: Int){
        viewModelScope.launch {
            _studioData.value = _repo.getSudioDataByID(id)
        }
    }

    fun getTatooersData(id: Int){
        viewModelScope.launch {
            _tatooersData.value = _repo.getTatooersDataByStuID(id)
        }
    }

    // 0 - id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles
    fun updateStudioName(){
        val name: String = _studioData.value?.getString(1)?: ""
        _studioName.value = name
    }

    fun updateStudioAddress(){
        val address: String = _studioData.value?.getString(2)?: ""
        _studioAddress.value = address
    }

    fun updateStudioPic(){
        val pic: String = _studioData.value?.getString(3)?: ""
        _studioPic.value = pic
    }

    fun updateStudioStars(){
        val stars: String = _studioData.value?.getString(4)?: ""
        _studioStars.value = stars
    }

    fun updateStudioPiercings(){
        val piercings: String = _studioData.value?.getString(5)?: ""
        _studioPiercing.value = piercings
    }

    fun updatStudioStyles(){
        val stylesData: JSONArray = JSONArray(_studioData.value?.getString(6))
        val styles: ArrayList<String> = arrayListOf()
        for(i in 0 until  stylesData.length()){
            styles.add(stylesData.getString(i))
        }
        _studioStyles.value = styles
    }

}