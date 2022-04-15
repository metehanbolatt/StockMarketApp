package com.metehanbolat.stockmarketapp.presentation.company_info

import com.metehanbolat.stockmarketapp.domain.model.CompanyInfo
import com.metehanbolat.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
