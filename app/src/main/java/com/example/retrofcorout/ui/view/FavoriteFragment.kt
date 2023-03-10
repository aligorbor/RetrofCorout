package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
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

        addOptionsMenu()

        setupUI()
        setupObservers()
    }

    private fun setupUI() =
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@FavoriteFragment.context)
            adapter = FavoriteAdapter(this@FavoriteFragment)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            recyclerView.adapter = adapter
            adapter.clickListenerToEdit.observe(viewLifecycleOwner) {
                //openFragment(DetailFragment.newInstance(it.id))
                operate(Operation.DETAIL, it)
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
                 //   val position = viewHolder.bindingAdapterPosition
                    val item = (viewHolder as FavoriteAdapter.DataViewHolder).userInViewHolder
                    operate(Operation.REMOVE, item)
                }
            }).attachToRecyclerView(recyclerView)

        }

    private fun setupObservers() {
        viewModel.listUsersDao.observe(viewLifecycleOwner, Observer  {
            with(binding) {
                it?.let { resource ->
                    when (resource) {
                        is ResponseState.Success -> {
                            recyclerView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            resource.data?.let { users -> users.let { adapter.submitList(it) } }
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

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        //  super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate((R.menu.item_favorite_menu), menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_favorite_item -> {
                operate(Operation.DETAIL, adapter.menuUser)
            }
            R.id.remove_favorite_item -> {
                operate(Operation.REMOVE, adapter.menuUser)
            }
        }
        return false
    }

    private fun operate(operation: Operation,  user: User? = null) {
        when (operation) {
            Operation.DETAIL -> {
                user?.let {
              //      openFragment(DetailFragment.newInstance(user.id,true))
                    val action = FavoriteFragmentDirections.actionFavoriteDestToDetailDest(user.id,true)
                    findNavController().navigate(action)
                }

            }
            Operation.REMOVE -> {
                    adapter.apply {
                        user?.let { viewModel.deleteUserDao(it) }
                    }

            }
            Operation.REMOVE_ALL -> {
                    adapter.apply {
                        viewModel.deleteAllUsersDao()
                    }
            }
//            Operation.FAVORITE_ALL -> {
//                openFragment(FavoriteFragment.newInstance())
//            }
        }
    }

    fun addOptionsMenu(addListMenu: Boolean = false){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (addListMenu) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                } else {
                    menuInflater.inflate(R.menu.menu_favorite_main, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (addListMenu) {
                    menuItem.onNavDestinationSelected(findNavController())
//                    when (menuItem.itemId) {
//                        R.id.favorite_dest -> {
//                            operate(Operation.FAVORITE_ALL)
//                            true
//                        }
//                        else -> false
//                    }

                } else {
                    when (menuItem.itemId) {
                        R.id.action_delete_favorites -> {
                            operate(Operation.REMOVE_ALL)
                            true
                        }
                        else -> false
                    }
                }
            }

        })
    }

    override fun onPause() {
        super.onPause()
 //       addOptionsMenu(true)
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
        DETAIL, REMOVE, REMOVE_ALL   //, FAVORITE_ALL
    }
}