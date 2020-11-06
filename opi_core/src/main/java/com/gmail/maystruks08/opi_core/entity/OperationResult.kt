package com.gmail.maystruks08.opi_core.entity

enum class OperationResult {
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
    Loggedout,
    Busy
}