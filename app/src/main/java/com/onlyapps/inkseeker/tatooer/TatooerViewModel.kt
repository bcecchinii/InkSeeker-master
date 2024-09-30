package com.onlyapps.inkseeker.tatooer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.launch
import org.json.JSONArray

class TatooerViewModel : ViewModel() {

    private val _repo: Repository = Repository()

    private val _tatooerData: MutableLiveData<JSONArray> = MutableLiveData()
    val tatooerData: LiveData<JSONArray> get() = _tatooerData

    private val _tatooerPic: MutableLiveData<String> = MutableLiveData()
    val tatooerPic: LiveData<String> get() = _tatooerPic

    private val _tatooerName: MutableLiveData<String> = MutableLiveData()
    val tatooerName: LiveData<String> get() = _tatooerName

    private val _tatooerTag: MutableLiveData<String> = MutableLiveData()
    val tatooerTag: LiveData<String> get() = _tatooerTag

    private val _tatooerStyles: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val tatooerStyles: LiveData<ArrayList<String>> get() =_tatooerStyles

    private val _isSaved: MutableLiveData<Boolean> = MutableLiveData()
    val isSaved: LiveData<Boolean> = _isSaved

    private val _studios: MutableLiveData<JSONArray> = MutableLiveData()
    val studios: LiveData<JSONArray> get() = _studios

    fun getTatooData(id: Int){
        viewModelScope.launch {
            _tatooerData.value = _repo.getTatooerDataByID(id)
        }
    }

    fun getStudioData(id: Int) {
        viewModelScope.launch {
            _studios.value = _repo.getStudiosDataByTatID(id)
        }
    }

    fun updatePic() {
        val pic: String = _tatooerData.value?.getString(2)?: ""
        _tatooerPic.value = pic
    }

    fun updateName() {
        val name: String = _tatooerData.value?.getString(1)?: ""
        _tatooerName.value = name
    }

    fun updateTag() {
        val tag: String = _tatooerData.value?.getString(3)?: ""
        _tatooerTag.value = "@" + tag
    }

    fun updateStyles() {
        val stylesData: JSONArray = JSONArray(_tatooerData.value?.getString(5))
        val styles: ArrayList<String> = arrayListOf()
        for(i in 0 until stylesData.length()){
            styles.add(stylesData.getString(i))
        }
        _tatooerStyles.value = styles
    }

    fun isItemSavedByUser(idUser: Int, idItem: Int, type: String) {
        viewModelScope.launch {
            _isSaved.value = _repo.isItemSavedByUser(idUser, idItem, type)
        }
    }

}