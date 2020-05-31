package com.mibaldi.fitapp.appData

import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.server.Training as ServerTraining

fun ServerTraining.toDomainTraining(tags:List<Tag>): Training =
    Training(
        id,
        name,
        date,
        circuit,
        tags
    )


