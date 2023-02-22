package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.FragmentDetailBinding
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.ui.viewmodel.ResponseState

private const val ARG_ID = "user_id"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var userId: String? = null
    private var userCurrent =
        User("https://loremflickr.com/640/480/girl", "user@email", "", "User Name")
    private val viewModel: MainViewModel by activityViewModels()

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
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers(userId)

        binding.buttonSave.setOnClickListener {
            userCurrent.name = binding.textViewUserName.text.toString()
            userCurrent.email = binding.textViewUserEmail.text.toString()
            userId?.let {
                viewModel.putEditUser(userCurrent)
            } ?: viewModel.postNewUser(userCurrent)
        }
        setupUI(User("https://loremflickr.com/640/480/girl", "user@email", "", "User Name"))
    }

    private fun setupUI(user: User) =
        with(binding) {
            textViewUserName.setText(user.name)
            textViewUserEmail.setText(user.email)
            Glide.with(imageViewAvatar.context)
                .load(user.avatar)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .into(imageViewAvatar)
            progressBar.visibility = View.GONE
        }

    private fun setupObservers(userId: String?) {
        userId?.let{ idUser->
            viewModel.getUser(idUser).observe(viewLifecycleOwner, Observer {
                observed(it)
            })
        } ?: setupUI(userCurrent)

        userId?.let { idUser->
            viewModel.editUser.observe(viewLifecycleOwner, Observer {
                observed(it)
            })
        } ?: viewModel.newUser.observe(viewLifecycleOwner, Observer {
            observed(it)
        })
    }

    private fun observed(responseState: ResponseState<User>?) {
        with(binding) {
            responseState?.let { resource ->
                when (resource) {
                    is ResponseState.Success -> {
                        resource.data?.let { user ->
                            userId = user.id
                            userCurrent = user
                            setupUI(user)
                        }
                    }
                    is ResponseState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DetailFragment.context,
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String? = null) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, userId)
                }
            }
    }
}