package pool

import com.raquo.laminar.api.L.*

object Model:
  val emailAddressVar = Var("")
  val pinVar = Var("")
  val accountVar = Var(Account.empty)
  val poolsVar = Var(List.empty[Pool])
  val cleaningsVar = Var(List.empty[Cleaning])
  val measurementsVar = Var(List.empty[Measurement])
  val chemicalsVar = Var(List.empty[Chemical])