package com.mutlak.metis.wordmem.injection.component

import com.mutlak.metis.wordmem.injection.PerFragment
import com.mutlak.metis.wordmem.injection.module.FragmentModule
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent