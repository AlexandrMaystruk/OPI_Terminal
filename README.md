 <H1>Android Open Payment Initiative library</H1>

This Kotlin library for connecting EFT terminals based on OPI (Open Payments Initiative. This is a communication protocol between the EPOS application and any solution for cashless payments solution installed on the EFT/PoS terminal.)

 <H2>Usage</H2>
 
 Create terminal instance:
            
          private val externalTerminal = Terminal.Builder()
            .inputPort(inputPort)
            .outputPort(outputPort)
            .ipAddress(ipAddress)
            .applicationSender("yourAppName")
            .workstationID(deviceName)
            .timeout(40000)
            .logger(opiLogger)
            .build()
 
  Login:
            
        fun login(): ServiceResponse {
        val serviceRequest = ServiceRequest(
                requestType = RequestType.Login.name,
                workstationID = workstationID,
                requestID = "0",
                applicationSender = applicationSender,
                posData = ServiceRequest.PosData(posTimeStamp = Date())
        )
        return callWithShutdown(serviceRequest)
    }
 
 
 Make transaction::
            
       fun transaction(paymentData: Payment): CardResponse {
           val cardServiceRequest = CardRequest(
                requestID = paymentData.transactionId,
                workstationID = workstationID,
                requestType = RequestType.CardPayment.name,
                posData = CardRequest.PosData(
                        posTimeStamp = Date().toServerUTCFormat(),
                        usePreselectedCard = false
                ),
                privateData = CardRequest.PrivateData(
                        lastReceiptNumber = paymentData.lastReceiptNumber.toString()
                ),
                totalAmount = CardRequest.TotalAmount(
                        currency = paymentData.currency,
                        paymentAmount = paymentData.total.toString()
                )
              )
            return callTransactionWithShutdown(cardServiceRequest)
        }
           
 
 Close EFT day:
            
         fun reconciliationWithClosure(): ServiceResponse {
             val serviceRequest = ServiceRequest(
                        requestType = RequestType.ReconciliationWithClosure.name,
                        workstationID = workstationID,
                        requestID = "0",
                        applicationSender = applicationSender,
                        posData = ServiceRequest.PosData(posTimeStamp = Date())
                      )
             return callTransactionWithShutdown(serviceRequest)
        }
            

<H2>Technical solution</H2>

The O.P.I. interface implementation does not depend on a specific operating system. It is an XML-based interface. Communication takes place via TCP/IP. The XML messages are exchanged over two sockets that are referred to as channels (channel 0 and channel 1). The original OPI/IFSF specification defines three message pairs:

Card Request/Response (channel 0)
Service Request/Response (channel 0)
Device Request/Response (channel 1)

Using the O.P.I. interface gives a payment solution access to the PoS peripherals, e.g. to a PoS printer to print out receipts, a display to output messages to the cashier or cardholder, or a magnetic card reader. Decoupling the interface in this way increases its flexibility for integration in international, solution and industry-specific scenarios for users as well as for PoS and payment solution providers, and therefore also protects their investments.

<H2>Communication schema with timeouts</H2>


![GitHub Logo](Open%20Payment%20Initiative%20timeout.png)
