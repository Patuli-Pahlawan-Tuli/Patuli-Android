package com.puxxbu.PatuliApp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.puxxbu.PatuliApp.data.api.config.ApiConfig
import com.puxxbu.PatuliApp.data.database.SessionDataPreferences
import com.puxxbu.PatuliApp.data.repository.DataRepository
import com.puxxbu.PatuliApp.ui.fragments.home.HomeViewModel
import com.puxxbu.PatuliApp.ui.fragments.lesson.viewmodel.LessonViewModel
import com.puxxbu.PatuliApp.ui.fragments.profile.ProfileViewModel
import com.puxxbu.PatuliApp.ui.fragments.quiz.QuizViewModel
import com.puxxbu.PatuliApp.ui.main.MainViewModel
import com.puxxbu.PatuliApp.ui.login.LoginViewModel
import com.puxxbu.PatuliApp.ui.register.RegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PatuliApp : Application() {
    private val Context.data: DataStore<Preferences> by preferencesDataStore("token")


    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        startKoin {
            androidLogger()
            androidContext(this@PatuliApp)
            modules(repoModule, viewModelModule)
        }

    }

    private val viewModelModule = module {
        viewModel { RegisterViewModel(get())  }
        viewModel { LoginViewModel(get()) }
        viewModel { MainViewModel(get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { LessonViewModel(get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { QuizViewModel(get()) }
    }

    private val repoModule = module {
        single { ApiConfig.getApiService(context) }
        single { SessionDataPreferences.getInstance(context.data) }
        single { DataRepository(get(), get()) }
    }

    companion object {
        lateinit var context: Context
    }
}