package com.onlyapps.inkseeker.profile.contattaci.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R

class FaqAdapterClass (private var dataList: ArrayList<FaqDataClass>): RecyclerView.Adapter<FaqAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.itemlayout_faq, parent, false)

        return ViewHolderClass(itemView)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.num.text = currentItem.id+"."
        holder.question.text = currentItem.question
        holder.answer.text = currentItem.answer

        holder.itemView.setOnClickListener{
            if (holder.answer.visibility == View.GONE) {
                holder.answer.visibility == View.VISIBLE
            } else {
                holder.answer.visibility == View.GONE
            }
        }
    }

    inner class ViewHolderClass(itemView : View) : RecyclerView.ViewHolder(itemView){
        val num: TextView = itemView.findViewById(R.id.faq_item_num)
        val question: TextView = itemView.findViewById(R.id.faq_item_question)
        val answer: TextView = itemView.findViewById(R.id.faq_item_answer)
    }

}