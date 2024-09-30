package com.onlyapps.inkseeker

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.home.HomeDataClass
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

class Repository {

    suspend fun createNewUser(email: String, password: String, username: String): Boolean{

        try {
            // Step 1: Create a new user with Firebase Authentication
            val authResult = Firebase.auth.createUserWithEmailAndPassword(email, password).await()

            // Step 2: Update user details on your server
            val params = arrayOf(
                arrayOf("email", Firebase.auth.currentUser!!.email.toString()),
                arrayOf("username", username),
                arrayOf("picURL", "https://www.computerhope.com/jargon/g/guest-user.png")
            )

            val result = createUser(params)

            if (result == "OK") {
                Log.d("API", "User registered/updated successfully")
                return true
            } else {
                Log.d("API", "Error during user registration/update")
            }
            return true
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "createUserWithEmail:failure", e)
            return false
        }
    }

    private fun createUser(params: Array<Array<String>>): String {
        try {
            return requests.POST("http://giobra.com:5000/registerUser/", params)
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return createUser(params)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>): Boolean{
        var success: Boolean = false
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleFirstName = account?.givenName ?: ""
            val googleEmail = account?.email ?: ""
            val googleProfilePicURL = account?.photoUrl.toString()

            var params = arrayOf(
                arrayOf("email", googleEmail),
                arrayOf("username", googleFirstName),
                arrayOf("picURL", googleProfilePicURL)
            )
            var result = createUser(params)

            if (result == "OK") {
                Log.d("API", "User registered/updated successfully")
            } else {
                Log.d("API", "Error during user registration/update")
            }
            success = true

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
        return success
    }

    suspend fun getCurrentUser(): FirebaseUser?{
        return FirebaseAuth.getInstance().currentUser
    }

    suspend fun getUserData(email: String): JSONArray {
        try {
            val params = arrayOf(arrayOf("email", email))
            val value = JSONArray(requests.POST("http://giobra.com:5000/getUser/", params)).apply {
                Log.d("API", "$this")
            }
            return  value
        } catch (e: Exception) {
            // Handle the exception, log it, or return an appropriate default value
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getUserData(email)
        }
    }

    suspend fun getStudiodata(): ArrayList<HomeDataClass>{
        try {
            val dataList = arrayListOf<HomeDataClass>()
            var studios = JSONArray(requests.GET("http://giobra.com:5000/getStudios/"))
            for (i in 0 until studios.length()) {
                val studio = studios.getJSONArray(i) // 1 - name, 2 - address, 3 - pic, 4 - stars
                dataList.add(HomeDataClass(studio.getInt(0), studio.getString(3), studio.getString(1), studio.getString(2), studio.getString(4), studio.getDouble(7), studio.getDouble(8)))
            }
            Log.d("API","data update success")
            return dataList
        } catch (e: Exception) {
            Log.e("REQUEST ERROR", e.stackTraceToString())
            return arrayListOf()
        }
    }

    suspend fun getStudiosDataByTatID(id: Int): JSONArray {
        try {
            return JSONArray(requests.POST("http://giobra.com:5000/getStudiosByTatooerID/", arrayOf( arrayOf("id", id.toString()) )))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getStudiosDataByTatID(id)
        }
    }

    suspend fun getTatooerDataByID(id: Int): JSONArray {
        try {
            return JSONArray(requests.POST("http://giobra.com:5000/getTatooerByID/", arrayOf( arrayOf("id", id.toString()) )))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getTatooerDataByID(id)
        }
    }

    suspend fun getSudioDataByID(id: Int):JSONArray {
        var params = arrayOf( arrayOf("id", "$id") )
        try {
            // 0 - id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles
            return JSONArray(requests.POST("http://giobra.com:5000/getStudioByID/", params))
        }catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getSudioDataByID(id)
        }
    }

    suspend fun getTatooersDataByStuID(id: Int): JSONArray {
        try {
            return JSONArray(requests.POST("http://giobra.com:5000/getTatooersByStudioID/", arrayOf(arrayOf("id", id.toString()))))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getTatooersDataByStuID(id)
        }
    }

    suspend fun getUserId(email: String): Int {
        try {
            return JSONArray(requests.POST("http://giobra.com:5000/getUser/", arrayOf( arrayOf("email", email) ))).getInt(0)
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getUserId(email)
        }
    }

    suspend fun getAlbumsData(id: Int, type: Char): JSONArray {
        try {
            var params = arrayOf(
                arrayOf("id", "$id"), arrayOf("type", "$type")
            )
            return JSONArray(requests.POST("http://giobra.com:5000/getAlbumsByUserID/", params))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getAlbumsData(id, type)
        }
    }

    suspend fun getItemsByAlbum(idUser: Int, idAlbum: Int, type: String): JSONArray {
        try {
            var newType: String
            if (type == "s") {
                newType = "studios"
            } else {
                newType = "tatooers"
            }
            var params = arrayOf(
                arrayOf("idUser", "$idUser"), arrayOf("idAlbum", "$idAlbum"), arrayOf("type", newType)
            )
            return JSONArray(requests.POST("http://giobra.com:5000/getItemsByUserAlbumID/", params))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getItemsByAlbum(idUser, idAlbum, type)
        }
    }

    suspend fun addNewAlbum(id: Int, name: String, type: String) {
        try {
            var params = arrayOf(
                arrayOf("id", "$id"), arrayOf("name", "$name"), arrayOf("type", "$type")
            )
            if (requests.POST("http://giobra.com:5000/newAlbum/", params) == "OK") {
                return
            } else {
                addNewAlbum(id, name, type)
            }
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            addNewAlbum(id, name, type)
        }
    }

    suspend fun saveItem(idUser: Int, idAlbum: Int, idItem: Int) {
        try {
            var params = arrayOf(
                arrayOf("idUser", "$idUser"), arrayOf("idAlbum", "$idAlbum"), arrayOf("idItem", "$idItem")
            )
            if (requests.POST("http://giobra.com:5000/newItem/", params) == "OK") {
                return
            } else {
                saveItem(idUser, idAlbum, idItem)
            }
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            saveItem(idUser, idAlbum, idItem)
        }
    }

    suspend fun getItemsByUserID(idUser: Int, type: String): JSONArray {
        try {
            var params = arrayOf(
                arrayOf("idUser", "$idUser"), arrayOf("type", type)
            )
            return JSONArray(requests.POST("http://giobra.com:5000/getItemsByUserID/", params))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getItemsByUserID(idUser, type)
        }
    }

    suspend fun isItemSavedByUser(idUser: Int, idItem: Int, type: String): Boolean {
        try {
            var params = arrayOf(
                arrayOf("idUser", "$idUser"), arrayOf("idItem", "$idItem"), arrayOf("type", type)
            )
            if (JSONArray(requests.POST("http://giobra.com:5000/isItemSavedByUser/", params)).length() == 0) {
                return false
            } else {
                return true
            }
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return isItemSavedByUser(idUser, idItem, type)
        }
    }

    suspend fun editFolderName(idAlbum: Int, newName: String, type: String){
        try {
            var params = arrayOf(
                arrayOf("id", "$idAlbum"), arrayOf("name", "$newName"), arrayOf("type", "$type")
            )
            if(requests.POST("http://giobra.com:5000/updateAlbumName/", params) == "OK"){
                return
            }else{
                editFolderName(idAlbum, newName, type)
            }
        } catch (e: Exception){
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            editFolderName(idAlbum, newName, type)
        }
    }

    suspend fun delImg(idUser: Int, idAlbum: Int, idItem: Int){
        try {
            var params = arrayOf(
                arrayOf("idUser", "$idUser"),
                arrayOf("idAlbum", "$idAlbum"),
                arrayOf("idItem", "$idItem")
            )
            if(requests.POST("http://giobra.com:5000/deleteItemFromAlbum/", params) == "OK"){
                return
            }else{
                delImg(idUser, idAlbum, idItem)
            }
        } catch (e: Exception){
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            delImg(idUser, idAlbum, idItem)
        }
    }

}