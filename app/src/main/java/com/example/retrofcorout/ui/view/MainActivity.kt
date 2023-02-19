package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.api.RetrofitBuilder
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.ActivityMainBinding
import com.example.retrofcorout.ui.adapter.MainAdapter
import com.example.retrofcorout.ui.base.ViewModelFactory
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.utils.Status

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViewModel()
        setupUI()
        setupObservers()
    }

    private fun setupUI() =
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MainAdapter(arrayListOf())
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            recyclerView.adapter = adapter
        }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, Observer {
            with(binding) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            recyclerView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            resource.data?.let { users -> retrieveList(users) }
                        }
                        Status.ERROR -> {
                            recyclerView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun retrieveList(users: List<User>) {
        adapter.apply {
            addUsers(users)
            notifyDataSetChanged()
        }
    }

}