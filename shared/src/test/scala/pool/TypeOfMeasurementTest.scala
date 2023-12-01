package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import TypeOfMeasurement.*

final class TypeOfMeasurementTest extends AnyFunSuite with Matchers:
  test("type of measurement"):
    toEnum(TotalChlorine.display) shouldBe TotalChlorine
