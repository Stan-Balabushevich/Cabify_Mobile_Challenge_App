package id.slava.nt.cabifymobilechallengeapp.di

import id.slava.nt.cabifymobilechallengeapp.presentation.products_list.ProductListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel {
        ProductListViewModel(getProductsUseCase = get(), getDiscountRulesUseCase = get())
    }

}