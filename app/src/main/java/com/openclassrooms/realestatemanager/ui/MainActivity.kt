package com.openclassrooms.realestatemanager.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.openclassrooms.realestatemanager.R

import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.databinding.ActivityMainNavHeaderBinding
import com.openclassrooms.realestatemanager.ui.property_list.PropertyListFragment
import com.openclassrooms.realestatemanager.ui.login.LoginFragment
import com.openclassrooms.realestatemanager.ui.map.MapFragment
import com.openclassrooms.realestatemanager.ui.property_list.PropertyListFragmentDirections
import com.openclassrooms.realestatemanager.ui.register.RegisterFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.viewmodel.InitializationViewModel


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var activityMainNavHeaderBinding: ActivityMainNavHeaderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val sharedAgentViewModel: SharedAgentViewModel by viewModels {
        ViewModelFactory(
            (application as MainApplication).agentRepository,
            (application as MainApplication).propertyRepository,
            (application as MainApplication).addressRepository,
            (application as MainApplication).photoRepository
        )
    }
    private val sharedPropertyViewModel: SharedPropertyViewModel by viewModels {
        ViewModelFactory(
            (application as MainApplication).agentRepository,
            (application as MainApplication).propertyRepository,
            (application as MainApplication).addressRepository,
            (application as MainApplication).photoRepository
        )
    }

    private val initializationViewModel: InitializationViewModel by viewModels {
        ViewModelFactory(
            (application as MainApplication).agentRepository,
            (application as MainApplication).propertyRepository,
            (application as MainApplication).addressRepository,
            (application as MainApplication).photoRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        drawerLayout = activityMainBinding.activityMainDrawerLayout
        navigationView = activityMainBinding.activityMainNavView
        // Set up the drawer toggle
        setupDrawerButton()
        // Set up the navigation view from the drawerLayout
        setupDrawerContent(navigationView)
        // Loat the initial fragment into the fragment container
//        loadInitialFragment()
        // Get the header view and bind it to the activityMainNavHeaderBinding
        activityMainNavHeaderBinding =
            ActivityMainNavHeaderBinding.bind(navigationView.getHeaderView(0))
        // Observe the loged agent to update the UI
        observeLogedAgent()
        // Initialize the app using the InitializationViewModel
        initializationViewModel.startInitialization(application as MainApplication)

    }

    private fun observeLogedAgent() {
        sharedAgentViewModel.logedAgent.observeForever {
            if (it != null) forAgentConnected(it) else forAgentNotConnected()
        }
    }

    private fun forAgentNotConnected() {
        showTheLogIn()
        showDefaultNavHeaderNotConnected()
        activityMainBinding.bottomNavigationView.visibility = View.GONE
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
        setupBottomNavigation()
        // Set the current item of the viewPager to the desired position
        activityMainBinding.viewPager.currentItem = 0 // or whichever position you want to switch to
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
        val viewPager = activityMainBinding.viewPager
        val adapter = ViewPagerAdapter(supportFragmentManager)
        // Add the PropertyListFragment and MapFragment to the adapter
        adapter.addFragment(PropertyListFragment(), "Property List")
        adapter.addFragment(MapFragment(), "Map")
        viewPager.adapter = adapter
        // Set the OnItemSelectedListener for the BottomNavigationView
        setOnItemSelectedListener()
    }

    private fun setOnItemSelectedListener() {
        activityMainBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.list_fragment -> {
                    activityMainBinding.viewPager.currentItem = 0
                    true
                }

                R.id.map_fragment -> {
                    activityMainBinding.viewPager.currentItem = 1
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            // Run camera to take a picture
            R.id.nav_take_photo -> {
                dispatchTakePictureIntent()
            }
            // Show settings from phone
            R.id.nav_settings -> {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
            // Logout agent from his account
            R.id.nav_logout -> {
                logOutActions()
            }
            // Login agent to his account
            R.id.nav_login -> {
                logInActions()
            }
        }
        drawerLayout = activityMainBinding.activityMainDrawerLayout
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logInActions() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(PropertyListFragmentDirections.actionPropertyListFragmentToLoginFragment())
    }

    private fun logOutActions() {
        navigationView.menu.findItem(R.id.nav_login).isVisible = true
        sharedAgentViewModel.setLogedAgent(null)
        observeLogedAgent()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener(this)
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

    private fun showLoginScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, LoginFragment())
            .addToBackStack(null)
            .commit()
    }


    private fun showRegisterScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
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