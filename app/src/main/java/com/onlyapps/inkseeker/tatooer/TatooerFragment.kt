package com.onlyapps.inkseeker.tatooer

import android.graphics.BitmapFactory
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.home.SaveAdapterClass
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.requests
import org.json.JSONArray
import java.io.InputStream
import java.net.URL

class TatooerFragment(private val id: Int) : Fragment() {

    companion object {
        fun newInstance() = TatooerFragment()

        private fun TatooerFragment() {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        iniViewmodel()
        return inflater.inflate(R.layout.fragment_tatooer, container, false)
    }

    private lateinit var tatooViewModel: TatooerViewModel
    private lateinit var preferredViewModel: PreferredViewModel

    private lateinit var tatooerPicture: ImageView
    private lateinit var tatooerName: TextView
    private lateinit var tatooerTagIG: TextView

    private lateinit var saveTatoo: ImageButton
    private lateinit var backbutton: ImageButton

    private lateinit var studiosRecycler: RecyclerView
    private lateinit var studiosDataList: ArrayList<StudioDataClass>

    private lateinit var stylesRecycler: RecyclerView
    private lateinit var stylesDataList: ArrayList<StyleDataClass>

    private fun iniViewmodel(){
        tatooViewModel = ViewModelProvider(this).get(TatooerViewModel::class.java)
        preferredViewModel = ViewModelProvider(this).get(PreferredViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 0 - id, 1 - name, 2 - picLink, 3 - instagramTag, 4 - instagramLink, 5 - styles
        initUI(view)

        studiosDataList = arrayListOf()

        val studiosItemAdapter = StudioAdapterClass(studiosDataList, parentFragmentManager)
        studiosRecycler.adapter = studiosItemAdapter

        stylesDataList = arrayListOf()

        val stylesItemAdapter = StyleAdapterClass(stylesDataList, requireContext())
        stylesRecycler.adapter = stylesItemAdapter

        getData()

        saveTatoo.setOnClickListener { saveTatooer(view) }

        backbutton.setOnClickListener { view ->
            if(parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStackImmediate()
            }else{
                Toast.makeText(this.context, "no backstack", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initUI(view: View){

        tatooerPicture = view.findViewById(R.id.tatooer_picture)
        tatooerName = view.findViewById(R.id.tatooer_name)
        tatooerTagIG = view.findViewById(R.id.tatooer_instagram)

        studiosRecycler = view.findViewById(R.id.tatooer_studios_recycler)
        studiosRecycler.layoutManager = LinearLayoutManager(requireContext())
        studiosRecycler.setHasFixedSize(true)

        stylesRecycler = view.findViewById(R.id.tatooer_styles_recycler)
        stylesRecycler.layoutManager = LinearLayoutManager(requireContext())
        stylesRecycler.setHasFixedSize(true)

        saveTatoo = view.findViewById(R.id.tatooer_preferBtn)
        backbutton = view.findViewById(R.id.tatooer_backbutton)
    }

    private fun getData(){
        tatooViewModel.getTatooData(id)
        tatooViewModel.getStudioData(id)
        tatooViewModel.updatePic()
        tatooViewModel.updateName()
        tatooViewModel.updateTag()
        tatooViewModel.updateStyles()
    }

    private fun saveTatooer(view: View){
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
        preferredViewModel.updateFolderData('t')
        preferredViewModel.updateFolderList()
        itemAdapter = SaveAdapterClass(preferredViewModel.folderList.value!!,
            preferredViewModel,
            preferredViewModel.userId.value!!,
            id,
            't')
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tatooViewModel.tatooerData.observe(viewLifecycleOwner, Observer { datas ->
            tatooViewModel.updatePic()
            tatooViewModel.updateName()
            tatooViewModel.updateTag()
            tatooViewModel.updateStyles()
        })

        tatooViewModel.tatooerPic.observe(viewLifecycleOwner, Observer {picId ->
            tatooerPicture.setImageBitmap(
                BitmapFactory.decodeStream(
                    URL(
                        picId
                    ).content as InputStream
                )
            )
        })

        tatooViewModel.tatooerName.observe(viewLifecycleOwner, Observer { name ->
            tatooerName.text = name
        })

        tatooViewModel.tatooerStyles.observe(viewLifecycleOwner, Observer { styles ->
            for (i in styles.indices) {
                val style = styles[i]
                stylesDataList.add(StyleDataClass(style))
            }
        })

        tatooViewModel.studios.observe(viewLifecycleOwner, Observer { studios ->
            for (i in 0 until studios.length()) {
                val studio = JSONArray(studios.getString(i))
                studiosDataList.add(StudioDataClass(studio.getInt(0), studio.getString(1)))
            }
        })

        tatooViewModel.tatooerTag.observe(viewLifecycleOwner, Observer { tag ->
            tatooerTagIG.text = tag
        })

        tatooViewModel.isSaved.observe(viewLifecycleOwner, Observer { isSaved ->
            if (isSaved) {
                saveTatoo.setBackgroundResource(R.drawable.ic_filled_heart_black)
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }



}