package io.xpk.apidb

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.HandlerMapping

@RestController
class ApiController {

  @RequestMapping
  fun api(webRequest: ServletWebRequest): Any {
    return webRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST) as String
  }
}
