package com.puxxbu.PatuliApp.ui.fragments.lesson.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puxxbu.PatuliApp.PatuliApp.Companion.context
import com.puxxbu.PatuliApp.R
import com.puxxbu.PatuliApp.data.model.LessonItemModel
import com.puxxbu.PatuliApp.databinding.ItemLessonBinding
import com.puxxbu.PatuliApp.ui.fragments.lesson.LessonListActivity
import android.graphics.Bitmap
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.puxxbu.PatuliApp.ui.fragments.lesson.DetailLessonActivity

class LessonAdapter (private val items: List<LessonItemModel>) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding : ItemLessonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LessonItemModel) {

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
           binding.apply {
               Glide.with(itemView.context)
                   .load(item.image_url)
                   .placeholder(circularProgressDrawable)
                   .fitCenter()
                   .diskCacheStrategy(DiskCacheStrategy.DATA)
                   .into(ivLessonPhoto)
               tvLessonTitle.text = item.title
               tvLessonDesc.text = item.description
               tvTimeNeeded.text = context.getString(R.string.time_needed, item.hour_needed.toString())
           }

            binding.cardLesson.setOnClickListener{
                val intent = Intent(itemView.context, LessonListActivity::class.java)
                intent.putExtra(LessonListActivity.EXTRA_LESSON_NAME, item.title)
                itemView.context.startActivity(intent)
            }

        }
    }
}