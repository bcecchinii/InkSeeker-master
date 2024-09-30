package com.onlyapps.inkseeker.profile.contattaci.faq

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.studio.StyleAdapterClass
import com.onlyapps.inkseeker.studio.StyleDataClass

class FaqFragment : Fragment() {

    companion object {
        fun newInstance() = FaqFragment()
    }

    private lateinit var viewModel: FaqViewModel

    private lateinit var faqRecycler: RecyclerView
    private lateinit var faqDataList: ArrayList<FaqDataClass>

    private lateinit var back: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this).get(FaqViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)

        faqDataList = arrayListOf()

        getDatas(view)

        val layoutManager = LinearLayoutManager(this.context)
        faqRecycler.layoutManager = layoutManager


        val faqItemAdapter = FaqAdapterClass(faqDataList)
        faqRecycler.adapter = faqItemAdapter



        back.setOnClickListener {
            if(parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStackImmediate()
            } else {
                Toast.makeText(this.context, "no backstack", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initUI(view: View){
        faqRecycler = view.findViewById(R.id.faq_recycler)
        back = view.findViewById(R.id.faq_backbutton)
    }

    private fun getDatas(v: View) {
        viewModel.getFaqList(v.context)
        for (i in viewModel.faqList.value!!.indices){
            val faq = viewModel.faqList.value!![i]
            faqDataList.add(FaqDataClass((i+1).toString(), faq[0], faq[1]))
        }
    }


}