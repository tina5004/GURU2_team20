package com.example.guru2_team20.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.guru2_team20.MainViewModel
import com.example.guru2_team20.R
import com.example.guru2_team20.data.model.Store
import com.example.guru2_team20.databinding.FragmentReportBinding
import com.google.android.gms.maps.model.LatLng


private const val ARG_LAT_LNG = "ARG_LAT_LNG"
private const val ARG_ADDRESS = "ARG_ADDRESS"


class ReportFragment : Fragment() {

    companion object {
        const val TAG = "ReportFragment"

        fun newInstance(latLng: LatLng, address: String): ReportFragment {
            val arguments = Bundle().apply {
                putParcelable(ARG_LAT_LNG, latLng)
                putString(ARG_ADDRESS, address)
            }

            return ReportFragment().apply {
                setArguments(arguments)
            }
        }
    }


    private var _latLng: LatLng? = null
    private val latLng get() = _latLng!!

    private var _address: String? = null
    private val address get() = _address!!

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            _latLng = requireArguments().getParcelable(ARG_LAT_LNG)
            _address = requireArguments().getString(ARG_ADDRESS)
        }

        if (savedInstanceState != null) {
            _latLng = savedInstanceState.getParcelable(ARG_LAT_LNG)
            _address = savedInstanceState.getString(ARG_ADDRESS)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(ARG_LAT_LNG, latLng)
        outState.putString(ARG_ADDRESS, address)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        with(binding) {
            addressTextField.editText!!.setText(address)

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
            editButton.setOnClickListener { requireActivity().onBackPressed() }

            nameTextField.editText!!.doAfterTextChanged {
                binding.registerButton.isEnabled = validateRequiredFields()
            }

            bigButton.setOnCheckedChangeListener { _, _ ->
                binding.registerButton.isEnabled = validateRequiredFields()
            }

            smallButton.setOnCheckedChangeListener { _, _ ->
                binding.registerButton.isEnabled = validateRequiredFields()
            }

            registerButton.setOnClickListener {
                register()
                Toast.makeText(it.context, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.setFragmentResult("Refresh", Bundle())
                requireActivity().supportFragmentManager.popBackStack("Home", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    /**
     * 필수 입력 또는 체크를 확인하는 함수
     * @return true 전부 입력됨
     */
    private fun validateRequiredFields(): Boolean {
        return binding.nameTextField.editText!!.text.toString().isNotBlank() &&
                (binding.bigButton.isChecked || binding.smallButton.isChecked)
    }

    /**
     * 서버에 가게 정보를 등록하는 함수
     */
    private fun register() {
        val name = binding.nameTextField.editText!!.text.toString().trim()
        val type = when (binding.typeGroup.checkedRadioButtonId) {
            -1 -> Store.Type.NONE
            R.id.cafe_button -> Store.Type.CAFE
            else -> Store.Type.RESTAURANT
        }
        val size = if (binding.bigButton.isChecked && binding.smallButton.isChecked) {
            Store.Size.BOTH
        } else if (binding.bigButton.isChecked) {
            Store.Size.BIG
        } else {
            Store.Size.SMALL
        }

        viewModel.addStore(latLng, name, type, size)
    }
}