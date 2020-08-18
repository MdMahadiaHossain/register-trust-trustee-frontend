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

package navigation

import base.SpecBase
import controllers.register.leadtrustee.organisation.routes._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.leadtrustee.organisation._

class LeadTrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new LeadTrusteeOrganisationNavigator
  val index = 0

  "LeadTrusteeOrganisation Navigator" must {

    "UK registered yes no page -> YES -> Name page" in {
      val answers = emptyUserAnswers
        .set(UkRegisteredYesNoPage(index), true).success.value

      navigator.nextPage(UkRegisteredYesNoPage(index), fakeDraftId, answers)
        .mustBe(NameController.onPageLoad(index, fakeDraftId))
    }

    "UK registered yes no page -> NO -> Name page" in {
      val answers = emptyUserAnswers
        .set(UkRegisteredYesNoPage(index), false).success.value

      navigator.nextPage(UkRegisteredYesNoPage(index), fakeDraftId, answers)
        .mustBe(NameController.onPageLoad(index, fakeDraftId))
    }

    "Name page" when {

      "UK registered" must {
        "-> UTR page" in {
          val answers = emptyUserAnswers
            .set(UkRegisteredYesNoPage(index), true).success.value

          navigator.nextPage(NamePage(index), fakeDraftId, answers)
            .mustBe(UtrController.onPageLoad(index, fakeDraftId))
        }
      }

      "Not UK registered" must {
        "-> Address UK yes no page" in {
          val answers = emptyUserAnswers
            .set(UkRegisteredYesNoPage(index), false).success.value

          navigator.nextPage(NamePage(index), fakeDraftId, answers)
            .mustBe(AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "Is address in UK page -> YES -> UK address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), true).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(UkAddressController.onPageLoad(index, fakeDraftId))
    }

    "Is address in UK page -> NO -> International address page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), false).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(InternationalAddressController.onPageLoad(index, fakeDraftId))
    }

    "UK address page -> Email address yes no page" in {
      navigator.nextPage(UkAddressPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
    }

    "International address page -> Email address yes no page" in {
      navigator.nextPage(InternationalAddressPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Email address yes no page -> YES -> Email address page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage(index), true).success.value

      navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(EmailAddressController.onPageLoad(index, fakeDraftId))
    }

    "Email address yes no page -> NO -> Telephone number page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage(index), false).success.value

      navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(TelephoneNumberController.onPageLoad(index, fakeDraftId))
    }

    "Email address page -> Telephone number page" in {
      navigator.nextPage(EmailAddressPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(TelephoneNumberController.onPageLoad(index, fakeDraftId))
    }

    "Telephone number page -> Check your answers page" in {
      navigator.nextPage(TelephoneNumberPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(CheckDetailsController.onPageLoad(index, fakeDraftId))
    }
  }

}
