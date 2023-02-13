package pool

import com.sun.net.httpserver.Filter
import com.sun.net.httpserver.Filter.Chain
import com.sun.net.httpserver.HttpExchange

final class CorsFilter extends Filter:
  override def doFilter(exchange: HttpExchange, chain: Chain): Unit =
    val headers = exchange.getResponseHeaders()
    headers.add("Access-Control-Allow-Origin", "*")
    headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
    headers.add("Access-Control-Allow-Headers", "*")
    headers.add("Access-Control-Allow-Credentials", "true")
    headers.add("Access-Control-Allow-Credentials-Header", "*")

    chain.doFilter(exchange)

  override def description(): String = "Cors Filter"