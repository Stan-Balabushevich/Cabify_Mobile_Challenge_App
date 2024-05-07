package id.slava.nt.cabifymobilechallengeapp.di

import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        GetProductsUseCase(repository = get())
    }

}