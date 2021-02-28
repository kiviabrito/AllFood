package com.kiviabrito.allfood.utils

import androidx.fragment.app.Fragment
import com.kiviabrito.allfood.R
import com.kiviabrito.allfood.ui.CustomFragmentFactory.MainFragmentEnum

open class BaseFragment : Fragment() {

    fun loadFragment(fragmentEnum: MainFragmentEnum) {
        val fragmentInst = requireActivity().supportFragmentManager
            .fragmentFactory
            .instantiate(requireActivity().classLoader, fragmentEnum.name)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragmentInst)
            .addToBackStack(fragmentEnum.name)
            .commit()
    }

}