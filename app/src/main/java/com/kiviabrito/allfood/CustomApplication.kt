package com.kiviabrito.allfood

import android.app.Application
import androidx.room.Room
import com.kiviabrito.allfood.data.api.RetrofitFactory
import com.kiviabrito.allfood.data.api.RetrofitFactoryImpl
import com.kiviabrito.allfood.data.local.AppDatabase
import com.kiviabrito.allfood.data.repository.PlacesRepository
import com.kiviabrito.allfood.data.repository.PlacesRepositoryImpl
import com.kiviabrito.allfood.ui.CustomFragmentFactory
import com.kiviabrito.allfood.ui.map.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CustomApplication)
            modules(
                module {
                    single(named(CustomFragmentFactory::class.simpleName.orEmpty())) { CustomFragmentFactory()  }
                    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }
                    single<RetrofitFactory> { RetrofitFactoryImpl() }
                    viewModel { MapViewModel(get()) }
                    single { Room.databaseBuilder(get(), AppDatabase::class.java, AppDatabase.NAME).build() }
                    single { get<AppDatabase>().favoritePlaceDao() }
                }
            )
        }
    }

}