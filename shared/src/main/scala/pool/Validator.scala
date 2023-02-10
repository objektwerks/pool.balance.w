package pool

object Validator:
  extension (value: String)
    def isLicense: Boolean = if value.nonEmpty && value.length == 36 then true else false
    def isPin: Boolean = value.length == 7
    def isEmailAddress: Boolean = value.nonEmpty && value.length >= 3 && value.contains("@")
    def isInt(text: String): Boolean = text.matches("\\d+")
    def isDouble(text: String): Boolean = text.matches("\\d{0,7}([\\.]\\d{0,4})?")

  extension (command: Command)
    def isValid: Boolean =
      command match
        case register @ Register(emailAddress)         => register.isValid
        case login @ Login(_, _)                       => login.isValid
        case deactivate @ Deactivate(_)                => deactivate.isValid
        case reactivate @ Reactivate(_)                => reactivate.isValid
        case listPools @ ListPools(_)                  => listPools.isValid
        case savePool @ SavePool(_, _)                 => savePool.isValid
        case listCleanings @ ListCleanings(_, _)       => listCleanings.isValid
        case saveCleaning @ SaveCleaning(_, _)         => saveCleaning.isValid
        case listMeasurements @ ListMeasurements(_, _) => listMeasurements.isValid
        case saveMeasurement @ SaveMeasurement(_, _)   => saveMeasurement.isValid
        case listChemicals @ ListChemicals(_, _)       => listChemicals.isValid
        case saveChemical @ SaveChemical(_, _)         => saveChemical.isValid

  extension (register: Register)
    def isValid: Boolean = register.emailAddress.isEmailAddress

  extension (login: Login)
    def isValid: Boolean = login.emailAddress.isEmailAddress && login.pin.isPin

  extension (deactivate: Deactivate)
    def isValid: Boolean = deactivate.license.isLicense

  extension (reactivate: Reactivate)
    def isValid: Boolean = reactivate.license.isLicense

  extension (listPools: ListPools)
    def isValid: Boolean = listPools.license.isLicense

  extension (savePool: SavePool)
    def isValid: Boolean = savePool.license.isLicense && savePool.pool.isValid

  extension (listCleanings: ListCleanings)
    def isValid: Boolean = listCleanings.license.isLicense

  extension (saveCleaning: SaveCleaning)
    def isValid: Boolean = saveCleaning.license.isLicense && saveCleaning.cleaning.isValid

  extension (listMeasurements: ListMeasurements)
    def isValid: Boolean = listMeasurements.license.isLicense

  extension (saveMeasurement: SaveMeasurement)
    def isValid: Boolean = saveMeasurement.license.isLicense && saveMeasurement.measurement.isValid

  extension (listChemicals: ListChemicals)
    def isValid: Boolean = listChemicals.license.isLicense

  extension (saveChemical: SaveChemical)
    def isValid: Boolean = saveChemical.license.isLicense && saveChemical.chemical.isValid

  extension  (license: License)
    def isLicense: Boolean = license.license.isLicense

  extension (account: Account)
    def isActivated: Boolean =
      account.id >= 0 &&
      account.license.isLicense &&
      account.emailAddress.isEmailAddress &&
      account.pin.isPin &&
      account.activated > 0 &&
      account.deactivated == 0
    def isDeactivated: Boolean =
      account.id > 0 &&
      account.license.isLicense &&
      account.emailAddress.isEmailAddress &&
      account.pin.isPin &&
      account.activated == 0 &&
      account.deactivated > 0

  extension (pool: Pool)
    def isValid =
      pool.id >= 0 &&
      pool.license.isLicense &&
      pool.name.nonEmpty &&
      pool.volume > 1000 &&
      pool.unit.nonEmpty

  extension (cleaning: Cleaning)
    def isValid: Boolean =
      cleaning.id >= 0 &&
      cleaning.poolId > 0 &&
      cleaning.cleaned > 0

  extension (measurement: Measurement)
    def isValid: Boolean =
      import Measurement.*

      measurement.id >= 0 &&
      measurement.poolId > 0 &&
      totalChlorineRange.contains(measurement.totalChlorine) &&
      freeChlorineRange.contains(measurement.freeChlorine) &&
      combinedChlorineRange.contains(measurement.combinedChlorine) &&
      (measurement.ph >= 6.2 && measurement.ph <= 8.4) &&
      calciumHardnessRange.contains(measurement.calciumHardness) &&
      totalAlkalinityRange.contains(measurement.totalAlkalinity) &&
      cyanuricAcidRange.contains(measurement.cyanuricAcid) &&
      totalBromineRange.contains(measurement.totalBromine) &&
      saltRange.contains(measurement.salt) &&
      temperatureRange.contains(measurement.temperature) &&
      measurement.measured > 0

  extension (chemical: Chemical)
    def isValid: Boolean =
      chemical.id >= 0 &&
      chemical.poolId > 0 &&
      chemical.typeof.nonEmpty &&
      chemical.amount > 0.00 &&
      chemical.unit.nonEmpty
      chemical.added > 0