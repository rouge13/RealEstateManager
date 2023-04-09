package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.property_list.PropertyListFragment

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

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

        // Configure the toolbar
//        configureToolBar()




    }

//    private fun configureToolBar() {
//        setSupportActionBar(activityMainBinding.toolbar)
//        actionBar?.setDisplayHomeAsUpEnabled(true)
//    }

    private fun loadInitialFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_property_list, PropertyListFragment())
            .commit()
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle the navigation view item clicks here.
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
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
}