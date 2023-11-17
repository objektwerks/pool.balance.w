package pool

import java.util.UUID

object License:
  def newInstance: String = UUID.randomUUID.toString