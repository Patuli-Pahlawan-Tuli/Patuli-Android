package com.puxxbu.PatuliApp.ui.fragments.lesson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.puxxbu.PatuliApp.databinding.ActivityDetailLessonBinding

class DetailLessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLessonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}