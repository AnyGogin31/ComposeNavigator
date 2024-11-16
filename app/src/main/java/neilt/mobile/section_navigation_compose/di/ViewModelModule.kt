package neilt.mobile.section_navigation_compose.di

import neilt.mobile.section_navigation_compose.ui.screens.root.RootViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::RootViewModel)
}
