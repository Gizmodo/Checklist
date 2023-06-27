package ru.dl.checklist.app.di.component

import dagger.Component
import ru.dl.checklist.app.app.App
import ru.dl.checklist.app.di.module.DataBaseModule
import ru.dl.checklist.app.di.module.DispatcherModule
import ru.dl.checklist.app.di.module.NetworkModule
import ru.dl.checklist.app.presenter.auth.AuthViewModel
import ru.dl.checklist.app.presenter.detail.DetailViewModel
import ru.dl.checklist.app.presenter.house.check.HouseCheckViewModel
import ru.dl.checklist.app.presenter.house.main.HouseViewModel
import ru.dl.checklist.app.presenter.main.MainViewModel
import ru.dl.checklist.app.presenter.mark.MarksListViewModel
import ru.dl.checklist.app.presenter.objects.ObjectsViewModel
import ru.dl.checklist.app.presenter.template.TemplateViewModel
import ru.dl.checklist.app.presenter.zone.ZonesListViewModel
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        DataBaseModule::class,
        DispatcherModule::class,
        NetworkModule::class,
    ]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(vm: MainViewModel)
    fun inject(vm: ZonesListViewModel)
    fun inject(vm: MarksListViewModel)
    fun inject(vm: TemplateViewModel)
    fun inject(vm: ObjectsViewModel)
    fun inject(vm: AuthViewModel)
    fun inject(vm: HouseViewModel)
    fun inject(vm: HouseCheckViewModel)
    fun inject(vm: DetailViewModel)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun databaseModule(dataBaseModule: DataBaseModule): Builder
        fun networkModule(networkModule: NetworkModule): Builder
    }
}
