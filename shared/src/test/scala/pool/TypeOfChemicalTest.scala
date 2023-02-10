package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import TypeOfChemical.*

class TypeOfChemicalTest extends AnyFunSuite with Matchers:
  test("type of chemical") {
    valueOf(LiquidChlorine.toString) shouldBe LiquidChlorine
    valueOf(Trichlor.toString) shouldBe Trichlor
    valueOf(Dichlor.toString) shouldBe Dichlor
    valueOf(CalciumHypochlorite.toString) shouldBe CalciumHypochlorite
    valueOf(Stabilizer.toString) shouldBe Stabilizer
    valueOf(Algaecide.toString) shouldBe Algaecide
    valueOf(MuriaticAcid.toString) shouldBe MuriaticAcid
    valueOf(Salt.toString) shouldBe Salt

    toEnum(LiquidChlorine.display) shouldBe LiquidChlorine
    toEnum(Trichlor.display) shouldBe Trichlor
    toEnum(Dichlor.display) shouldBe Dichlor
    toEnum(CalciumHypochlorite.display) shouldBe CalciumHypochlorite
    toEnum(Stabilizer.display) shouldBe Stabilizer
    toEnum(Algaecide.display) shouldBe Algaecide
    toEnum(MuriaticAcid.display) shouldBe MuriaticAcid
    toEnum(Salt.display) shouldBe Salt
 }