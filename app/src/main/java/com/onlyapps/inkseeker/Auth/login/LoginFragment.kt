package com.onlyapps.inkseeker.Auth.login


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.Auth.AuthViewModel
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.profile.ProfileFragment
import com.onlyapps.inkseeker.Auth.register.RegisterFragment
import com.onlyapps.inkseeker.requests


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button

    private lateinit var gotoRegister: TextView

    private lateinit var googleSigninBtn: SignInButton
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private fun initAuthViewModel() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAuthViewModel()

        email = view.findViewById(R.id.login_emailEdit)
        password = view.findViewById(R.id.login_passwordEdit)

        loginBtn = view.findViewById(R.id.login_loginBtn)
        loginBtn.setOnClickListener { view ->
            val email = email.text.toString()
            val password = password.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this.context, R.string.login_missing_email, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this.context, R.string.login_missing_pwd, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        authViewModel.updateFirebaseUser()
                        Toast.makeText(this.context, "Logged in", Toast.LENGTH_SHORT).show()
                        val profileFragment = ProfileFragment()
                        setCurrentFragment(profileFragment)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            view.context,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Toast.makeText(this.context, "Failed to log in", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        gotoRegister = view.findViewById(R.id.login_gotoRegister)
        gotoRegister.setOnClickListener { view ->
            val registerFragment = RegisterFragment()
            this.parentFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentView, registerFragment)
                commit()
            }
        }

        googleSigninBtn = view.findViewById(R.id.googleSigninBtn)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this.requireActivity(), gso)

        authViewModel.googleSignIn(this.requireContext())

        authViewModel.googleSignIn?.observe(viewLifecycleOwner, Observer { account ->
            if(account != null) {
                val profileFragment = ProfileFragment()
                setCurrentFragment(profileFragment)
            }
        })

        googleSigninBtn.setOnClickListener { view ->
            val signInIntent = gsc.signInIntent
            startActivityForResult(
                signInIntent, 9001
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9001) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            val success = authViewModel.handleSignIn(task)
            if(success){
                var profileFragment = ProfileFragment()
                setCurrentFragment(profileFragment)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        this.parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentView, fragment)
            commit()
        }

}