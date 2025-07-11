package com.example.tourguideplus.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!
    private lateinit var vm: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentProfileBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, ProfileViewModelFactory(app))
            .get(ProfileViewModel::class.java)

        // Подписываемся на данные пользователя
        vm.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                b.etName.setText(user.name)
                b.etEmail.setText(user.email)
            }
        }

        // Сохраняем по кнопке
        b.btnSaveProfile.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            if (name.isEmpty()) {
                b.etName.error = "Введите имя"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                b.etEmail.error = "Введите email"
                return@setOnClickListener
            }
            vm.saveProfile(name, email)
            Snackbar.make(b.root, "Профиль сохранён", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
