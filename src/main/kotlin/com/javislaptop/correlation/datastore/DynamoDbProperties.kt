package com.javislaptop.correlation.datastore

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@ConstructorBinding
@ConfigurationProperties(prefix = "aws.eod.dynamodb")
data class DynamoDbProperties(val table : String, val optionsTable : String)
