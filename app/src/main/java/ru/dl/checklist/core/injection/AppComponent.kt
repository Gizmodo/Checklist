package ru.dl.checklist.core.injection

import dagger.Component
import ru.dl.checklist.App
import ru.dl.checklist.FirstFragment
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        DataBaseModule::class,
        DispatcherModule::class,
    ]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(fragment: FirstFragment)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun databaseModule(dataBaseModule: DataBaseModule): Builder
    }
}
