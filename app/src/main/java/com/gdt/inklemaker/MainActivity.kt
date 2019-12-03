package com.gdt.inklemaker

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.main_fab
import kotlinx.android.synthetic.main.inc_app_bar.toolbar

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemReselectedListener {

  private lateinit var navController: NavController
  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var bottomNav: BottomNavigationView

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    bottomNav = findViewById(R.id.main_bottom_nav_view)
    navController = findNavController(R.id.main_nav_host_fragment)
    appBarConfiguration = AppBarConfiguration(navGraph = navController.graph)

    bottomNav.setupWithNavController(navController)
    setupActionBarWithNavController(navController, appBarConfiguration)
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
      when (destination.id) {
        R.id.nav_bottombar_inkles, R.id.nav_bottombar_yarns -> {
          main_fab.show()
          showBottomNav()
          supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        R.id.nav_bottombar_settings -> {
          main_fab.hide()
          showBottomNav()
          supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        R.id.nav_add_yarn -> {
          main_fab.hide()
          hideBottomNav()
          supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_close_24px)
          }
        }
        R.id.nav_add_inkle -> {
          main_fab.hide()
          hideBottomNav()
          supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_close_24px)
          }
        }
        else -> {
          main_fab.hide()
          hideBottomNav()
          supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
      }
    }

    // Avoids reloading the fragment when an item is reselected
    bottomNav.setOnNavigationItemReselectedListener(this)
  }

  override fun onSupportNavigateUp(): Boolean {
    return navController.navigateUp()
  }

  override fun onNavigationItemReselected(item: MenuItem) {
    // do nothing
  }

  private fun hideBottomNav() {
    bottomNav.visibility = View.GONE
  }

  private fun showBottomNav() {
    bottomNav.visibility = View.VISIBLE
  }

}
