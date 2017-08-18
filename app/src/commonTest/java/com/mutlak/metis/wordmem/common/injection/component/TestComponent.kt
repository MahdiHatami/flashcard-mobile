package com.mutlak.metis.wordmem.common.injection.component

import com.mutlak.metis.wordmem.common.injection.module.ApplicationTestModule
import com.mutlak.metis.wordmem.injection.component.ApplicationComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationTestModule::class))
interface TestComponent : ApplicationComponent