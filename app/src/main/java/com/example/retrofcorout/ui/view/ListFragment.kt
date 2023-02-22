package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofcorout.R
import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.api.RetrofitBuilder
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.FragmentListBinding
import com.example.retrofcorout.ui.adapter.MainAdapter
import com.example.retrofcorout.ui.base.ViewModelFactory
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.ui.viewmodel.ResponseState

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        ViewModelFactory(
            ApiHelper(
                RetrofitBuilder.apiService
            )
        )
    }
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        viewModel.getUsers()
    }

    private fun setupUI() =
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@ListFragment.context)
            adapter = MainAdapter(arrayListOf())
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            recyclerView.adapter = adapter
            adapter.clickListenerToEdit.observe(viewLifecycleOwner) {
                openFragment(DetailFragment.newInstance(it.id))
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT+ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                   val position = viewHolder.bindingAdapterPosition
                    adapter.apply {
                        viewModel.deleteUser(deleteUser(position))
                        notifyItemRemoved(position)
                    }
                }
            }).attachToRecyclerView(recyclerView)

            fab.setOnClickListener {
                openFragment(DetailFragment.newInstance())
            }
        }

    private fun setupObservers() {
        viewModel.listUsers.observe(viewLifecycleOwner, Observer {
            with(binding) {
                it?.let { resource ->
                    when (resource) {
                        is ResponseState.Success -> {
                            recyclerView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            resource.data?.let { users -> retrieveList(users) }
                        }
                        is ResponseState.Error -> {
                            recyclerView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ListFragment.context,
                                resource.error.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is ResponseState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        })

        viewModel.editUser.observe(viewLifecycleOwner, Observer {
            viewModel.getUsers()
        })

        viewModel.newUser.observe(viewLifecycleOwner, Observer {
            viewModel.getUsers()
        })

        viewModel.delUser.observe(viewLifecycleOwner, Observer {
            with(binding) {
                it?.let { resource ->
                    when (resource) {
                        is ResponseState.Success -> {
                            progressBar.visibility = View.GONE
                        }
                        is ResponseState.Error -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ListFragment.context,
                                resource.error.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is ResponseState.Loading -> {
                            progressBar.visibility = View.VISIBLE
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

    private fun openFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ListFragment()
    }
}