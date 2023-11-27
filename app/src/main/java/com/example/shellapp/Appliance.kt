package com.example.shellapp

import java.io.Serializable
data class Appliance(
    val applianceType: String,
    val serialPortNum: String,
    val applianceName: String
) : Serializable

