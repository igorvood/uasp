package ru.vtb.uasp.common.kafka

import ru.vtb.uasp.common.abstraction.AbstractMaskedSerializeService

case class MaskProducerDTO[IN, OUT](flinkSinkProperties: FlinkSinkProperties,
                                    serialize: AbstractMaskedSerializeService[IN, OUT]
                                   )
