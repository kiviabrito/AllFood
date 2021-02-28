package com.kiviabrito.allfood.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.kiviabrito.allfood.ui.map.fragments.ListFragment
import com.kiviabrito.allfood.ui.map.fragments.MapFragment

class CustomFragmentFactory : FragmentFactory() {

    companion object {
        const val NAME = "CustomFragmentFactory"
    }

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MainFragmentEnum.MAP.name -> MapFragment()
            MainFragmentEnum.LIST.name -> ListFragment()
            else -> super.instantiate(classLoader, className)
        }
    }

    enum class MainFragmentEnum {
        MAP,
        LIST;
    }
}