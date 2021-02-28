package com.kiviabrito.allfood.ui.map.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kiviabrito.allfood.R
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.databinding.ItemRestaurantBinding
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.loadImage
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.orZero
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.removeAccent
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.setDrawable
import java.util.*

class RestaurantAdapter(
    private var items: List<PlaceDTO>,
    private val onItemClickListener: (PlaceDTO) -> Unit,
    private val onFavoriteClickListener: (PlaceDTO) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>(), Filterable {

    var filterItems = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filterItems.size
    }

    fun setItemsAdapter(newList: List<PlaceDTO>) {
        val oldList = filterItems
        val diffCallback = PlaceDiffCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        filterItems = newList
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        filterItems.getOrNull(position)?.let { item ->
            holder.bindView(item)
        }
    }

    inner class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: PlaceDTO) {
            binding.apply {
                image.loadImage(item.imageURL)
                name.text = item.name
                rating.rating = item.rating?.toFloat().orZero()
                totalRating.text = root.context.getString(R.string.total_rating_format, item.ratingTotal.orZero())
                priceLevel.text = item.getPriceLevelAndOpenHour()
                distance.text = root.context.getString(R.string.distance_format, item.getDistanceToUserStringKm())
                favorite.setDrawable(if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_no)
                favorite.setOnClickListener {
                    onFavoriteClickListener(item)
                }
                root.setOnClickListener {
                    onItemClickListener(item)
                }
            }
        }
    }

    class PlaceDiffCallback(
        private val oldList: List<PlaceDTO>,
        private val newList: List<PlaceDTO>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].equals(newList[newItemPosition])
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filterItems = (filterResults.values as? List<*>)?.filterIsInstance<PlaceDTO>() ?: listOf()
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val keyWords = charSequence?.toString()?.toLowerCase(Locale.getDefault())?.removeAccent()
                val filterResults = FilterResults()
                val suggestions = if (keyWords.isNullOrEmpty())
                    items
                else {
                    items.filter {
                        it.name.toLowerCase(Locale.getDefault()).removeAccent().contains(keyWords)
                    }
                }
                filterResults.values = suggestions
                filterResults.count = suggestions.count()
                return filterResults
            }
        }
    }
}
