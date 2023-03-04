package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofcorout.App
import com.example.retrofcorout.R
import com.example.retrofcorout.data.api.ApiHelper
import com.example.retrofcorout.data.api.RetrofitBuilder
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.FragmentListBinding
import com.example.retrofcorout.ui.adapter.ListUsersAdapter
import com.example.retrofcorout.ui.base.ViewModelFactory
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.ui.viewmodel.ResponseState

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        ViewModelFactory(
            ApiHelper(RetrofitBuilder.apiService),
            (requireActivity().application as App).userDao
        )
    }
    private lateinit var adapter: ListUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addOptionsMenu()

        setupUI()
        setupObservers()
        viewModel.getUsers()
    }

    private fun setupUI() =
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@ListFragment.context)
            adapter = ListUsersAdapter(arrayListOf(), this@ListFragment)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            recyclerView.adapter = adapter
            adapter.clickListenerToEdit.observe(viewLifecycleOwner) {
                operate(Operation.DETAIL, null, it)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT + ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    operate(Operation.REMOVE, position)
                }
            }).attachToRecyclerView(recyclerView)

            fab.setOnClickListener {
                operate(Operation.NEW)
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

        viewModel.editUser.observe(viewLifecycleOwner, Observer { response ->
            //    viewModel.getUsers()
            adapter.apply {
                (response as? ResponseState.Success)?.let {
                    notifyItemChanged(updateUser(it.data!!))
                }
            }
        })

        viewModel.newUser.observe(viewLifecycleOwner, Observer { response ->
            //     viewModel.getUsers()
            adapter.apply {
                (response as? ResponseState.Success)?.let {
                    notifyItemInserted(insertUser(it.data!!))
                }
            }
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

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        //  super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate((R.menu.item_menu), menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite_item -> {
                operate(Operation.FAVORITE_ADD, null, adapter.menuUser)
            }
            R.id.edit_item -> {
                operate(Operation.DETAIL, null, adapter.menuUser)
            }
            R.id.remove_item -> {
                operate(Operation.REMOVE, adapter.menuPosition)
            }
        }
        return false
    }

    private fun operate(operation: Operation, position: Int? = null, user: User? = null) {
        when (operation) {
            Operation.NEW -> {
                openFragment(DetailFragment.newInstance())
            }
            Operation.DETAIL -> {
                user?.let { openFragment(DetailFragment.newInstance(user.id)) }
            }
            Operation.FAVORITE_ADD -> {
                user?.let {
                    viewModel.insertUserDao(user)
                    openFragment(FavoriteFragment.newInstance(user.id))
                }
            }
            Operation.FAVORITE_ALL -> {
                openFragment(FavoriteFragment.newInstance())
            }
            Operation.REMOVE -> {
                position?.let {
                    adapter.apply {
                        viewModel.deleteUser(deleteUser(position))
                        notifyItemRemoved(position)
                    }
                }

            }
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

    fun addOptionsMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        operate(Operation.FAVORITE_ALL)
                        true
                    }
                    else -> false
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        addOptionsMenu()
    }

    enum class Operation {
        DETAIL, REMOVE, NEW, FAVORITE_ADD, FAVORITE_ALL
    }
}