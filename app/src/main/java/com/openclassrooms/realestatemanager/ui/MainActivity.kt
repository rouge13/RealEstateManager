package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.databinding.ActivityMainNavHeaderBinding
import com.openclassrooms.realestatemanager.ui.propertyList.PropertyListFragmentDirections
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.InitializationViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var activityMainNavHeaderBinding: ActivityMainNavHeaderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var sharedAgentViewModel: SharedAgentViewModel
    private lateinit var initializationViewModel: InitializationViewModel
    private lateinit var sharedNavigationViewModel: SharedNavigationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        // Init the ViewModel
        initViewModels()
        drawerLayout = activityMainBinding.activityMainDrawerLayout
        navigationView = activityMainBinding.activityMainNavView
        // Set up the drawer toggle
        setupDrawerButton()
        // Get the header view and bind it to the activityMainNavHeaderBinding
        activityMainNavHeaderBinding =
            ActivityMainNavHeaderBinding.bind(navigationView.getHeaderView(0))
        // Observe the loged agent to update the UI
        observeLogedAgent()
        // Initialize the app using the InitializationViewModel
        initializationViewModel.startInitialization(application as MainApplication)
        // Set up the NavController and connect it to the NavigationView
        setupNavigationController()
        checkHasInternet()
        initAddOnClickListeners()
        initModifyOnClickListeners()
        initSearchOnClickListeners()
    }

    private fun initModifyOnClickListeners() {
        activityMainBinding.btnModify.setOnClickListener {
            if (navController.currentDestination?.id != R.id.addAndModifyPropertyFragment) {
                navController.navigate(R.id.addAndModifyPropertyFragment)
                sharedNavigationViewModel.setAddOrModifyClicked(true)
            } else {
                navController.popBackStack() // Go back to the previous fragment
            }
        }
    }

    private fun initAddOnClickListeners() {
        activityMainBinding.btnAdd.setOnClickListener {
            if (navController.currentDestination?.id != R.id.addAndModifyPropertyFragment) {
                navController.navigate(R.id.addAndModifyPropertyFragment)
                sharedNavigationViewModel.setAddOrModifyClicked(false)
            } else {
                navController.popBackStack() // Go back to the previous fragment
            }
        }
    }

    private fun initViewModels() {
        sharedAgentViewModel =
            ViewModelProvider(this, ViewModelFactory(application as MainApplication)).get(
                SharedAgentViewModel::class.java
            )
        initializationViewModel =
            ViewModelProvider(this, ViewModelFactory(application as MainApplication)).get(
                InitializationViewModel::class.java
            )
        sharedNavigationViewModel =
            ViewModelProvider(this, ViewModelFactory(application as MainApplication)).get(
                SharedNavigationViewModel::class.java
            )

    }

    private fun initSearchOnClickListeners() {
        activityMainBinding.btnSearch.setOnClickListener {
            if (navController.currentDestination?.id != R.id.searchFragment) {
                navController.navigate(R.id.searchFragment)
            } else {
                navController.popBackStack() // Go back to the previous fragment
            }
        }
    }


    private fun setupNavigationController() {
        // Init the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem, navController)
        }
    }

    private fun handleNavigationItemSelected(
        item: MenuItem,
        navController: NavController
    ): Boolean {
        when (item.itemId) {
            R.id.nav_take_photo -> {
                dispatchTakePictureIntent()
                drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer
                return true
            }

            R.id.nav_logout -> {
                logOutActions()
                return true
            }

            R.id.nav_login -> {
                logInActions()
                return true
            }

            else -> {
                // Let the NavController handle the other menu items
                val handled =
                    item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
                if (handled) {
                    drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer if an item is handled
                }
                return handled
            }
        }
    }

    private fun observeLogedAgent() {
        sharedAgentViewModel.loggedAgent.observe(this) { agent ->
            if (agent != null) forAgentConnected(agent) else forAgentNotConnected()
        }
    }

    private fun forAgentNotConnected() {
        showTheLogIn()
        showDefaultNavHeaderNotConnected()
    }

    private fun showDefaultNavHeaderNotConnected() {
        activityMainNavHeaderBinding.username.text =
            getString(R.string.text_view_property_agent_name)
        activityMainNavHeaderBinding.userEmail.text =
            getString(R.string.text_view_property_agent_email)
        setImageProfileDefault()
    }

    private fun showTheLogIn() {
        navigationView.menu.findItem(R.id.nav_login).isVisible = true
        navigationView.menu.findItem(R.id.nav_logout).isVisible = false
    }

    private fun forAgentConnected(agent: AgentEntity) {
        showTheLogOut()
        showAgentNavHeaderConnected(agent)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun checkHasInternet() {
        if (isOnline(this)) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            } else {
                agentLocationUpdates()
            }
            setupBottomNavigation()
            activityMainBinding.viewPager.currentItem = 0 // Switch to the PropertyListFragment
        } else {
            activityMainBinding.bottomNavigationView.visibility = View.GONE
        }
    }

    private fun agentLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, getString(R.string.gps_enavailable), Toast.LENGTH_LONG).show()
            }
        }

    private fun requestLocationUpdates() {
        sharedAgentViewModel.startLocationUpdates()
    }

    private fun showAgentNavHeaderConnected(agent: AgentEntity) {
        activityMainNavHeaderBinding.username.text = "${agent.firstName} ${agent.lastName}"
        activityMainNavHeaderBinding.userEmail.text = agent.email
        setImageProfileAgent(agent)
    }

    private fun setImageProfileAgent(agent: AgentEntity) {
        val photoProfileImageView = activityMainNavHeaderBinding.photoProfileImageView
        photoProfileImageView.setImageResource(
            this.resources.getIdentifier(agent.photo, "drawable", this.packageName)
        )
    }

    private fun setImageProfileDefault() {
        val photoProfileImageView = activityMainNavHeaderBinding.photoProfileImageView
        photoProfileImageView.setImageResource(
            this.resources.getIdentifier("ic_must_be_connected", "drawable", this.packageName)
        )
    }

    private fun showTheLogOut() {
        navigationView.menu.findItem(R.id.nav_login).isVisible = false
        navigationView.menu.findItem(R.id.nav_logout).isVisible = true
    }

    private fun setupBottomNavigation() {
        activityMainBinding.bottomNavigationView.visibility = View.VISIBLE
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_fragment -> {
                    navController.popBackStack(R.id.propertyListFragment, false)
                    navController.navigate(R.id.propertyListFragment)
                    true
                }

                R.id.map_fragment -> {
                    navController.popBackStack(R.id.mapFragment, false)
                    navController.navigate(R.id.mapFragment)
                    true
                }

                else -> false
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_agent_info_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                    invalidateOptionsMenu()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logInActions() {
        navController.navigate(PropertyListFragmentDirections.actionPropertyListFragmentToLoginFragment())
        drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer
    }

    private fun logOutActions() {
        navigationView.menu.findItem(R.id.nav_login).isVisible = true
        sharedAgentViewModel.setLogedAgent(null)
        observeLogedAgent()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer
    }

    private fun setupDrawerButton() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            activityMainBinding.activityMainToolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
        } else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user in a Toast
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}