package com.mibaldi.fitapp.appData

import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.server.Training as ServerTraining

fun ServerTraining.toDomainTraining(): Training =
    Training(
        id,
        name,
        date,
        circuit
    )


