package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import TypeOfMeasurement.*

final class TypeOfMeasurementTest extends AnyFunSuite with Matchers:
  test("type of measurement"):
    toEnum(TotalChlorine.display) shouldBe TotalChlorine
    toEnum(FreeChlorine.display) shouldBe FreeChlorine
    toEnum(CombinedChlorine.display) shouldBe CombinedChlorine
    toEnum(Ph.display) shouldBe Ph
    toEnum(CalciumHardness.display) shouldBe CalciumHardness
    toEnum(TotalAlkalinity.display) shouldBe TotalAlkalinity
    toEnum(CyanuricAcid.display) shouldBe CyanuricAcid
    toEnum(TotalBromine.display) shouldBe TotalBromine
    toEnum(Salt.display) shouldBe Salt
    toEnum(Temperature.display) shouldBe Temperature