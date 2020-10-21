package com.hssoft.smartcheckout.opi_core.terminal.entity

enum class Result {
    Success,
    Failure,
    PartialFailure,
    DeviceUnavailable,
    Aborted,
    TimedOut,
    FormatError,
    ParsingError,
    ValidationError,
    MissingMandatoryData,
    Busy
}