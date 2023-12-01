package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import TypeOfChemical.*

final class TypeOfChemicalTest extends AnyFunSuite with Matchers:
  test("type of chemical"):
    toEnum(LiquidChlorine.display) shouldBe LiquidChlorine
    toEnum(Trichlor.display) shouldBe Trichlor
    toEnum(Dichlor.display) shouldBe Dichlor
    toEnum(CalciumHypochlorite.display) shouldBe CalciumHypochlorite
    toEnum(Stabilizer.display) shouldBe Stabilizer
    toEnum(Algaecide.display) shouldBe Algaecide
    toEnum(MuriaticAcid.display) shouldBe MuriaticAcid
    toEnum(Salt.display) shouldBe Salt