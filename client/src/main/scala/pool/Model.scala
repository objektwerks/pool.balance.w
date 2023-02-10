package pool

import com.raquo.laminar.api.L.*

object Model:
  val accountVar = Var(Account.empty)
  val poolsVar = Var(List.empty[Pool])