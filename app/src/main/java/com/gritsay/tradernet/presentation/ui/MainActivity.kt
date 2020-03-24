package com.gritsay.tradernet.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gritsay.tradernet.BuildConfig
import com.gritsay.tradernet.R
import com.gritsay.tradernet.constants.Tickers
import com.gritsay.tradernet.data.model.Rate
import com.gritsay.tradernet.presentation.adapters.TraderAdapter
import com.gritsay.tradernet.presentation.presenter.MainActivityPresenter
import kotlinx.android.synthetic.main.activity_tradernet.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), MainActivityView {

    private var presenter: MainActivityPresenter = MainActivityPresenter(this)
    private var stocks: MutableList<Rate> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tradernet)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        val tickers: MutableList<String> = mutableListOf()

        enumValues<Tickers>().joinToString {
            tickers.add(it.String).toString()
        }
        val adapter = TraderAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        presenter.start(tickers)
    }


    override fun updateData(stocks: List <Rate>) {
        runOnUiThread {
            this.stocks.clear()
            this.stocks.addAll(stocks)
            (recyclerView.adapter as TraderAdapter).updateAdapterData(stocks)
            recyclerView.adapter?.notifyDataSetChanged() //TODO diff util
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.stop()
    }
}
