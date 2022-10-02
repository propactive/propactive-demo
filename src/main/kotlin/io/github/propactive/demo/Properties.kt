package io.github.propactive.demo

import io.github.propactive.demo.type.PORT_NUMBER
import io.github.propactive.demo.type.PRIME
import io.github.propactive.environment.Environment
import io.github.propactive.property.Property
import io.github.propactive.type.INTEGER
import io.github.propactive.type.URL
import io.github.propactive.type.UUID

@Environment(
    [
        "prod:       application.properties",
        "stage/test: *-application.properties",
        "dev:        localhost-application.properties"
    ]
)
object Properties {
    @Property(
        [
            "prod: ABC"
        ]
    )
    const val prodOnlyStringPropertyKey = "propactive.demo.string.key"

    @Property(
        value = [
            "prod:       433",
            "stage/test: 80",
            "dev:        8080"
        ],
        type = PORT_NUMBER::class
    )
    const val portPropertyKey = "propactive.demo.port.key"

    @Property(
        value = [
            "prod:       https://www.prodland.com",
            "stage/test: http://www.nonprodland.com",
            "dev:        http://127.0.0.1/"
        ],
        type = URL::class
    )
    const val urlPropertyKey = "propactive.demo.url.key"

    @Property(
        value = [
            "prod:       3000",
            "stage/test: 10000",
            "dev:        30000"
        ],
        type = INTEGER::class
    )
    const val timoutInMsPropertyKey = "propactive.demo.timout-in-ms.key"

    @Property(
        value = [
            "prod:           1ed0a470-bb84-11ec-ae36-00163e9b33ca",
            "stage/test/dev: 00000000-0000-0000-0000-000000000000"
        ],
        type = UUID::class
    )
    const val uuidPropertyKey = "propactive.demo.uuid.key"

    @Property(
        value = [
            "prod/stage/test/dev: 7"
        ],
        type = PRIME::class
    )
    const val primePropertyKey = "propactive.demo.prime.key"
}
