package com.puxxbu.PatuliApp.ui.fragments.lesson.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.api.response.lesson.DataItem
import com.puxxbu.PatuliApp.data.model.LessonItemNavModel
import com.puxxbu.PatuliApp.databinding.ItemDetailLessonBinding
import com.puxxbu.PatuliApp.databinding.ItemLessonNavigationBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.DetailLessonActivity

class LessonNavAdapter(private val items: List<LessonItemNavModel>) :
    RecyclerView.Adapter<LessonNavAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLessonNavigationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemLessonNavigationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LessonItemNavModel) {
            binding.apply {
                tvTitle.text = item.title
            }

            binding.cardLessonNav.setOnClickListener {
                val intent: Intent = Intent(itemView.context, DetailLessonActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }
    }

}