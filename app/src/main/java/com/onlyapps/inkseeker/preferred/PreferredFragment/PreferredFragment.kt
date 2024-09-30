package com.onlyapps.inkseeker.preferred.PreferredFragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reaccolte_view.PreferredFolderAdapter
import com.example.reaccolte_view.PreferredFolderData
import com.example.reaccolte_view.PreferredImgData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Auth.AuthViewModel
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.profile.ProfileViewModel
import com.onlyapps.inkseeker.requests
import org.json.JSONArray

class PreferredFragment : Fragment() {

    private lateinit var rvFolder : RecyclerView
    private lateinit var itemAdapter : PreferredFolderAdapter

    //dati delle raccolte degli studi salvati
    private lateinit var folderListStu : ArrayList<PreferredFolderData>
    private lateinit var imgListStu : ArrayList<PreferredImgData>
    lateinit var title_listStu : ArrayList<String>

    private lateinit var currentType: TextView

    //dati delle raccolte dei tattuatori salvati
    private lateinit var folderListTat : ArrayList<PreferredFolderData>
    private lateinit var imgListTat : ArrayList<PreferredImgData>
    lateinit var title_listTat : ArrayList<String>

    private lateinit var btnStudi : CardView
    private lateinit var btnTattuatori : CardView
    private lateinit var txtTat : TextView
    private lateinit var txtStu : TextView

    private lateinit var btnAddFolder : ImageButton
    //definire funzione per il btn Previous Page
    private lateinit var btnPrev : ImageButton
    var bool : Boolean = true//true se studio, false se tattuatore

    private var emailUser: String = ""
    private var idUser: Int = 0

    private lateinit var albumsTatooers: JSONArray
    private lateinit var albumsStudios: JSONArray

    companion object {
        fun newInstance() = PreferredFragment()
    }

    private lateinit var preferredViewModel: PreferredViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(R.layout.fragment_preferred, container, false)
    }

    fun initViewModel(){
        preferredViewModel = ViewModelProvider(this).get(PreferredViewModel::class.java)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel

        preferredViewModel.folderList.observe(viewLifecycleOwner, Observer { newDataList ->
            onRefreshAdapter(newDataList)
        })

        // Observe changes to the type (studio or tattoo)
        preferredViewModel.type.observe(viewLifecycleOwner, Observer { type ->
            if (type == "s") {
                handleClickStudio()
            } else if(type == "t") {
                handleClickTattoo()
            }
        })

        preferredViewModel.titleList.observe(viewLifecycleOwner, Observer { newTitles->
            preferredViewModel.updateFolderList()
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBtn(view)

        initializeTxt(view)

        currentType.text = "s"

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

        if(emailUser != "") {
            preferredViewModel.getUserID(emailUser)

            preferredViewModel.updateFolderData('s')

            // 0 - idUser, 1 - idAlbum, 2 - name, 3 - type, 4 - isDefault

            preferredViewModel.updateTitleList()

            initializeBtn(view)

            initializeTxt(view)

            //inizializzazione RecyclerView
            rvFolder = view.findViewById(R.id.rv_folders)
            rvFolder.setHasFixedSize(true)
            rvFolder.layoutManager = LinearLayoutManager(view.context)

            //inizializzazione Array dei dati dei studio salvati
            folderListStu = arrayListOf<PreferredFolderData>()
            imgListStu = arrayListOf<PreferredImgData>()

            //inizializzazione Array dei dati dei tattuatori salvati
            folderListTat = arrayListOf<PreferredFolderData>()
            imgListTat = arrayListOf<PreferredImgData>()

            preferredViewModel.updateFolderList()

            //onclicklistener dei vari bottoni
            btnStudi.setOnClickListener { preferredViewModel.toggleType("s") }
            btnTattuatori.setOnClickListener { preferredViewModel.toggleType("t") }
            btnAddFolder.setOnClickListener { handleClickAdd(view) }

        }else{
            val loginFragment = LoginFragment()
            setCurrentFragment(loginFragment, false)
            return
        }

    }

    private fun initializeBtn(view: View){

        btnAddFolder = view.findViewById(R.id.btn_add_folder)
        btnPrev = view.findViewById(R.id.btn_back)
        btnStudi = view.findViewById(R.id.preferred_change_folder_type_studio_btn)
        btnTattuatori = view.findViewById(R.id.preferred_change_folder_type_tatooers_btn)

    }

    private fun initializeTxt(view: View){
        txtStu = view.findViewById(R.id.studio_txt)
        txtTat = view.findViewById(R.id.tatooers_txt)
        currentType = view.findViewById(R.id.currentType)
    }

    private fun onRefreshAdapter(updatedList : ArrayList<PreferredFolderData>){
        rvFolder = requireView().findViewById(R.id.rv_folders)
        rvFolder.setHasFixedSize(true)
        rvFolder.layoutManager = LinearLayoutManager(requireView().context)
        val type: Char
        itemAdapter = PreferredFolderAdapter(updatedList, preferredViewModel, parentFragmentManager)
        rvFolder.adapter = itemAdapter
    }

    private fun handleClickStudio(){
        if(emailUser != "") {
            preferredViewModel.updateFolderData('s')
            preferredViewModel.updateFolderList()
            //viewModel.updateFolderList(folderListStu)
            btnTattuatori.setBackgroundResource(R.color.disabled)
            btnStudi.setBackgroundResource(R.color.primary)

            currentType.text = "s"

            txtTat.setTextColor(resources.getColor(android.R.color.black)) // Use resources.getColor() to get the actual color value
            txtStu.setTextColor(resources.getColor(android.R.color.white))
        }else{
            val loginFragment = LoginFragment()
            setCurrentFragment(loginFragment, false)
        }

    }

    private fun handleClickTattoo(){
        if(emailUser != "") {
            preferredViewModel.updateFolderData('t')
            preferredViewModel.updateFolderList()
            //viewModel.updateFolderList(folderListTat)
            btnStudi.setBackgroundResource(R.color.disabled)
            btnTattuatori.setBackgroundResource(R.color.primary)

            currentType.text = "t"

            txtStu.setTextColor(resources.getColor(android.R.color.black))
            txtTat.setTextColor(resources.getColor(android.R.color.white))
        }else{
            val loginFragment = LoginFragment()
            setCurrentFragment(loginFragment, false)
        }


    }

    private fun handleClickAdd(view: View){

        val inflater = LayoutInflater.from(view.context)
        val addFolder = inflater.inflate(R.layout.popup_addfolderview, null)
        val folderName = addFolder.findViewById<EditText>(R.id.addFolderName)
        val addDialog = AlertDialog.Builder(view.context)
        .setView(addFolder)
        .setPositiveButton("Add"){dialog,_->
            //addNewAlbum(idUser, folderName.text.toString(), currentType.text.toString())
            preferredViewModel.addFolder(folderName.text.toString())
           // setCurrentFragment(PreferredFragment(), false)
            dialog.dismiss()
        }
        .setNegativeButton("Cancel"){dialog,_->
            Toast.makeText(view.context, "no folder added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        .create()
        folderName.setOnKeyListener{_, keyCode, _ ->
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                val folderNames = folderName.text.toString()
                preferredViewModel.addFolder(folderNames)
                addDialog.dismiss()
                true
            }else{
                false
            }
        }
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