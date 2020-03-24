package com.gritsay.tradernet.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property c изменение в процентах относительно цены предыдущей торговой сессии
 * @property pcp изменение в процентах относительно цены предыдущей торговой сессии
 * @property ltr биржа последней сделки
 * @property name название бумаги
 * @property ltp цена последней сделки
 * @property chg изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии
 */
@Serializable
data class Rate(
    var previouslyPrice: Float? = null,
    @SerialName("c")  @Optional var code: String,
    @SerialName("pcp") @Optional var percentageChangeRelative: Float? = null,
    @SerialName("ltr") @Optional var exchangeOfTheLatestTrade: String? = null,
    @SerialName("name") @Optional var name: String? = null,
    @SerialName("ltp") @Optional var lastTradePrice: Float? = null,
    @SerialName("chg") @Optional var changeInThePriceOfTheLastTradeInPoints: Float? = null
)

open class ExpectedException(message: String) : Exception(message)