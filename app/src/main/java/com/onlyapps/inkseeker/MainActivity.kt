package com.onlyapps.inkseeker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.onlyapps.inkseeker.explore.ExploreFragment

import com.onlyapps.inkseeker.home.HomeFragment
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.preferred.PreferredFragment.PreferredFragment
//import com.onlyapps.inkseeker.preferred.PreferredFragment
import com.onlyapps.inkseeker.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    private lateinit var selItemUnderline: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.navView)
        selItemUnderline = findViewById(R.id.selItemUnderline)

        val homeFragment = HomeFragment()
        val exploreFragment = ExploreFragment()
        val preferredFragment = PreferredFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment, false)

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> {
                    selItemUnderline.x = getLinePos(R.id.menu_item_home)
                    setCurrentFragment(homeFragment, false)
                }
                R.id.menu_item_explore -> {
                    selItemUnderline.x = getLinePos(R.id.menu_item_explore)
                    setCurrentFragment(exploreFragment, false)
                }
               R.id.menu_item_preferred -> {
                    selItemUnderline.x = getLinePos(R.id.menu_item_preferred)
                    setCurrentFragment(preferredFragment, false)
                }
                R.id.menu_item_profile -> {
                    selItemUnderline.x = getLinePos(R.id.menu_item_profile)
                    setCurrentFragment(profileFragment, true)
                }
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun setCurrentFragment(fragment: Fragment, addToBackStack: Boolean){
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.fragmentView, fragment)
        if(addToBackStack){
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun getLinePos(resId: Int): Float = findViewById<View>(resId).x + (findViewById<View>(resId).width/2) - (selItemUnderline.width/2)

}