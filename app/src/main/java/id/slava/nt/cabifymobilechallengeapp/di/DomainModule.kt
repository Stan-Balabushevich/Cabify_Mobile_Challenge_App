package id.slava.nt.cabifymobilechallengeapp.di

import id.slava.nt.cabifymobilechallengeapp.domain.usecase.CalculateTotalWithDiscountUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.CountSpecificItemsUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetDiscountRulesUseCase
import id.slava.nt.cabifymobilechallengeapp.domain.usecase.GetProductsUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        GetProductsUseCase(repository = get())
    }
    factory {
        GetDiscountRulesUseCase(repository = get())
    }

    factory {
        CalculateTotalWithDiscountUseCase()
    }

    factory {
        CountSpecificItemsUseCase()
    }

}