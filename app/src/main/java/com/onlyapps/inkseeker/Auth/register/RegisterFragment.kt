package com.onlyapps.inkseeker.Auth.register

import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.Auth.AuthViewModel
import com.onlyapps.inkseeker.Auth.login.LoginFragment

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var repPassword: EditText
    private lateinit var registerBtn: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gotoLogin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    private fun initAuthViewModel() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.register_emailEdit)
        username = view.findViewById(R.id.register_usernameEdit)
        password = view.findViewById(R.id.register_passwordEdit)
        repPassword = view.findViewById(R.id.register_repeatPasswordEdit)
        registerBtn = view.findViewById(R.id.register_signUpBtn)

        gotoLogin = view.findViewById(R.id.register_gotoLogin)
        gotoLogin.setOnClickListener { view ->
            var loginFragment = LoginFragment()
            setCurrentFragment(loginFragment, false)
        }

        initAuthViewModel()

        registerBtn.setOnClickListener { view ->
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()
            val repPassword = repPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this.context, R.string.login_missing_email, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                Toast.makeText(this.context, R.string.register_missing_username, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this.context, R.string.login_missing_pwd, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (repPassword.isEmpty()) {
                Toast.makeText(this.context, R.string.register_missing_rep_pwd, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.equals(repPassword)) {
                Toast.makeText(this.context, R.string.register_pwd_not_matching, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.newUser(email, password, username)

            authViewModel.isCreated.observe(viewLifecycleOwner, Observer { success ->
                if (success) {
                    val loginFragment = LoginFragment()
                    this.parentFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentView, loginFragment)
                        commit()
                    }
                }else{
                        Toast.makeText(
                            this.context,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        Toast.makeText(this.context, "Registration failed", Toast.LENGTH_SHORT)
                            .show()
                }
            })
        }
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = parentFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}