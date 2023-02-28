package com.example.retrofcorout.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.retrofcorout.R
import com.example.retrofcorout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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

        savedInstanceState ?: run {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListFragment.newInstance())
                .commitNow()
        }
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