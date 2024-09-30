package com.onlyapps.inkseeker.profile.translate

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R


class TranslateAdapterClass (private val dataList: ArrayList<TranslateDataClass>): RecyclerView.Adapter<TranslateAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_translate, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvlang.text = currentItem.dataLang
        holder.rvIcon.setImageResource(currentItem.dataImage)

        holder.itemView.setOnClickListener{
            val selectedLang = currentItem.dataLang_abr
            Log.d("AdapterClass", "Selected language code : $selectedLang")
            LocaleHelper.setLocale(holder.itemView.context, selectedLang)
            Log.d("AdapterClass", "Language changed. Current Lang $position")

            (holder.itemView.context as? Activity)?.recreate()
        }

    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rvlang: TextView = itemView.findViewById(R.id.language)
        val rvIcon: ImageView = itemView.findViewById(R.id.lang_icon_lbl)
    }

}