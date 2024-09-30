package com.onlyapps.inkseeker.preferred

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reaccolte_view.PreferredFolderAdapter
import com.example.reaccolte_view.PreferredFolderData
import com.example.reaccolte_view.PreferredImgData
import com.example.reaccolte_view.imgAdapter
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.InputStream
import java.net.URL

class PreferredViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _folderData: MutableLiveData<JSONArray> = MutableLiveData()
    val folderData: LiveData<JSONArray> get() =_folderData

    private val _folderList = MutableLiveData<ArrayList<PreferredFolderData>>()
    val folderList : LiveData<ArrayList<PreferredFolderData>> get() = _folderList

    private val _titleList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val titleList: LiveData<ArrayList<String>> get() = _titleList

    private val _imgList: MutableLiveData<ArrayList<PreferredImgData>> = MutableLiveData()
    val imgList: LiveData<ArrayList<PreferredImgData>> get() = _imgList

    private val _repo: Repository = Repository()

    private val _type = MutableLiveData<String>()
    val type : LiveData<String> get() = _type

    private val _userSavedStudios: MutableLiveData<JSONArray> = MutableLiveData()
    val userSavedStudios: LiveData<JSONArray> = _userSavedStudios

    private val _imgData : MutableLiveData<ArrayList<PreferredImgData>?> = MutableLiveData()
    val imgData : LiveData<ArrayList<PreferredImgData>?> = _imgData

    private val _userId: MutableLiveData<Int> = MutableLiveData()
    val userId: LiveData<Int> get() = _userId

    private val _itemId: MutableLiveData<Int> = MutableLiveData()
    val itemId: LiveData<Int> = _itemId

    private val _albumId: MutableLiveData<Int> = MutableLiveData()
    val albumId: LiveData<Int> get() =_albumId


    init {
        _folderList.value = arrayListOf()
        _type.value = "s"
        _imgData.value = arrayListOf()
        _userSavedStudios.value = JSONArray()
    }

    fun getItemsByUserID(idUser: Int, type: String) {
        viewModelScope.launch {
            _userSavedStudios.value = _repo.getItemsByUserID(idUser, type)
        }
    }

    fun updateFolderData(type: Char){
        viewModelScope.launch {
            _folderData.value = _repo.getAlbumsData(_userId.value!!, type)
        }
    }

    fun updateTitleList(){
        val list = _titleList.value
        for (i in 0 until _folderData.value!!.length()) {
            val studio = JSONArray(_folderData.value!!.getString(i))
            list?.add(studio.getString(2))
        }
    }

    fun updateFolderList(){
        viewModelScope.launch {
            val folderList: ArrayList<PreferredFolderData> = arrayListOf()
            val data = _folderData.value
            val id = _userId.value
            if(data != null && id != null) {
                if(_type.value == "s"){
                    for (i in 0 until _folderData.value!!.length()) {
                        val album = JSONArray(data!!.getString(i))
                        // 0 - id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles
                        val studiosData =
                            _repo.getItemsByAlbum(_userId.value!!, album.getInt(1), album.getString(3))
                        val imgList: ArrayList<PreferredImgData> = arrayListOf()
                        for (j in 0 until studiosData.length()) {
                            val studio = JSONArray(studiosData.getString(j))
                            imgList.add(
                                PreferredImgData(
                                    studio.getString(3),
                                    studio.getString(1),
                                    "s",
                                    studio.getInt(0)
                                )
                            )
                        }
                        folderList.add(PreferredFolderData(album.getString(2), imgList))
                    }
                } else {
                    for (i in 0 until _folderData.value!!.length()) {
                        val album = JSONArray(_folderData.value!!.getString(i))
                        // 0 - id, 1 - name, 2 - picLink, 3 - igTag, 4 - igLink, 5 - styles
                        val tatooersData = _repo.getItemsByAlbum(_userId.value!!, album.getInt(1), album.getString(3))
                        val imgListTat: ArrayList<PreferredImgData> = arrayListOf()
                        for (j in 0 until tatooersData.length()) {
                            val tatooer = JSONArray(tatooersData.getString(j))
                            imgListTat.add(PreferredImgData(tatooer.getString(2), tatooer.getString(1), "t", tatooer.getInt(0)))
                        }
                        folderList.add(PreferredFolderData(album.getString(2), imgListTat))
                    }
                }

            }
            _folderList.value = folderList
        }
    }

    fun toggleType(type: String){
        _type.value = type
    }

    fun addFolder(folderName : String){
        /*val currentList = _folderList.value ?: arrayListOf()

        val newFolder = PreferredFolderData(folderName, null)

        currentList.add(newFolder)

        _folderList.value = currentList*/
        viewModelScope.launch {
            _repo.addNewAlbum(_userId.value!!, folderName, _type.value!!)
            if (_type.value == "s"){
                updateFolderData('s')
            }else{
                updateFolderData('t')
            }
            updateFolderList()
        }
    }


    fun getData(pos: Int): PreferredFolderData{
        val currentList = _folderList.value ?: arrayListOf()
        val currentFolder = currentList.get(pos)

        return currentFolder
    }

    fun getSize(): Int{
        val currentList = _folderList.value ?: arrayListOf()
        return currentList.size
    }

    fun setUserID(id: Int){
        _userId.value = id
    }

    fun getUserID(email: String){
        viewModelScope.launch {
            val id = _repo.getUserId(email)
            _userId.value = id
        }
    }

    fun addImg(userId: Int, idAlbum: Int, idItem: Int){
        viewModelScope.launch {
            _repo.saveItem(userId, idAlbum, idItem)
        }
    }

    fun delImg(userId: Int, idAlbum: Int, idItem: Int){
        viewModelScope.launch {
            _repo.delImg(userId, idAlbum, idItem)
        }
    }

    fun getAlbumId(type: Char, name: String){
        viewModelScope.launch {
            var albumID = 0
            updateFolderData(type)
            val albums = _folderData.value
            for (i in 0 until albums!!.length()) {
                val album = JSONArray(albums!!.getString(i))
                if (name == album.getString(2)) {
                    albumID = album.getString(1).toInt()
                    break
                }
            }
            _albumId.value = albumID
        }
    }

  /*  fun editFolderName(prevName: String, newName: String){
        viewModelScope.launch {
            val type: Char
            if(_type.value == "s"){
                type = 's'
            }else{
                type = 't'
            }
           /* Log.d("type", type.toString())
            getAlbumId(type, prevName)
            Log.d("AlbumId", _albumId.value.toString())
            Log.d("folderData", _folderData.value.toString())*/
            val albumIdDeferred = async { getAlbumId(type, prevName) }
            albumIdDeferred.await() // Wait for the result
            _repo.editFolderName(_albumId.value ?: return@launch, newName , _type.value!!)

        }
    }*/

}