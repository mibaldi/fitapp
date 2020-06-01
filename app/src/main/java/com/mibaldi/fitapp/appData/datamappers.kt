package com.mibaldi.fitapp.appData

import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.fitapp.appData.server.Training as ServerTraining
import com.mibaldi.fitapp.appData.server.Weight as ServerWeight

fun ServerTraining.toDomainTraining(tags:List<Tag>): Training =
    Training(
        id,
        name,
        date,
        circuit,
        tags
    )
fun ServerWeight.toDomainWeight(): Weight =
    Weight(date,weight)

