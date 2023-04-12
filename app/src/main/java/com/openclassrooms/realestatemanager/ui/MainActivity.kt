package com.openclassrooms.realestatemanager.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.example.realestatemanager.databinding.ActivityMainNavHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.ui.property_list.PropertyListFragment
import com.openclassrooms.realestatemanager.ui.login.LoginFragment
import com.openclassrooms.realestatemanager.ui.login.LoginFragmentListener
import com.openclassrooms.realestatemanager.ui.login.LoginViewModel
import com.openclassrooms.realestatemanager.ui.login.LoginViewModelFactory
import com.openclassrooms.realestatemanager.ui.register.RegisterFragment
import com.openclassrooms.realestatemanager.ui.register.RegisterFragmentListener

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    LoginFragmentListener, RegisterFragmentListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var activityMainNavHeaderBinding: ActivityMainNavHeaderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as MainApplication).agentRepository)
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
        loadInitialFragment()
        // Get the header view and bind it
        val headerView = navigationView.getHeaderView(0)
        activityMainNavHeaderBinding = ActivityMainNavHeaderBinding.bind(headerView)

        // Observe the loged agent to update the UI
        observeLogedAgent()
    }

    private fun observeLogedAgent() {
        viewModel.logedAgent.observe(this) { agent ->
            if (agent != null) {
                logOptions(navLogIn = false,navLogOut = true)
                activityMainNavHeaderBinding.username.text = "${agent.firstName}  ${agent.lastName}"
                activityMainNavHeaderBinding.userEmail.text = agent.email
                activityMainNavHeaderBinding.photoProfileImageView.setImageResource(
                    this.resources.getIdentifier(agent.photo, "drawable", this.packageName)
                )
            } else {
                logOptions(navLogIn = true,navLogOut = false)
            }
        }
    }

    private fun logOptions(navLogIn: Boolean, navLogOut: Boolean) {
        activityMainBinding.activityMainNavView.menu.findItem(R.id.nav_login).isVisible = navLogIn
        activityMainBinding.activityMainNavView.menu.findItem(R.id.nav_logout).isVisible = navLogOut
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_agent_info_menu, menu)
        menu?.findItem(R.id.nav_login)?.setOnMenuItemClickListener {
            showLoginScreen()
            true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close the drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            // Show settings from phone
            R.id.nav_settings -> {
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
            // Logout agent from his account
//            R.id.nav_logout -> {
//
//            }
            // Login agent to his account
            R.id.nav_login -> {
                val loginFragment = LoginFragment()
                showLoginScreen()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.login_container, loginFragment)
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun loadInitialFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_property_list, PropertyListFragment())
            .commit()
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
        ShowTheGoodFrameLayout(loginContainerShow = true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_container, LoginFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun hideLoginScreen() {
        ShowTheGoodFrameLayout(fragmentPropertyListShow = true)
    }

    override fun onLoginSuccess() {
        hideLoginScreen()
    }

    override fun onLoginCancel() {
        hideLoginScreen()
    }

    override fun onLoginSignUp() {
        ShowRegisterScreen()
    }

    override fun onRegisterSuccess() {
        hideRegisterScreen()
    }

    override fun onRegisterCancel() {
        hideRegisterScreen()
    }

    private fun hideRegisterScreen() {
        ShowTheGoodFrameLayout(loginContainerShow = true)
    }

    private fun ShowRegisterScreen() {
        ShowTheGoodFrameLayout(registerContainerShow = true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun ShowTheGoodFrameLayout(
        loginContainerShow: Boolean = false,
        registerContainerShow: Boolean = false,
        fragmentPropertyListShow: Boolean = false) {
        when (loginContainerShow) {
            true -> activityMainBinding.loginContainer.visibility = View.VISIBLE
            false -> activityMainBinding.loginContainer.visibility = View.GONE
        }
        when (registerContainerShow) {
            true -> activityMainBinding.registerContainer.visibility = View.VISIBLE
            false -> activityMainBinding.registerContainer.visibility = View.GONE
        }
        when (fragmentPropertyListShow) {
            true -> activityMainBinding.fragmentPropertyList.visibility = View.VISIBLE
            false -> activityMainBinding.fragmentPropertyList.visibility = View.GONE
        }
    }
}