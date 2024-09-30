package com.onlyapps.inkseeker.studio

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.tatooer.TatooerFragment


class StyleAdapterClass (private val dataList: ArrayList<StyleDataClass>, private val context: Context): RecyclerView.Adapter<StyleAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_ddl_item, parent, false)

        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.rvText.text = getStringResourceByName(currentItem.text)
    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rvText: TextView = itemView.findViewById(R.id.ddl_item_text)
    }

    private fun getStringResourceByName(aString: String): String? {
        val packageName: String = context.getPackageName()
        val resId: Int = context.getResources().getIdentifier(aString, "string", packageName)
        return context.getString(resId)
    }

}

class TatooerAdapterClass (private val dataList: ArrayList<TatooerDataClass>, private val parentFragmentManager: FragmentManager): RecyclerView.Adapter<TatooerAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_ddl_item, parent, false)

        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.rvText.text = currentItem.name

        holder.itemView.setOnClickListener{
            var tatooerFragment = TatooerFragment(currentItem.id)
            setCurrentFragment(tatooerFragment, true)
        }
    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rvText: TextView = itemView.findViewById(R.id.ddl_item_text)
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}