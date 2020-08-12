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

package controllers.register.trustees.organisation

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.trustees.TelephoneNumberFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.trustees.organisation.TrusteeOrgNamePage
import pages.register.trustees.{IsThisLeadTrusteePage, TelephoneNumberPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trustees.organisation.TrusteeOrgTelephoneNumberView

import scala.concurrent.{ExecutionContext, Future}

class TrusteeOrgTelephoneNumberController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           navigator: Navigator,
                                           validateIndex: IndexActionFilterProvider,
                                           identify: RegistrationIdentifierAction,
                                           getData: DraftIdRetrievalActionProvider,
                                           requireData: RegistrationDataRequiredAction,
                                           requiredAnswer: RequiredAnswerActionProvider,
                                           formProvider: TelephoneNumberFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: TrusteeOrgTelephoneNumberView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteeOrgNamePage(index), routes.TrusteeBusinessNameController.onPageLoad(index, draftId))) andThen
      requiredAnswer(RequiredAnswer(IsThisLeadTrusteePage(index), controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val trusteeName = request.userAnswers.get(TrusteeOrgNamePage(index)).get

      val messagePrefix: String = getMessagePrefix(index, request)

      val form = formProvider(messagePrefix)

      val preparedForm = request.userAnswers.get(TelephoneNumberPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, messagePrefix, trusteeName))
  }

  private def getMessagePrefix(index: Int, request: RegistrationDataRequest[AnyContent]) = {
    val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index)).get

    val messagePrefix = if (isLead) {
      "leadTrusteesTelephoneNumber"
    } else {
      "telephoneNumber"
    }
    messagePrefix
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val trusteeName = request.userAnswers.get(TrusteeOrgNamePage(index)).get

      val messagePrefix: String = getMessagePrefix(index, request)

      val form = formProvider(messagePrefix)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, messagePrefix, trusteeName))),

        value => {
          val answers = request.userAnswers.set(TelephoneNumberPage(index), value)

          for {
            updatedAnswers <- Future.fromTry(answers)
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TelephoneNumberPage(index), draftId, updatedAnswers))
        }
      )
  }
}
