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

package views.register.leadtrustee.individual

import forms.trustees.EmailAddressFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.individual.EmailAddressView

class EmailAddressViewSpec extends QuestionViewBehaviours[String] {

  val prefix = "emailAddress"
  val name = FullName("FirstName", None, "LastName")
  val index = 0

  override val form: Form[String] = new EmailAddressFormProvider().withPrefix(prefix)

  val view: EmailAddressView = viewFor[EmailAddressView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, fakeDraftId, index, name.toString)(fakeRequest, messages)

  "EmailAddress View" must {

    behave like dynamicTitlePage(applyView(form), prefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(form, applyView,
      prefix, Some(name.toString))

    behave like pageWithASubmitButton(applyView(form))

  }
}
