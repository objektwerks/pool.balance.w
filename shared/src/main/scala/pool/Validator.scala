package pool

object Validator:
  extension (value: String)
    def isLicense: Boolean = if value.nonEmpty && value.length == 36 then true else false
    def isPin: Boolean = value.length == 7
    def isEmailAddress: Boolean = value.nonEmpty && value.length >= 3 && value.contains("@")
    def isName: Boolean = value.length >= 2
    def isInt(text: String): Boolean = text.matches("\\d+")
    def isDouble(text: String): Boolean = text.matches("\\d{0,7}([\\.]\\d{0,4})?")

  extension (value: Int)
    def isGreaterThan1899 = value > 1899
    def isGreaterThan999 = value > 999

  extension (id: Long)
    def isZero: Boolean = id == 0
    def isGreaterThanZero: Boolean = id > 0

  extension (command: Command)
    def isValid: Boolean =
      command match
        case register @ Register(emailAddress)           => register.isValid
        case login @ Login(_, _)                         => login.isValid
        case deactivate @ Deactivate(_)                  => deactivate.isValid
        case reactivate @ Reactivate(_)                  => reactivate.isValid
        case listPools @ ListPools(_)                    => listPools.isValid
        case addPool @ AddPool(_, _)                     => addPool.isValid
        case updatePool @ UpdatePool(_, _)               => updatePool.isValid
        case listCleanings @ ListCleanings(_, _)         => listCleanings.isValid
        case addCleaning @ AddCleaning(_, _)             => addCleaning.isValid
        case updateCleaning @ UpdateCleaning(_, _)       => updateCleaning.isValid
        case listMeasurements @ ListMeasurements(_, _)   => listMeasurements.isValid
        case addMeasurement @ AddMeasurement(_, _)       => addMeasurement.isValid
        case updateMeasurement @ UpdateMeasurement(_, _) => updateMeasurement.isValid
        case listChemicals @ ListChemicals(_, _)         => listChemicals.isValid
        case addChemical @ AddChemical(_, _)             => addChemical.isValid
        case updateChemical @ UpdateChemical(_, _)       => updateChemical.isValid

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

  extension (addPool: AddPool)
    def isValid: Boolean = addPool.license.isLicense && addPool.pool.isValid

  extension (updatePool: UpdatePool)
    def isValid: Boolean = updatePool.license.isLicense && updatePool.pool.isValid

  extension (listCleanings: ListCleanings)
    def isValid: Boolean = listCleanings.license.isLicense

  extension (addCleaning: AddCleaning)
    def isValid: Boolean = addCleaning.license.isLicense && addCleaning.cleaning.isValid

  extension (updateCleaning: UpdateCleaning)
    def isValid: Boolean = updateCleaning.license.isLicense && updateCleaning.cleaning.isValid

  extension (listMeasurements: ListMeasurements)
    def isValid: Boolean = listMeasurements.license.isLicense

  extension (addMeasurement: AddMeasurement)
    def isValid: Boolean = addMeasurement.license.isLicense && addMeasurement.measurement.isValid

  extension (updateMeasurement: UpdateMeasurement)
    def isValid: Boolean = updateMeasurement.license.isLicense && updateMeasurement.measurement.isValid

  extension (listChemicals: ListChemicals)
    def isValid: Boolean = listChemicals.license.isLicense

  extension (addChemical: AddChemical)
    def isValid: Boolean = addChemical.license.isLicense && addChemical.chemical.isValid

  extension (updateChemical: UpdateChemical)
    def isValid: Boolean = updateChemical.license.isLicense && updateChemical.chemical.isValid

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
      chemical.chemical.nonEmpty &&
      chemical.amount > 0.00 &&
      chemical.unit.nonEmpty
      chemical.added > 0
