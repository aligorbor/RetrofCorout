package com.example.retrofcorout.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.retrofcorout.R
import com.example.retrofcorout.data.model.User
import com.example.retrofcorout.databinding.FragmentDetailBinding
import com.example.retrofcorout.ui.viewmodel.MainViewModel
import com.example.retrofcorout.ui.viewmodel.ResponseState
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_ID = "user_id"
private const val FROM_DAO = "from_dao"

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var userId: String? = null
    private var userCurrent =
        User(id="", avatar ="https://loremflickr.com/640/480/girl", email = "", name = "", dateBirth = Date())
    private val viewModel: MainViewModel by activityViewModels()

    private var clickedSave :Boolean = false   //to avoid false observing of previous data
    private var fromDao :Boolean = false // true - observe ROOM; false - observe RETROFIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val safeArgs: DetailFragmentArgs by navArgs()
        userId = safeArgs.userId
        fromDao = safeArgs.fromDao
//        arguments?.let {
//            userId = it.getString(ARG_ID)
//            fromDao = it.getBoolean(FROM_DAO)
//        }
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
            val strFormat = view.context.getString(R.string.date_format)
            val myFormat = SimpleDateFormat(strFormat, Locale.getDefault(Locale.Category.FORMAT))
            val dateBirth: Date? = myFormat.parse(binding.textViewUserBirthday.text.toString(), ParsePosition(0))
            userCurrent.dateBirth = dateBirth
            if (userCurrent.name.isEmpty() || userCurrent.email.isEmpty() || userCurrent.dateBirth==null) {
                Toast.makeText(
                    this.context,
                    "Name, E-mail and Birthday must be filled to save",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (fromDao) {
                    userId?.let{
                        viewModel.updateUserDao(userCurrent)
                    }
                } else {
                    userId?.let {
                        viewModel.putEditUser(userCurrent)
                    } ?: viewModel.postNewUser(userCurrent)
                    clickedSave = true
                }
            }
      //      requireActivity().supportFragmentManager.popBackStack()
            findNavController().popBackStack()
        }
    }

    private fun setupUI(user: User) =
        with(binding) {
            varUser=user
            progressBar.visibility = View.GONE
        }

    private fun setupObservers(userId: String?) {
        if (fromDao){
            userId?.let{ idUser->
                viewModel.getUserDao(userId).observe(viewLifecycleOwner, Observer {
                    observed(it)
                })
            } ?: setupUI(userCurrent)
        } else {
            userId?.let{ idUser->
                viewModel.getUser(idUser).observe(viewLifecycleOwner, Observer {
                    observed(it)
                })
            } ?: setupUI(userCurrent)

            userId?.let { idUser->
                viewModel.editUser.observe(viewLifecycleOwner, Observer {
                    if (clickedSave) {
                        observed(it)
                        clickedSave = !clickedSave
                    }
                })
            } ?: viewModel.newUser.observe(viewLifecycleOwner, Observer {
                if (clickedSave) {
                    observed(it)
                    clickedSave = !clickedSave
                }
            })
        }

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
        fun newInstance(userId: String? = null, fromDao: Boolean = false) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, userId)
                    putBoolean(FROM_DAO, fromDao)
                }
            }
    }
}