/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helpers

import auth.{Authorisation, Authorised, Authority, NotAuthorised}
import connectors.AuthConnector
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mock.MockitoSugar
import play.api.mvc.Result
import play.api.mvc.Results._
import services.AuditService
import uk.gov.hmrc.play.auth.microservice.connectors.ConfidenceLevel
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.Future

object AuthHelper extends MockitoSugar {

  val oid = "foo"
  val uri = s"""/x/y/$oid"""
  val userDetailsLink = "bar"
  val mockAuthConnector = mock[AuthConnector]
  val mockAuditService = mock[AuditService]
  val mockHttp = mock[HttpGet with HttpPost]

  implicit val hc = HeaderCarrier()

  def authorityBuilder(confidenceLevel: ConfidenceLevel): Option[Authority] = Some(Authority(uri, oid, userDetailsLink, confidenceLevel))

  def setUp(authority: Option[Authority], affinityGroup: Option[String]): Unit = {
    when(mockAuthConnector.getCurrentAuthority()(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(authority))
    when(mockAuthConnector.getAffinityGroup(Matchers.anyString)(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(affinityGroup))
  }

  def mockGetCurrentAuthority(response: HttpResponse): Unit =
    when(mockHttp.GET[HttpResponse](Matchers.eq("localhost/auth/authority"))(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(response))

  def mockGetAffinityGroupResponse(response: HttpResponse): Unit =
    when(mockHttp.GET[HttpResponse](Matchers.eq(s"localhost$uri"))(Matchers.any(), Matchers.any())).thenReturn(Future.successful(response))

  object Authorities {
    val userCL0 = authorityBuilder(ConfidenceLevel.L0)
    val userCL50 = authorityBuilder(ConfidenceLevel.L50)
    val userCL100 = authorityBuilder(ConfidenceLevel.L100)
    val userCL200 = authorityBuilder(ConfidenceLevel.L200)
    val userCL300 = authorityBuilder(ConfidenceLevel.L300)
    val userCL500 = authorityBuilder(ConfidenceLevel.L500)
  }

  object AffinityGroups {
   val organisation = Some("Organisation")
  }
}
