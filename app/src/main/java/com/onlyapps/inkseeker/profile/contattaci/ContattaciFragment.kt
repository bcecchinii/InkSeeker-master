package com.onlyapps.inkseeker.profile.contattaci

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.play.integrity.internal.c
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.profile.contattaci.contacts.ContactsFragment
import com.onlyapps.inkseeker.profile.contattaci.faq.FaqFragment

class ContattaciFragment : Fragment() {

    companion object {
        fun newInstance() = ContattaciFragment()
    }

    private lateinit var viewModel: ContattaciViewModel

    private lateinit var backBtn: ImageButton

    private lateinit var contattaciFaqCard: CardView
    private lateinit var contattaciContactsCard: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contattaci, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn = view.findViewById(R.id.contattaci_backbutton)
        backBtn.setOnClickListener{
            if(parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStackImmediate()
            } else {
                Toast.makeText(this.context, "no backstack", Toast.LENGTH_SHORT).show()
            }
        }

        contattaciFaqCard = view.findViewById(R.id.contattaci_faq_card)
        contattaciContactsCard = view.findViewById(R.id.contattaci_contacts_card)

        contattaciFaqCard.setOnClickListener { view ->
            val faqFragment = FaqFragment()
            setCurrentFragment(faqFragment, true)
        }

        contattaciContactsCard.setOnClickListener { view ->
            val contactsFragment = ContactsFragment()
            setCurrentFragment(contactsFragment, true)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}