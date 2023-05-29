package com.puxxbu.PatuliApp.ui.fragments.lesson.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.api.response.lesson.DataItem
import com.puxxbu.PatuliApp.databinding.ItemDetailLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.DetailLessonActivity

class LessonListAdapter(private val items: List<DataItem>) :
    RecyclerView.Adapter<LessonListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDetailLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemDetailLessonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .fitCenter()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivLessonPhoto)
                tvTitle.text = item.lessonName
                tvDifficulty.text = item.lessonType
            }

            binding.card.setOnClickListener {
                val intent: Intent = Intent(itemView.context, DetailLessonActivity::class.java)
                intent.putExtra(DetailLessonActivity.EXTRA_TYPE, item.lessonType)
                intent.putExtra(DetailLessonActivity.EXTRA_NUMBER, item.lessonNumber)
                intent.putExtra(DetailLessonActivity.EXTRA_SIZE, items.size)
                itemView.context.startActivity(intent)
            }
        }
    }
}