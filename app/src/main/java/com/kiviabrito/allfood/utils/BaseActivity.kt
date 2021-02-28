package com.kiviabrito.allfood.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kiviabrito.allfood.R
import com.kiviabrito.allfood.ui.CustomFragmentFactory.MainFragmentEnum

open class BaseActivity : AppCompatActivity() {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        setFragmentFactory()
        return super.onCreateView(name, context, attrs)
    }

    open fun setFragmentFactory() {}

    fun loadFragment(fragmentEnum: MainFragmentEnum) {
        val fragmentInst = supportFragmentManager
            .fragmentFactory
            .instantiate(classLoader, fragmentEnum.name)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragmentInst)
            .addToBackStack(fragmentEnum.name)
            .commit()
    }

    fun goBackToFragment(fragmentEnum: MainFragmentEnum) {
        supportFragmentManager.popBackStack(fragmentEnum.name, 0)
    }

    private fun onBack() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        onBack()
    }

}