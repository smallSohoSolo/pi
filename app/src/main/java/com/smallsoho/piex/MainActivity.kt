package com.smallsoho.piex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.smallsoho.piex.databinding.ActivityMainBinding
import com.smallsoho.piex.vm.NumViewModel

class MainActivity : AppCompatActivity() {

    private val numViewModel: NumViewModel by lazy {
        ViewModelProviders.of(this).get(NumViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.numViewModel = numViewModel
    }

}
