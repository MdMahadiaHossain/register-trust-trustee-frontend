/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.register.leadtrustee.organisation

import base.SpecBase
import config.annotations.LeadTrusteeOrganisation
import forms.EmailAddressFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import pages.register.leadtrustee.organisation.{EmailAddressPage, NamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.leadtrustee.organisation.EmailAddressView

class EmailAddressControllerSpec extends SpecBase {

  val index = 0

  lazy val emailAddressRoute: String = routes.EmailAddressController.onPageLoad(index, fakeDraftId).url

  val formProvider = new EmailAddressFormProvider()
  val form: Form[String] = formProvider.withPrefix("leadTrustee.organisation.email")
  val name: String = "Name"

  val validAnswer: String = "email@example.com"

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(NamePage(index), name).success.value

  "EmailAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, emailAddressRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[EmailAddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(EmailAddressPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, emailAddressRoute)

      val view = application.injector.instanceOf[EmailAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeOrganisation]).toInstance(new FakeNavigator())
          )
          .build()

      val request =
        FakeRequest(POST, emailAddressRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, emailAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[EmailAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, name)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, emailAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, emailAddressRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
