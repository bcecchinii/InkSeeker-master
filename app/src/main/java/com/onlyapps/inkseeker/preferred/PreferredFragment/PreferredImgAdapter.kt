package com.example.reaccolte_view

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.studio.StudioFragment
import com.onlyapps.inkseeker.tatooer.TatooerFragment
import java.io.InputStream
import java.net.URL

//adapter di image_saved_view
class imgAdapter (private var dataList : ArrayList<PreferredImgData>?,
                  val parentFragmentManager: FragmentManager) :
    RecyclerView.Adapter<imgAdapter.ViewHolderClass>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_imgsavedview, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList?.get(position)
        if (currentItem != null) {
            holder.rvImg.background = null
            holder.rvImg.setImageBitmap(
                BitmapFactory.decodeStream(
                    URL(
                        currentItem.imgLink
                    ).content as InputStream
                )
            )

            holder.itemView.setOnClickListener { view ->
                if (currentItem.type == "s") {
                    var fragment = StudioFragment(currentItem.id)
                    setCurrentFragment(fragment, true)
                } else {
                    var fragment = TatooerFragment(currentItem.id)
                    setCurrentFragment(fragment, true)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    class ViewHolderClass (itemView : View) : RecyclerView.ViewHolder(itemView) {

        val rvImg : ImageView =itemView.findViewById(R.id.imgSaved)

    }

    fun updateData(newDataList : ArrayList<PreferredImgData>){
        dataList = newDataList
        notifyDataSetChanged()
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


}