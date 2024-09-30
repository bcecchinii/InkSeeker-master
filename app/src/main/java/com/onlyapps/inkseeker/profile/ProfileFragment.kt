package com.onlyapps.inkseeker.profile

import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.get
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Auth.AuthViewModel
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.profile.contattaci.ContattaciFragment
import com.onlyapps.inkseeker.requests
import com.onlyapps.inkseeker.profile.translate.TranslateFragment
import org.json.JSONArray
import java.io.InputStream
import java.net.URL

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var profilePic: ImageView
    private lateinit var username: TextView

    private lateinit var infoPersonali: CardView
    private lateinit var sicurezza: CardView
    private lateinit var traduzione: CardView
    private lateinit var privacy: CardView
    private lateinit var contattaci: CardView

    private lateinit var logoutBtn: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    fun initViewModel(){
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userEmail: String = ""
        profilePic = requireView().findViewById(R.id.profilePic)
        username = requireView().findViewById(R.id.profileUsername)
        view.findViewById<CardView>(R.id.profPicUser).foreground = null

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        var gsc = GoogleSignIn.getClient(this.requireContext(), gso)

        initViewModel()
        authViewModel.updateFirebaseUser()
        authViewModel.googleSignIn(this.requireContext())

        logoutBtn = view.findViewById(R.id.logoutBtn)
        logoutBtn.setPaintFlags(logoutBtn.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        logoutBtn.setOnClickListener { view ->
            authViewModel.signOut(gsc)
            Toast.makeText(this.context, "Logged out successfully", Toast.LENGTH_SHORT)
            val loginFragment = LoginFragment()
            setCurrentFragment(loginFragment, false)
        }

        infoPersonali = view.findViewById(R.id.userViewInfoPersonaliCard)
        sicurezza = view.findViewById(R.id.userViewSicurezzaCard)
        traduzione = view.findViewById(R.id.userViewTraduzioneCard)
        privacy = view.findViewById(R.id.userViewPrivacyCard)
        contattaci = view.findViewById(R.id.userViewContactUsCard)

        traduzione.setOnClickListener { view ->
            val translateFragment = TranslateFragment()
            setCurrentFragment(translateFragment, true)
        }

        contattaci.setOnClickListener { view ->
            val contattaciFragment = ContattaciFragment()
            setCurrentFragment(contattaciFragment, true)
        }
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        authViewModel.firebaseUser?.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if(firebaseUser == null){
                authViewModel.googleSignIn?.observe(viewLifecycleOwner, Observer { gsa ->
                    if(gsa == null){
                        val loginFragment = LoginFragment()
                        setCurrentFragment(loginFragment, false)
                        return@Observer
                    }else{
                        profileViewModel.updateUserEmailGoog(this.requireContext())
                    }
                })
            }else{
                profileViewModel.updateUserEmailFire()
            }
        })

        profileViewModel.userEmail.observe(viewLifecycleOwner, Observer { email ->
            if(email.isNotEmpty()){
                profileViewModel.getUserData()

                profilePic.setImageBitmap(
                    BitmapFactory.decodeStream(
                        URL(
                            profileViewModel.userData.value?.get(3)?.toString()?: ""
                        ).content as InputStream
                    )
                )
                username.text = profileViewModel.userData.value?.get(2)?.toString()?: ""
            }
        })

    }

   /* private fun getUserData(email: String): JSONArray {
        try {
            var params = arrayOf(
                arrayOf("email", email)
            )
            return JSONArray(requests.POST("http://giobra.com:5000/getUser/", params))
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return getUserData(email)
        }
    }*/

}