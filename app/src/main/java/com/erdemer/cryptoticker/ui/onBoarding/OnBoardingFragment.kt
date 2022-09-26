package com.erdemer.cryptoticker.ui.onBoarding

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentOnBoardingBinding
import com.erdemer.cryptoticker.ui.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

class OnBoardingFragment :
    BaseFragment<FragmentOnBoardingBinding>(FragmentOnBoardingBinding::inflate) {
    override fun onCreateFinished() {
        navigateIfUserLoggedIn()
    }

    private fun navigateIfUserLoggedIn() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.homeFragment,null, NavOptions.Builder().setPopUpTo(R.id.onBoardingFragment,true).build())
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
            }
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_onBoardingFragment_to_registerFragment)
            }
        }
    }
}