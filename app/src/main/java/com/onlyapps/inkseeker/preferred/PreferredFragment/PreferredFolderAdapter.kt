package com.example.reaccolte_view

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import java.text.FieldPosition

//l'Adapter del folder_view
class PreferredFolderAdapter (private  var dataList : ArrayList<PreferredFolderData>?,
    private val viewModel : PreferredViewModel,
    val parentFragmentManager: FragmentManager) :
    RecyclerView.Adapter<PreferredFolderAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_folderview, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return viewModel.getSize() ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = viewModel.getData(position)
        if (currentItem != null) {
            holder.rvTitle.text = currentItem.title
        }

        //questo serve per inizializzare e riempire di dati il RecyclerView delle immagini salvate
        holder.rvImgRv.setHasFixedSize(true)
        holder.rvImgRv.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = imgAdapter(currentItem?.imgList, parentFragmentManager)
        holder.rvImgRv.adapter = adapter

    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rvTitle : TextView = itemView.findViewById(R.id.folderName)
        val rvImgRv : RecyclerView = itemView.findViewById(R.id.rv_imgSaved)

    }

    fun updateData(newDataList : ArrayList<PreferredFolderData>){
        dataList = newDataList
        notifyDataSetChanged()
    }

   /* fun popUpMenu(v : View, pos : Int){
        val popUpMenus = PopupMenu(v.context,v)
        popUpMenus.inflate(R.menu.menu_morepreferred)
        popUpMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editName->{
                    editName(v, pos)
                    true
                }
                else-> true
            }
        }
        popUpMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popUpMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
    }

    fun editName(v : View, pos : Int){
        val currentItem = dataList!!.get(pos)
        val addFolder = LayoutInflater.from(v.context)
            .inflate(R.layout.popup_addfolderview,null)
        val folderName = addFolder.findViewById<EditText>(R.id.addFolderName)
        val addDialog = AlertDialog.Builder(v.context)
        .setView(addFolder)
        .setPositiveButton(R.string.ok){dialog,_->
            if (pos != null) {
                viewModel.editFolderName(currentItem.title, folderName.text.toString())
                Log.d("Prev name", currentItem.title)
                Log.d("NEw NAme", folderName.text.toString())
            }
            Toast.makeText(v.context, "edit text button clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel){dialog,_->

            Toast.makeText(v.context, "no changes made", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }
        .create()
        folderName.setOnKeyListener{_, keyCode, _ ->
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                viewModel.editFolderName(currentItem.title, folderName.text.toString())
                addDialog.dismiss()
                true
            }else{
                false
            }
        }
        addDialog.show()
    }*/


}