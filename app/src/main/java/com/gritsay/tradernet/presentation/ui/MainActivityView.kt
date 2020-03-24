package com.gritsay.tradernet.presentation.ui

import com.gritsay.tradernet.data.model.Rate

interface MainActivityView {

    fun updateData(stocks: List<Rate>)

}