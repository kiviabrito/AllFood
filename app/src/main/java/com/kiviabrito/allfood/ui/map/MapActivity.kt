package com.kiviabrito.allfood.ui.map

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.Toast
import com.kiviabrito.allfood.databinding.ActivityMainBinding
import com.kiviabrito.allfood.ui.CustomFragmentFactory
import com.kiviabrito.allfood.ui.CustomFragmentFactory.MainFragmentEnum
import com.kiviabrito.allfood.utils.BaseActivity
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.setIsVisible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class MapActivity : BaseActivity() {

    private val customFragmentFactory: CustomFragmentFactory by inject(named(CustomFragmentFactory.NAME))
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFirstFragment(savedInstanceState)
        setObserver()
        setupActionBar()
    }

    override fun setFragmentFactory() {
        supportFragmentManager.fragmentFactory = customFragmentFactory
    }

    private fun setupActionBar() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearch(s)
            }
        })
    }

    private fun loadFirstFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            loadFragment(MainFragmentEnum.MAP)
        }
    }

    private fun setObserver() {
        viewModel.loading.observe(this, {
            it.get()?.let { shouldShowLoading ->
                binding.gpProgressBar.setIsVisible(shouldShowLoading)
                if (shouldShowLoading) {
                    binding.inputSearch.text = SpannableStringBuilder("")
                }
            }
        })
        viewModel.error.observe(this, {
            it.get()?.let { errorMsg ->
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }

}