package com.gritsay.tradernet.presentation.presenter

import com.gritsay.tradernet.constants.Constants
import com.gritsay.tradernet.data.repository.TradingSocketRepository
import com.gritsay.tradernet.presentation.ui.MainActivity
import timber.log.Timber


class MainActivityPresenter(private val view: MainActivity) {

    private val trading = TradingSocketRepository(Constants.tradingUrl)

    fun start(tickers: MutableList<String>) {
        trading.subscribeOnUpdate(tickers = tickers) { stocks ->
            stocks.values.toList().let {
                it.forEach { rate ->
                    Timber.d(rate.toString())
                }
                view.updateData(it)
            }
        }
    }

    fun stop() {
        trading.unsubscribeOnUpdate()
    }


}