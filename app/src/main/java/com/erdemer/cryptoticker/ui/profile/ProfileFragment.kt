package com.erdemer.cryptoticker.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.erdemer.cryptoticker.R
import com.erdemer.cryptoticker.databinding.FragmentProfileBinding
import com.erdemer.cryptoticker.ui.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun onCreateFinished() {
        binding.tvWelcomeText.text = getString(R.string.profile_welcome_text,Firebase.auth.currentUser?.displayName)
    }

    override fun initListeners() {
        super.initListeners()
        val user = Firebase.auth.currentUser
        binding.tvLogout.setOnClickListener {
            showDialog("Are you sure you want to logout?", dialogType = DialogType.WARNING)  {
                Firebase.auth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_onBoardingFragment)
            }
        }
        binding.tvChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeEmailFragment)
        }
    }

}