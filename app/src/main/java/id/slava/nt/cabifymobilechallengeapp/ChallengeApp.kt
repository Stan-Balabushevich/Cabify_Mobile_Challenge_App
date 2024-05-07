package id.slava.nt.cabifymobilechallengeapp

import android.app.Application
import id.slava.nt.cabifymobilechallengeapp.di.presentationModule
import id.slava.nt.cabifymobilechallengeapp.di.dataModule
import id.slava.nt.cabifymobilechallengeapp.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChallengeApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidContext(this@ChallengeApp)
            modules(
                dataModule,
                presentationModule,
                domainModule
            )
        }
    }
}