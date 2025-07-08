package com.example.tourguideplus.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tourguideplus.R
import com.example.tourguideplus.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Автор работы
        binding.tvAuthor.text = "Автор работы: Иванов Иван Иванович"

        // Версия приложения через PackageManager
        val version = try {
            requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0)
                .versionName
        } catch (e: Exception) {
            "?"
        }
        binding.tvVersion.text = "Версия приложения: $version"

        // Описание и контакты из строковых ресурсов
        binding.tvDescription.text = getString(R.string.help_description)
        binding.tvContact.text     = getString(R.string.help_contact)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
