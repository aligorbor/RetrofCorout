package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofcorout.R
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.FragmentFavoriteBinding
import com.example.retrofcorout.ui.adapter.FavoriteAdapter
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.ui.viewmodel.ResponseState

private const val ARG_ID = "user_id"

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private var userId: String? = null
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_favorite -> {
                        openFragment(DetailFragment.newInstance())
                        true
                    }
                    else -> false
                }
            }

        })

        setupUI()
        setupObservers()
        viewModel.getUsers()
    }

    private fun setupUI() =
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@FavoriteFragment.context)
            adapter = FavoriteAdapter(arrayListOf(), this@FavoriteFragment)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            recyclerView.adapter = adapter
            adapter.clickListenerToEdit.observe(viewLifecycleOwner) {
                //openFragment(DetailFragment.newInstance(it.id))
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
//                    adapter.apply {
//                        viewModel.deleteUser(deleteUser(position))
//                        notifyItemRemoved(position)
//                    }
                    operate(Operation.REMOVE, position)
                }
            }).attachToRecyclerView(recyclerView)

            fab.setOnClickListener {
                //       openFragment(DetailFragment.newInstance())
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
                                this@FavoriteFragment.context,
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
                                this@FavoriteFragment.context,
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
                operate(Operation.DETAIL, null, adapter.menuUser)
            }
            R.id.edit_item -> {
                operate(Operation.DETAIL, null, adapter.menuUser)
            }
            R.id.remove_item -> {
                operate(Operation.REMOVE, adapter.menuPosition)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun operate(operation: Operation, position: Int? = null, user: User? = null) {
        when (operation) {
            Operation.NEW -> {
                openFragment(DetailFragment.newInstance())
            }
            Operation.DETAIL -> {
                user?.let { openFragment(DetailFragment.newInstance(user.id)) }
            }
            Operation.FAVORITE -> {}
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
        fun newInstance(userId: String? = null) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, userId)
                }
            }
    }

    enum class Operation {
        DETAIL, FAVORITE, REMOVE, NEW
    }
}