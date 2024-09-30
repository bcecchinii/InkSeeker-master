package com.onlyapps.inkseeker.studio

import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Auth.AuthViewModel
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.home.HomeAdapterClass
import com.onlyapps.inkseeker.home.HomeDataClass
import com.onlyapps.inkseeker.home.SaveAdapterClass
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.requests
import org.json.JSONArray
import java.io.InputStream
import java.net.URL

class StudioFragment(private val id: Int) : Fragment() {

    companion object {
        fun newInstance() = StudioFragment()

        private fun StudioFragment() {

        }
    }

    private lateinit var studioViewModel: StudioViewModel
    private lateinit var preferredViewModel: PreferredViewModel

    private lateinit var studioPicture: ImageView
    private lateinit var studioName: TextView
    private lateinit var studioAddress: TextView

    private lateinit var studioBackButton: ImageButton

    private lateinit var stylesCard: CardView
    private lateinit var stylesChevron: ImageView
    private lateinit var stylesRecycler: RecyclerView
    private lateinit var stylesDataList: ArrayList<StyleDataClass>
    private lateinit var studioSave: ImageButton

    private lateinit var tatooersCard: CardView
    private lateinit var tatooersChevron: ImageView
    private lateinit var tatooersRecycler: RecyclerView
    private lateinit var tatooersDataList: ArrayList<TatooerDataClass>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(R.layout.fragment_studio, container, false)
    }

    private fun initViewModel(){
        studioViewModel = ViewModelProvider(this).get(StudioViewModel::class.java)
        preferredViewModel = ViewModelProvider(this).get(PreferredViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)

        stylesCard.setOnClickListener { view ->
            if (stylesRecycler.visibility == View.GONE) {
                stylesChevron.background = resources.getDrawable(R.drawable.ic_chevron_down)
                stylesRecycler.visibility = View.VISIBLE
            } else {
                stylesChevron.background = resources.getDrawable(R.drawable.ic_chevron_right)
                stylesRecycler.visibility = View.GONE
            }
        }

        studioBackButton.setOnClickListener { view ->
            if(parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStackImmediate()
            }else{
                Toast.makeText(this.context, "no backstack", Toast.LENGTH_SHORT).show()
            }
        }

        studioSave.setOnClickListener{saveStudio(view)}

        stylesDataList = arrayListOf()

        val stylesItemAdapter = StyleAdapterClass(stylesDataList, requireContext())
        stylesRecycler.adapter = stylesItemAdapter

        tatooersCard.setOnClickListener { view ->
            if (tatooersRecycler.visibility == View.GONE) {
                tatooersChevron.background = resources.getDrawable(R.drawable.ic_chevron_down)
                tatooersRecycler.visibility = View.VISIBLE
            } else {
                tatooersChevron.background = resources.getDrawable(R.drawable.ic_chevron_right)
                tatooersRecycler.visibility = View.GONE
            }
        }

        tatooersDataList = arrayListOf()

        val tatooersItemAdapter = TatooerAdapterClass(tatooersDataList, parentFragmentManager)
        tatooersRecycler.adapter = tatooersItemAdapter

        getDatas()

    }



    fun initUI(view: View){
        studioPicture = view.findViewById(R.id.studio_picture)

        studioName = view.findViewById(R.id.studio_name)

        studioAddress = view.findViewById(R.id.studio_address)
        studioBackButton = view.findViewById(R.id.studio_backbutton)

        stylesRecycler = view.findViewById(R.id.studio_styles_recycler)
        stylesRecycler.layoutManager = LinearLayoutManager(requireContext())
        stylesRecycler.setHasFixedSize(true)

        stylesCard = view.findViewById(R.id.studio_stylesCard)
        stylesChevron = view.findViewById(R.id.studio_styles_chevron)

        tatooersRecycler = view.findViewById(R.id.studio_tatooers_recycler)
        tatooersRecycler.layoutManager = LinearLayoutManager(requireContext())
        tatooersRecycler.setHasFixedSize(true)

        tatooersCard = view.findViewById(R.id.studio_tatooersCard)
        tatooersChevron = view.findViewById(R.id.studio_tatooers_chevron)

        studioSave = view.findViewById(R.id.studio_preferBtn)
    }

    private fun getDatas(){
        studioViewModel.getStudioData(id)
        studioViewModel.getTatooersData(id)
        studioViewModel.updateStudioName()
        studioViewModel.updateStudioAddress()
        studioViewModel.updateStudioPic()
        studioViewModel.updateStudioStars()
        studioViewModel.updateStudioPiercings()
        studioViewModel.updatStudioStyles()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        studioViewModel.studioData.observe(viewLifecycleOwner, Observer { studios ->
            studioViewModel.updateStudioName()
            studioViewModel.updateStudioAddress()
            studioViewModel.updateStudioPic()
            studioViewModel.updateStudioStars()
            studioViewModel.updateStudioPiercings()
            studioViewModel.updatStudioStyles()
        })

        studioViewModel.studioName.observe(viewLifecycleOwner, Observer { name ->
            studioName.text = name
        })

        studioViewModel.studioPic.observe(viewLifecycleOwner, Observer { pic ->
            studioPicture.setImageBitmap(
                BitmapFactory.decodeStream(
                    URL(
                        pic
                    ).content as InputStream
                )
            )
        })

        studioViewModel.studioAddress.observe(viewLifecycleOwner, Observer { address ->
            studioAddress.text = address
        })

        studioViewModel.tatooersData.observe(viewLifecycleOwner, Observer { data ->
            for (i in 0 until data.length()) {
                val tatooer = data.getJSONArray(i)
                tatooersDataList.add(TatooerDataClass(tatooer.getInt(0), tatooer.getString(1), tatooer.getString(2),  tatooer.getString(3), tatooer.getString(4), JSONArray(tatooer.getString(5))))
            }
        })

        studioViewModel.studioStyles.observe(viewLifecycleOwner, Observer { styles ->
            for (i in styles.indices) {
                val style = styles[i]
                stylesDataList.add(StyleDataClass(style))
            }
        })
    }

    private fun saveStudio(view: View){
        var emailUser: String = ""
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        var gsc = GoogleSignIn.getClient(this.requireContext(), gso)

        val firebaseUser = Firebase.auth.currentUser
        val googleUser: GoogleSignInAccount?
        if (firebaseUser == null) {
            googleUser = GoogleSignIn.getLastSignedInAccount(this.requireContext())
            if (googleUser == null) {
                val loginFragment = LoginFragment()
                setCurrentFragment(loginFragment, false)
                return
            } else {
                emailUser = googleUser.email.toString()
            }
        } else {
            emailUser = firebaseUser.email.toString()
        }
        preferredViewModel.getUserID(emailUser)
        val inflater = LayoutInflater.from(view.context)
        val saveFolder = inflater.inflate(R.layout.popup_savetofolder, null)
        val folderRv = saveFolder.findViewById<RecyclerView>(R.id.saveToFolderRv)
        val itemAdapter : SaveAdapterClass
        preferredViewModel.updateFolderData('s')
        preferredViewModel.updateFolderList()
        itemAdapter = SaveAdapterClass(preferredViewModel.folderList.value!!,
            preferredViewModel,
            preferredViewModel.userId.value!!,
            id,
            's')
        folderRv.adapter = itemAdapter
        folderRv.setHasFixedSize(true)
        folderRv.layoutManager = LinearLayoutManager(view.context)

        val addDialog = AlertDialog.Builder(view.context)
            .setView(saveFolder)
            .setPositiveButton(R.string.add){dialog,_->
                Toast.makeText(view.context, "studio added to folder", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel){dialog,_->
                Toast.makeText(view.context, "no folder added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()

        addDialog.show()
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }


}