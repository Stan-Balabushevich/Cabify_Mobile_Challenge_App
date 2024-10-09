package id.slava.nt.cabifymobilechallengeapp.di

import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.ProductListViewModel
import id.slava.nt.cabifymobilechallengeapp.presentation.resource.AndroidResourceProvider
import id.slava.nt.cabifymobilechallengeapp.presentation.resource.ResourceProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    single<ResourceProvider> {
        AndroidResourceProvider(get())
    }

//    viewModel {
//        ProductListViewModel(getProductsUseCase = get(), getDiscountRulesUseCase = get(), resourceProvider = get())
//    }

    viewModelOf(::ProductListViewModel)

}