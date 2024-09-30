package com.onlyapps.inkseeker.profile.translate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R

class TranslateFragment : Fragment() {

    companion object {
        fun newInstance() = TranslateFragment()
    }

    private lateinit var viewModel: TranslateViewModel

    private lateinit var changelangBackBtn: ImageButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<TranslateDataClass>
    lateinit var lang_list: Array<String>
    lateinit var lang_list_abr: Array<String>
    lateinit var icon_list: Array<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_translate, container, false)
    }


    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lang_list_abr = arrayOf(
            "it",
            "en"
        )

        lang_list = arrayOf(
            "Italiano",
            "English"
        )

        icon_list = arrayOf(
            R.drawable.lang_it,
            R.drawable.lang_eng
        )


        recyclerView = view.findViewById(R.id.avail_lang)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        dataList = arrayListOf<TranslateDataClass>()

        getData()

        changelangBackBtn = view.findViewById(R.id.changelangBackBtn)
        changelangBackBtn.setOnClickListener{
            if(parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStackImmediate()
            }else{
                Toast.makeText(this.context, "no backstack", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getData(){
        for(i in lang_list.indices){
            val dataClass = TranslateDataClass(lang_list[i],icon_list[i],lang_list_abr[i])
            dataList.add(dataClass)
        }
        val itemAdapter = TranslateAdapterClass(dataList)
        recyclerView.adapter = itemAdapter

    }

}