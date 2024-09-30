package com.onlyapps.inkseeker.profile.contattaci.faq

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onlyapps.inkseeker.R
import kotlinx.coroutines.launch

class FaqViewModel : ViewModel() {

    private val _faqList: MutableLiveData<Array<Array<String>>> = MutableLiveData()
    val faqList: LiveData<Array<Array<String>>> get() = _faqList

    fun getFaqList(context: Context) {
            val faqList = arrayOf<Array<String>>(
                arrayOf(string(R.string.faq_1_q, context), string(R.string.faq_1_q, context)),
                arrayOf(string(R.string.faq_2_q, context), string(R.string.faq_2_q, context)),
                arrayOf(string(R.string.faq_3_q, context), string(R.string.faq_3_q, context)),
                arrayOf(string(R.string.faq_4_q, context), string(R.string.faq_4_q, context)),
            )
            _faqList.value = faqList
    }

    private fun string(id: Int, context: Context): String {
        return context.resources.getString(id)
    }
}