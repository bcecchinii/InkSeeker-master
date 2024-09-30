package com.onlyapps.inkseeker.home

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reaccolte_view.PreferredFolderData
import com.example.reaccolte_view.PreferredImgData
import com.example.reaccolte_view.imgAdapter
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.studio.StudioFragment
import com.onlyapps.inkseeker.tatooer.TatooerFragment
import java.io.InputStream
import java.net.URL

class SaveAdapterClass(private  var dataList : ArrayList<PreferredFolderData>?,
                       val viewModel: PreferredViewModel,
                        val userID: Int,
                        val imgID: Int,
                        val type: Char

) :
    RecyclerView.Adapter<SaveAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlayout_folderview, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList?.get(position)
        if (currentItem != null) {
            holder.rvTitle.text = currentItem.title
        }
        viewModel.setUserID(userID)
        viewModel.updateFolderList()
        viewModel.updateFolderData(type)

        holder.rvImgRv.setHasFixedSize(true)
        holder.rvImgRv.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = saveImgAdapterClass(currentItem?.imgList )
        holder.rvImgRv.adapter = adapter
        var clickCount = 0
        holder.itemView.setOnClickListener{
           clickCount++
            if(clickCount % 2 != 0) {
                viewModel.getAlbumId(type, currentItem!!.title)
                viewModel.addImg(userID, viewModel.albumId.value!!, imgID)
                holder.rvCard.setBackgroundResource(R.drawable.select_backgound)
            }else{
                viewModel.getAlbumId(type, currentItem!!.title)
                viewModel.delImg(userID, viewModel.albumId.value!!, imgID)
                holder.rvCard.setBackgroundResource(R.drawable.unselect_card)
            }
            Log.d("CLICKED!", "element clicked " + clickCount + " times")
        }

    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rvTitle: TextView = itemView.findViewById(R.id.folderName)
        val rvImgRv: RecyclerView = itemView.findViewById(R.id.rv_imgSaved)
        var rvCard: CardView = itemView.findViewById(R.id.rv_folder_card)

    }

}
class saveImgAdapterClass(private var dataList : ArrayList<PreferredImgData>?)
    : RecyclerView.Adapter<saveImgAdapterClass.ViewHolderClass>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {

        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_imgsavedview, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList?.get(position)
       if (currentItem != null) {
            holder.rvImg.background = null
            /*holder.rvImg.setImageBitmap(
                BitmapFactory.decodeStream(
                    URL(
                        currentItem.imgLink
                    ).content as InputStream
                )
            )*/
            holder.rvImg.background = null

            Glide.with(holder.itemView.context)
                .load(currentItem.imgLink)
                .into(holder.rvImg)
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

}