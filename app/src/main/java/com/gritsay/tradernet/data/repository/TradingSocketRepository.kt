package com.gritsay.tradernet.data.repository

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.Socket.*
import com.gritsay.tradernet.constants.Constants
import com.gritsay.tradernet.data.model.ExpectedException
import com.gritsay.tradernet.data.model.Rate
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException


class TradingSocketRepository(private val url: String) {
    private lateinit var mSocket: Socket
    private val stocks: MutableMap<String, Rate> = mutableMapOf()

    fun unsubscribeOnUpdate() {
        mSocket.off("q")
        mSocket.off(EVENT_CONNECT)
        mSocket.off(EVENT_DISCONNECT)
        mSocket.off(EVENT_CONNECT_ERROR)
        mSocket.off(EVENT_CONNECT_TIMEOUT)
        if (mSocket.connected()) {
            mSocket.disconnect()
            Timber.d("sockets disconnected")
        }
    }

    fun subscribeOnUpdate(
        tickers: MutableList<String>,
        block: (result: MutableMap<String, Rate>) -> Unit
    ) {
        try {
            mSocket = IO.socket(url)
        } catch (e: URISyntaxException) {
            Timber.d("socket err ${e.message}")
            throw ExpectedException("mSocket not connected ${e.message}")
        }

        mSocket.on(EVENT_DISCONNECT) { Timber.d("EVENT_DISCONNECT") }
        mSocket.on(EVENT_CONNECT_ERROR) { Timber.d("EVENT_CONNECT_ERROR") }
        mSocket.on(EVENT_CONNECT_TIMEOUT) { Timber.d("EVENT_CONNECT_TIMEOUT") }
        mSocket.on(EVENT_CONNECT) {
            val poisonArray = JSONArray()

            tickers.forEach {
                Timber.d("ticker for each $it")
                poisonArray.put(it)
            }

            Timber.d("socket connected")
            mSocket.emit("sup_updateSecurities2", poisonArray)
            Timber.d("subscribeOnUpdate sup_updateSecurities2")
        }
        mSocket.on(Constants.topicChannel) {
            it.forEach { msg ->
                if (msg is JSONObject) {
                    val sMsg = msg.getJSONArray(Constants.topicChannel).toString()
                    Timber.d("sMsg = $sMsg")
                    try {
                        val nextUpdate = Json.nonstrict.parse(Rate.serializer().list, sMsg)
                        Timber.d("nextUpdate = $nextUpdate")
                        nextUpdate.forEach { q ->
                            if (stocks.containsKey(q.code)) {
                                q.changeInThePriceOfTheLastTradeInPoints?.let {value->
                                    stocks[q.code]?.changeInThePriceOfTheLastTradeInPoints =
                                       value
                                }
                                q.exchangeOfTheLatestTrade?.let {value->
                                    stocks[q.code]?.exchangeOfTheLatestTrade =
                                        value
                                }
                                q.percentageChangeRelative?.let {value->
                                    stocks[q.code]?.percentageChangeRelative =
                                        value
                                }
                                q.lastTradePrice?.let {value->
                                    stocks[q.code]?.lastTradePrice = value
                                }
                                q.name?.let {value->
                                    stocks[q.code]?.name = value
                                }
                                q.changeInThePriceOfTheLastTradeInPoints?.let {value->
                                    stocks[q.code]?.changeInThePriceOfTheLastTradeInPoints =
                                        value
                                }
                                q.lastTradePrice?.let {value->
                                    stocks[q.code]?.previouslyPrice =
                                        stocks[q.code]?.lastTradePrice
                                    stocks[q.code]?.lastTradePrice = value
                                }

                            } else {
                                stocks[q.code] = q
                            }
                        }
                        block(stocks)
                    } catch (e: MissingFieldException) {
                        Timber.d(e.message.toString())
                    }
                }
            }
        }
        mSocket.connect()
    }
}