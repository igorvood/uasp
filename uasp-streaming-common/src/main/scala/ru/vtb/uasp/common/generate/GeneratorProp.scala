package ru.vtb.uasp.common.generate

import ru.vtb.uasp.common.generate.dto.{CreationEnvProp, Profile, StandDTO}



case class GeneratorProp(creationEnvProp: CreationEnvProp,
                         stand: StandDTO,
                         profile: Profile
                        )
