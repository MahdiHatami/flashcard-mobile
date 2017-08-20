package com.mutlak.metis.wordmem.runner

import com.mutlak.metis.wordmem.MutlakApplication
import android.app.Application
import android.content.Context
import io.appflate.restmock.android.RESTMockTestRunner

/**
 * Created by ravindra on 4/2/17.
 */
class TestRunner : RESTMockTestRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MutlakApplication::class.java.name, context)
    }

}
