package com.onlyapps.inkseeker.home

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reaccolte_view.PreferredFolderAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.play.integrity.internal.c
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.profile.translate.LocaleHelper
import com.onlyapps.inkseeker.home.HomeDataClass
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.requests
import com.onlyapps.inkseeker.studio.StudioFragment
import com.onlyapps.inkseeker.studio.StudioViewModel
import org.json.JSONArray
import java.io.InputStream
import java.net.URL

class HomeAdapterClass (private var dataList: ArrayList<HomeDataClass>,
                        private val parentFragmentManager: FragmentManager,
                        val idUser: Int,
                        val context: Context,
                        val viewModel: PreferredViewModel,
                        private val googleMap: GoogleMap): RecyclerView.Adapter<HomeAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_studio, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        viewModel.getItemsByUserID(idUser, "s")
        val userSavedStudios = viewModel.userSavedStudios.value!!
        var savedStudios = arrayListOf<String>()
        for (i in 0 until userSavedStudios.length()) {
            val idStudio = JSONArray(userSavedStudios.getString(i)).getString(0)
            savedStudios.add(idStudio)
        }

        holder.rvName.text = currentItem.dataName
        holder.rvAddress.text = currentItem.dataAddress
        holder.rvStars.text = currentItem.dataStars

        holder.rvImage.setImageBitmap(
            BitmapFactory.decodeStream(
            URL(
                currentItem.dataPicLink
            ).content as InputStream
        ))

        holder.itemView.setOnClickListener{
            var studioFragment = StudioFragment(currentItem.id)
            setCurrentFragment(studioFragment, true)
        }

        if (idUser == -1) {
            holder.heartButton.visibility == View.GONE
        }

        if (currentItem.id.toString() in savedStudios) {
            holder.heartButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_filled_heart))
        }

        holder.heartButton.setOnClickListener { view ->
            if(idUser != -1){
                addToFolder(view, position)
                Toast.makeText(context, "Item Saved!", Toast.LENGTH_SHORT)
            }else{
                setCurrentFragment(LoginFragment(), false)
            }

        }

        holder.mapButton.setOnClickListener { view ->
            val position = LatLng(currentItem.lat, currentItem.lng)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f))
        }
    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.homeCardStudioProfilePicture)
        val rvName: TextView = itemView.findViewById(R.id.homeCardStudioNome)
        val rvAddress: TextView = itemView.findViewById(R.id.homeCardStudioIndirizzo)
        val rvStars: TextView = itemView.findViewById(R.id.homeCardStudioStars)
        val heartButton: ImageButton = itemView.findViewById(R.id.home_studio_heart_button)
        val mapButton: ImageButton = itemView.findViewById(R.id.home_studio_location_button)
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun updateData(newData: ArrayList<HomeDataClass>){
        dataList = newData
        notifyDataSetChanged()
    }

    private lateinit var itemAdapter: SaveAdapterClass
    private fun addToFolder(v: View, pos: Int){
        val currentItem = dataList[pos]
        val inflater = LayoutInflater.from(v.context)
        val saveFolder = inflater.inflate(R.layout.popup_savetofolder, null)
        val folderRv = saveFolder.findViewById<RecyclerView>(R.id.saveToFolderRv)
        viewModel.setUserID(idUser)
        viewModel.updateFolderData('s')
        viewModel.updateFolderList()
        itemAdapter = SaveAdapterClass(viewModel.folderList.value!!, viewModel, idUser, currentItem.id, 's')
        folderRv.adapter = itemAdapter
        folderRv.setHasFixedSize(true)
        folderRv.layoutManager = LinearLayoutManager(v.context)

        val addDialog = AlertDialog.Builder(v.context)
            .setView(saveFolder)
            .setPositiveButton(R.string.add){dialog,_->
                Toast.makeText(v.context, "studio added to folder", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel){dialog,_->
                Toast.makeText(v.context, "no folder added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()

        addDialog.show()


    }

}

class StyleAdapterClass (private var dataList: ArrayList<StyleDataClass>, private val context: Context): RecyclerView.Adapter<StyleAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_filter_styles_ddl_item, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.styleName.text = getStringResourceByName(currentItem.id)
        holder.styleName.setTextColor(context.resources.getColor(R.color.primary))

        holder.itemView.setOnClickListener{
            if (currentItem.isSelected) {
                holder.styleName.setTextColor(context.resources.getColor(R.color.black))
                currentItem.isSelected = false
            } else {
                holder.styleName.setTextColor(context.resources.getColor(R.color.primary))
                currentItem.isSelected = true
            }
        }
    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val styleName: TextView = itemView.findViewById(R.id.home_filter_ddl_item_style_name)
    }

    private fun getStringResourceByName(aString: String): String? {
        val packageName: String = context.getPackageName()
        val resId: Int = context.getResources().getIdentifier(aString, "string", packageName)
        return context.getString(resId)
    }

}