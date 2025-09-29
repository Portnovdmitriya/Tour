package com.example.tourguideplus.ui.auth

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private var _b: FragmentAuthBinding? = null
    private val b get() = _b!!
    private lateinit var vm: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentAuthBinding.inflate(inflater, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this, AuthViewModelFactory(requireActivity().application as TourGuideApp))
            .get(AuthViewModel::class.java)

        b.btnLogin.setOnClickListener {
            val u = b.etUsername.text.toString().trim()
            val p = b.etPassword.text.toString().trim()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            } else vm.login(u, p)
        }
        b.btnRegister.setOnClickListener {
            val u = b.etUsername.text.toString().trim()
            val p = b.etPassword.text.toString().trim()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            } else vm.register(u, p)
        }

        vm.state.observe(viewLifecycleOwner) { st ->
            b.progress.isVisible = (st is AuthState.Loading)
            when (st) {
                is AuthState.Success -> {
                    Toast.makeText(requireContext(), "Здравствуйте, ${st.username}", Toast.LENGTH_SHORT).show()
                    // вернёмся на главный экран
                    findNavController().navigate(R.id.placesFragment)
                }
                is AuthState.Error -> {
                    Toast.makeText(requireContext(), st.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
