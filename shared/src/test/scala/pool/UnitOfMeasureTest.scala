package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import UnitOfMeasure.*

class UnitOfMeasureTest extends AnyFunSuite with Matchers:
  test("unit of measure") {
    valueOf(gl.toString) shouldBe gl
    valueOf(l.toString) shouldBe l
    valueOf(kg.toString) shouldBe kg
    valueOf(lb.toString) shouldBe lb
    valueOf(tablet.toString) shouldBe tablet
    toList.length shouldBe 5
    toPoolList.length shouldBe 2
  }

  test("gallons to liters") {
    gallonsToLiters(1.0) shouldBe 3.785
    gallonsToLiters(2.0) shouldBe 7.57
 }

  test("liters to gallons") {
    litersToGallons(1.0) shouldBe 0.264
    litersToGallons(2.0) shouldBe 0.528
  }

  test("pounds to kilograms") {
    poundsToKilograms(1.0) shouldBe 0.454
  }

  test("kilograms to pounds") {
    kilogramsToPounds(1.0) shouldBe 2.205
    kilogramsToPounds(2.0) shouldBe 4.41
  }