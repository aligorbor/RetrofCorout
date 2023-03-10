package com.example.retrofcorout.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.retrofcorout.R
import com.example.retrofcorout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        with(binding) {
            setSupportActionBar(toolbar)
            val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
            val navController = host.navController
     //       appBarConfiguration = AppBarConfiguration(navController.graph)
            appBarConfiguration = AppBarConfiguration(setOf(R.id.users_dest, R.id.favorite_dest))
            setupActionBarWithNavController(navController, appBarConfiguration)
            bottomNavView.setupWithNavController(navController)
        }


//        addMenuProvider(object : MenuProvider{
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_main,menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId){
//                    R.id.action_favorite -> {
//                        openFragment(DetailFragment.newInstance())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
//        )


//        savedInstanceState ?: run {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, ListFragment.newInstance())
//                .commitNow()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
       return findNavController(R.id.my_nav_host_fragment).navigateUp(appBarConfiguration)
    }

//    private fun openFragment(fragment: Fragment) {
//        supportFragmentManager?.apply {
//            beginTransaction()
//                .add(R.id.container, fragment)
//                .addToBackStack("")
//                .commitAllowingStateLoss()
//        }
//    }

}