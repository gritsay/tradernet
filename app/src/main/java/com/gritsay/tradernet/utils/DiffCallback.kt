package com.gritsay.tradernet.utils

import androidx.recyclerview.widget.DiffUtil
import com.gritsay.tradernet.data.model.Rate

class DiffCallback(
    private val oldList: List<Rate>,
    private val newList: List<Rate>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].code == newList[newItemPosition].code
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].code.equals(newList[newItemPosition].code) &&
                oldList[oldItemPosition].percentageChangeRelative == newList[newItemPosition].percentageChangeRelative &&
                oldList[oldItemPosition].exchangeOfTheLatestTrade.equals(newList[newItemPosition].exchangeOfTheLatestTrade) &&
                oldList[oldItemPosition].name.equals(newList[newItemPosition].name) &&
                oldList[oldItemPosition].changeInThePriceOfTheLastTradeInPoints == oldList[newItemPosition].changeInThePriceOfTheLastTradeInPoints

    }
}