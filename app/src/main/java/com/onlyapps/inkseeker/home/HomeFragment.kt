package com.onlyapps.inkseeker.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.opengl.Visibility
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.onlyapps.inkseeker.HomeScrollView
import com.onlyapps.inkseeker.R
import com.onlyapps.inkseeker.Auth.login.LoginFragment
import com.onlyapps.inkseeker.preferred.PreferredViewModel
import com.onlyapps.inkseeker.requests
import com.onlyapps.inkseeker.tatooer.StudioDataClass
import org.json.JSONArray
import java.util.Objects


class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var mapHeight = 0

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var preferredViewModel: PreferredViewModel

    private lateinit var studiosData: JSONArray
    private lateinit var filteredStudios: JSONArray

    private lateinit var filterCard: CardView
    private lateinit var filterOptionsConnector: CardView
    private lateinit var filterOptionsCard: CardView

    private lateinit var filterPiercingCard: CardView
    private lateinit var filterPiercingRadioBtn: RadioButton

    private lateinit var filterStylesCard: CardView
    private lateinit var filterStylesSymbol: ImageView
    private lateinit var filterStylesRecycler: RecyclerView
    private lateinit var filterStylesDataList: ArrayList<StyleDataClass>
    private lateinit var filterStylesConfirmBtn: TextView

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private lateinit var scrollView: HomeScrollView
    private lateinit var studiosCard: CardView
    private lateinit var studiosCardExtender: CardView

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: HomeAdapterClass
    private lateinit var dataList: ArrayList<HomeDataClass>

    private var FINE_PERMISSION_CODE: Int = 1
    private val defaultLocation = LatLng(45.4654219, 9.1859243)
    private var  currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var heightMap: Int = 0
    private var heightRecyclerView: Int = 0

    private var userId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun initViewModel(){
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        preferredViewModel = ViewModelProvider(this).get(PreferredViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = getUserId()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        getStudiosData()
        filteredStudios = studiosData

        getHeights()

        mapView = view.findViewById(R.id.mapView)
        var params = mapView.layoutParams
        params.height = heightMap
        mapView.layoutParams = params

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        filterOptionsCard = view.findViewById(R.id.homeFilterOptionsBackgroundCard)

        filterPiercingCard = view.findViewById(R.id.home_filter_piercing_card)
        filterPiercingRadioBtn = view.findViewById(R.id.home_filter_piercing_radioBtn)

        filterPiercingCard.setOnClickListener { view ->
            if (filterPiercingRadioBtn.isChecked == true) {
                filterPiercingRadioBtn.isChecked = false
                filteredStudios = studiosData
            } else {
                filterPiercingRadioBtn.isChecked =  true
                filteredStudios = JSONArray()
                // 0 - id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles, 7 - lat, 8 - lng
                for (i in 0 until studiosData.length()) {
                    val studio = JSONArray(studiosData.getString(i))
                    if (toBoolean(studio.getInt(5))) {
                        filteredStudios.put(studio)
                    }
                }
            }
            setRecyclerViewItems()
            getStylesData()
        }

        filterStylesCard = view.findViewById(R.id.home_filter_styles_card)
        filterStylesSymbol = view.findViewById(R.id.home_filter_styles_symbol)

        filterStylesConfirmBtn = view.findViewById(R.id.home_filter_styles_confirm)
        filterStylesConfirmBtn.setOnClickListener { view ->
            filterPiercingRadioBtn.isChecked = false
            filteredStudios = JSONArray()
            val stylesToShow = getStylesToShow(filterStylesDataList)
            for (style in stylesToShow) {
                for (i in 0 until studiosData.length()) {
                    // 0 - id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles, 7 - lat, 8 - lng
                    val studio = JSONArray(studiosData.getString(i))
                    if ( (style in studio.getString(6)) and (indexOf(studio, filteredStudios) == -1) ) {
                        filteredStudios.put(studio)
                    }
                }
            }
            setRecyclerViewItems()
            updateStudiosData()
        }

        filterStylesCard.setOnClickListener { view ->
            if (filterStylesRecycler.visibility == View.GONE) {
                filterStylesRecycler.visibility = View.VISIBLE
                filterStylesConfirmBtn.visibility = View.VISIBLE
                filterStylesSymbol.background = resources.getDrawable(R.drawable.ic_minus)
            } else {
                filterStylesRecycler.visibility = View.GONE
                filterStylesConfirmBtn.visibility = View.GONE
                filterStylesSymbol.background = resources.getDrawable(R.drawable.ic_plus)
            }
        }

        filterStylesRecycler = view.findViewById(R.id.home_filter_styles_recycler)
        filterStylesRecycler.layoutManager = LinearLayoutManager(requireContext())
        filterStylesRecycler.setHasFixedSize(true)

        filterStylesDataList = arrayListOf()

        getStylesData()

        filterCard = view.findViewById(R.id.homeMapFilter)
        filterCard.setOnClickListener { view ->
            if (filterOptionsCard.visibility == View.GONE) {
                filterOptionsCard.visibility = View.VISIBLE
                //filterOptionsConnector.visibility = View.VISIBLE
            } else {
                filterOptionsCard.visibility = View.GONE
                //filterOptionsConnector.visibility = View.GONE
            }
        }

        recyclerView = view.findViewById(R.id.studios_list)
        params = recyclerView.layoutParams
        params.height = heightRecyclerView
        recyclerView.layoutParams = params

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf()

        scrollView = view.findViewById(R.id.home_scrollView)
        studiosCard = view.findViewById(R.id.home_studios_recycler_container)
        studiosCardExtender = view.findViewById(R.id.home_studios_recycler_container_extender)

        studiosCard.setOnTouchListener { v, event ->
            scrollView.isEnableScrolling = true
            return@setOnTouchListener true
        }

        studiosCardExtender.setOnTouchListener { v, event ->
            scrollView.isEnableScrolling = true
            return@setOnTouchListener true
        }

        recyclerView.setOnTouchListener { v, event ->
            scrollView.isEnableScrolling = true
            return@setOnTouchListener false
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onMapReady(map: GoogleMap) {
        map?.let {
            googleMap = it
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15.0f))
        if (currentLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 15.0f))
        }

        googleMap.setOnCameraMoveStartedListener({ i ->
            if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                scrollView.isEnableScrolling = false
            }
        })
        getLastLocation()

        setRecyclerViewItems()
        updateStudiosData()
    }

    private fun getHeights() {
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        val display = wm.defaultDisplay
        display.getSize(size)
        val heighty = size.y

        heightMap = Math.rint(heighty / 1.125).toInt()
        heightRecyclerView = Math.rint(heighty / 1.35).toInt()
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }
        var task: Task<Location> = fusedLocationProviderClient.getLastLocation()
        task.addOnSuccessListener { location ->
            if (location != null) {
                this.currentLocation = location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this.requireContext(), R.string.denied_permission, Toast.LENGTH_SHORT)
            }
        }
    }

    private fun getStudiosData() {
        try {
            studiosData = JSONArray(requests.GET("http://giobra.com:5000/getStudios/"))
        } catch (e: Exception) {
            Log.e("REQUEST ERROR", e.stackTraceToString())
            getStudiosData()
        }
    }

    private fun setRecyclerViewItems() {
        dataList = arrayListOf()
        for (i in 0 until filteredStudios.length()) {
            // 0- id, 1 - name, 2 - address, 3 - pic, 4 - stars, 5 - piercing, 6 - styles, 7 - lat, 8 - lng
            val studio = filteredStudios.getJSONArray(i)
            dataList.add(HomeDataClass(studio.getInt(0), studio.getString(3), studio.getString(1), studio.getString(2), studio.getString(4), studio.getDouble(7), studio.getDouble(8)))
            var markerOptions = MarkerOptions().position(LatLng(studio.getDouble(7), studio.getDouble(8)))
            markerOptions.draggable(false)
            markerOptions.title(studio.getString(1))
            googleMap.addMarker(markerOptions)
        }

        itemAdapter = HomeAdapterClass(dataList, parentFragmentManager, userId, requireContext(), preferredViewModel, googleMap)
        recyclerView.adapter = itemAdapter
    }

    private fun getUserId(): Int {
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        var gsc = GoogleSignIn.getClient(this.requireContext(), gso)

        val firebaseUser = Firebase.auth.currentUser
        val googleUser: GoogleSignInAccount?

        var emailUser = ""

        if (firebaseUser == null) {
            googleUser = GoogleSignIn.getLastSignedInAccount(this.requireContext())
            if (googleUser == null) {
                return -1
            } else {
                emailUser = googleUser.email.toString()
            }
        } else {
            emailUser = firebaseUser.email.toString()
        }
        return selectUserId(emailUser)
    }

    private fun selectUserId(email: String): Int {
        try {
            return JSONArray(requests.POST("http://giobra.com:5000/getUser/", arrayOf( arrayOf("email", email) ))).getInt(0)
        } catch (e: Exception) {
            Log.e("REQUESTS ERROR", e.stackTraceToString())
            return selectUserId(email)
        }
    }

    private fun getStylesData() {
        filterStylesDataList = arrayListOf()
        try {
            var styles = JSONArray(requests.GET("http://giobra.com:5000/getStyles/"))
            for (i in 0 until styles.length()) {
                val style = styles.getJSONArray(i) // 1 - id
                filterStylesDataList.add(StyleDataClass(style.getString(0), true))
            }

            val itemAdapter = StyleAdapterClass(filterStylesDataList, requireContext())
            filterStylesRecycler.adapter = itemAdapter
        } catch (e: Exception) {
            Log.e("REQUEST ERROR", e.stackTraceToString())
            getStylesData()
        }
    }

    private fun getStylesToShow(styles: ArrayList<StyleDataClass>): ArrayList<String> {
        var stylesToShow: ArrayList<String> = arrayListOf()

        for (style in styles) {
            if (style.isSelected) {
                stylesToShow.add(style.id)
            }
        }
        return stylesToShow
    }

    private fun toBoolean(int: Int): Boolean {
        if (int == null) {
            return false
        }
        if (int == 1) {
            return true
        }
        return false
    }

    private fun indexOf(obj: JSONArray, list: JSONArray): Int {
        for (i in 0 until list.length()) {
            val currentItem = JSONArray(list.getString(i))
            if (currentItem.toString() == obj.toString()) {
                return i
            }
        }
        return -1
    }


    private fun updateStudiosData(){
        homeViewModel.getStudiosData()
        homeViewModel.studioData.observe(viewLifecycleOwner, Observer {dataList ->
            itemAdapter.updateData(dataList)
        })
    }

}