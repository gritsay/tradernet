package com.gritsay.tradernet.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gritsay.tradernet.R
import com.gritsay.tradernet.constants.Constants
import com.gritsay.tradernet.data.model.Rate
import com.gritsay.tradernet.utils.DiffCallback
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tradernet_item.view.*
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

class TraderAdapter : RecyclerView.Adapter<TraderAdapter.ViewHolder>() {

    private var decimalFormat = DecimalFormat("0.00")
    private var items: MutableList<Rate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tradernet_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.containerView.code.text = items[position].code

        items[position].changeInThePriceOfTheLastTradeInPoints?.let { value ->
            when {
                value > 0f -> {
                    holder.containerView.delta_details.text =
                        "${items[position].lastTradePrice}  ( + ${items[position].changeInThePriceOfTheLastTradeInPoints}%)"
                }
                value < 0f -> {
                    holder.containerView.delta_details.text =
                        "${items[position].lastTradePrice}  (  ${items[position].changeInThePriceOfTheLastTradeInPoints}%)"
                }

            }
        }

        holder.containerView.names.text =
            "${items[position].exchangeOfTheLatestTrade} | ${items[position].name}"


        val percentValue = items[position].percentageChangeRelative
        percentValue?.let { value ->
            when {
                value < 0f -> {
                    holder.containerView.delta.setTextColor(Color.RED)
                    holder.containerView.delta.text = "${decimalFormat.format(percentValue)}%"
                }
                value > 0f -> {
                    holder.containerView.delta.setTextColor(Color.GREEN)
                    holder.containerView.delta.text = "+${decimalFormat.format(percentValue)}%"
                }
                value == 0f -> {
                    holder.containerView.delta.setTextColor(Color.GRAY)
                    holder.containerView.delta.text = "${decimalFormat.format(percentValue)}%"
                }
            }


            val previouslyPrice = items[position].previouslyPrice
            val currentPrice = items[position].lastTradePrice

            Timber.d("previously price = $previouslyPrice")
            Timber.d("current price = $currentPrice")

            previouslyPrice?.let { prev ->
                currentPrice?.let { curr ->
                    when {
                        prev > curr -> {
                            setLabelColor(
                                holder.containerView.context,
                                R.color.red
                            )
                        }
                        prev < curr -> {
                            setLabelColor(
                                holder.containerView.context,
                                R.color.green
                            )
                        }
                    }
                    items[position].previouslyPrice = items[position].lastTradePrice
                }
            }

            if (items[position].code.isNotEmpty()) {
                val logoUrl =
                    "${Constants.logoUrl}${items[position].code.toLowerCase(Locale.getDefault())}"
                Timber.d("logo url = $logoUrl")
                Picasso
                    .get()
                    .load(logoUrl)
                    .into(holder.containerView.companyLogo)
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer

    private fun setLabelColor(context: Context, color: Int) {
        val unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.drawable_corner)
        val wrappedDrawable = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
        wrappedDrawable?.let { DrawableCompat.setTint(it, ContextCompat.getColor(context, color)) }
    }

    fun diff(rates: List<Rate>) {
        val diffCallback = DiffCallback(this.items, rates)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(rates)
        diffResult.dispatchUpdatesTo(this)
    }
}