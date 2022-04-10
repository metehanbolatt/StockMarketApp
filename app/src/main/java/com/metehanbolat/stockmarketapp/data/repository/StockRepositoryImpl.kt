package com.metehanbolat.stockmarketapp.data.repository

import com.metehanbolat.stockmarketapp.data.local.StockDatabase
import com.metehanbolat.stockmarketapp.data.mapper.toCompanyListing
import com.metehanbolat.stockmarketapp.data.remote.StockApi
import com.metehanbolat.stockmarketapp.domain.model.CompanyListing
import com.metehanbolat.stockmarketapp.domain.repository.StockRepository
import com.metehanbolat.stockmarketapp.util.Resource
import com.opencsv.CSVReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> = flow {

        emit(Resource.Loading(true))
        val localListings = dao.searchCompanyListing(query = query)
        emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()
        val shouldJustFromCache = !isDbEmpty && !fetchFromRemote
        if (shouldJustFromCache) {
            emit(Resource.Loading(false))
            return@flow
        }
        val remoteListings = try {
            val response = api.getListings()
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't load data!"))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Resource.Error("Couldn't load data!"))
        }
    }
}