package com.gmail.maystruks08.opi_core.connector

interface OPILogger {

    fun log(message: String)

    fun logError(exception: Exception, message: String)

    fun removeOutdateLogFiles()

}