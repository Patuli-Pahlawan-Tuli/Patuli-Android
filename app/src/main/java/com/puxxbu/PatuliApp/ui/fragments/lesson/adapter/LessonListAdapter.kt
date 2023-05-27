package com.puxxbu.PatuliApp.ui.fragments.lesson.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.DetailLessonItemModel
import com.puxxbu.PatuliApp.databinding.ItemDetailLessonBinding

class LessonListAdapter(private val items: List<DetailLessonItemModel>) :
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

        fun bind(item: DetailLessonItemModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(item.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivLessonPhoto)
                tvTitle.text = item.title
                tvDifficulty.text = item.difficulty
            }

            binding.card.setOnClickListener {
//                val intent: Intent = Intent(itemView.context, LessonListActivity::class.java)
//                intent.putExtra("lesson_id", item.title)
//                itemView.context.startActivity(intent)
            }
        }
    }
}