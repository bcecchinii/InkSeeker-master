package com.onlyapps.inkseeker.explore

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.onlyapps.inkseeker.R

class ExploreFragment : Fragment() {

    companion object {
        fun newInstance() = ExploreFragment()
    }

    private lateinit var viewModel: ExploreViewModel

    private lateinit var title: TextView

    private lateinit var searchBtn: ImageButton
    private lateinit var searchEdit: EditText

    private lateinit var cardsMap: Map<String, CardView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardsMap = mapOf(
            getString(R.string.fineline) to view.findViewById(R.id.exploreCol1Card1),
            getString(R.string.geometric) to view.findViewById(R.id.exploreCol2Card1),
            getString(R.string.realistic) to view.findViewById(R.id.exploreCol1Card2),
            getString(R.string.cartoon) to view.findViewById(R.id.exploreCol2Card2),
            getString(R.string.lettering) to view.findViewById(R.id.exploreCol1Card3),
            getString(R.string.anime) to view.findViewById(R.id.exploreCol2Card3),
            getString(R.string.sketch) to view.findViewById(R.id.exploreCol1Card4),
            getString(R.string.microrealistic) to view.findViewById(R.id.exploreCol2Card4),
            getString(R.string.blackwork) to view.findViewById(R.id.exploreCol1Card5),
            getString(R.string.ornamental) to view.findViewById(R.id.exploreCol2Card5),
            getString(R.string.concept_art) to view.findViewById(R.id.exploreCol1Card6),
            getString(R.string.colored) to view.findViewById(R.id.exploreCol2Card6),
            getString(R.string.illustrative) to view.findViewById(R.id.exploreCol1Card7),
            getString(R.string.fantasy) to view.findViewById(R.id.exploreCol2Card7),
            getString(R.string.watercolor) to view.findViewById(R.id.exploreCol1Card8),
            getString(R.string.maori) to view.findViewById(R.id.exploreCol2Card8),
            getString(R.string.patch) to view.findViewById(R.id.exploreCol1Card9),
            getString(R.string.traditional) to view.findViewById(R.id.exploreCol2Card9),
            getString(R.string.animals) to view.findViewById(R.id.exploreCol1Card10),
            getString(R.string.mosaic) to view.findViewById(R.id.exploreCol2Card10),
        )

        title = view.findViewById(R.id.explore_title)
        searchEdit = view.findViewById(R.id.exploreSearchEdit)

        searchBtn = view.findViewById(R.id.exploreSearchButton)
        searchBtn.setOnClickListener { view ->
            if (searchEdit.visibility == View.GONE) {
                searchEdit.visibility = View.VISIBLE
                title.setTextColor(Color.TRANSPARENT)
            } else {
                searchEdit.visibility = View.GONE
                title.setTextColor(Color.BLACK)
                search(searchEdit.text.toString())
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun search(keyword: String) {
        for (cardAssoc in cardsMap) {
            if (!cardAssoc.component1().lowercase().contains(keyword.lowercase())) {
                cardAssoc.component2().visibility = View.GONE
            } else {
                cardAssoc.component2().visibility = View.VISIBLE
            }
        }
    }

}