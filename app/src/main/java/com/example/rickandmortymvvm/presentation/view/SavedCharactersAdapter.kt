package com.example.rickandmortymvvm.presentation.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.core.util.setImage
import com.example.rickandmortymvvm.databinding.RmListItemBinding
import com.example.rickandmortymvvm.domain.model.RickAndMorty

class SavedCharactersAdapter(
    private val context: Context
) : RecyclerView.Adapter<SavedCharactersAdapter.ViewHolder>() {

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

    /*
    Now we need the AsyncListDiffer to
    */
    val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(private val binding: RmListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rmUser: RickAndMorty) {
            binding.apply {
                rmFullName.text = rmUser.name

                // use Glide extension function to read in image
                rmImage.setImage(rmUser.image, rmImage)
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
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = differ.currentList[position]
        holder.bind(character)
        holder.rmListItem.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.favorite_anim)
        )
    }
}