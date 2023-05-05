package com.example.rickandmortymvvm.presentation.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.databinding.RmListItemBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty

class RickAndMortyAdapter(
    private val context: Context
) : PagingDataAdapter<RickAndMorty, RickAndMortyAdapter.ViewHolder>(diffCallback) {

    companion object {

        private val diffCallback = object
            : DiffUtil.ItemCallback<RickAndMorty>() {

            override fun areItemsTheSame(
                oldItem: RickAndMorty,
                newItem: RickAndMorty
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: RickAndMorty,
                newItem: RickAndMorty
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: RmListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rmUser: RickAndMorty) {
            binding.apply {
                rmFullName.text = rmUser.name

                // scale and transform image to our needs using Glide.
                Glide.with(rmImage.context)
                    .load(rmUser.image)
                    .placeholder(R.drawable.baseline_person_24)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(rmImage)

                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(rmUser)
                    }
                }
            }
        }

        val rmListItem = binding.rmListItem
    }

    /*
    Item click listener variable.
    */
    private var onItemClickListener: ((RickAndMorty) -> Unit)? = null

    /*
    Setter method for the onItemClickListener.
    */
    fun setOnItemClickListener(listener: ((RickAndMorty) -> Unit)?) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RmListItemBinding
            .inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RickAndMortyAdapter.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
        holder.rmListItem.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.favorite_anim)
        )
    }
    /**
     * MUST REMOVE OVERRIDE getItemCount() METHOD FOR PAGING3 TO WORK!!
     */
}