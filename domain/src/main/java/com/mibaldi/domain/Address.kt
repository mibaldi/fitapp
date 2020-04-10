package com.mibaldi.domain


data class Address(var street: String?= "",
                   var locality: String?= "",
                   var postalCode: String?="",
                   var region: String?="",
                   var country: String?="",
                   var streetNumber: String?="")
