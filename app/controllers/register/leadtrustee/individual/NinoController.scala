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

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import config.annotations.LeadTrusteeIndividual
import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.NinoFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.individual.{TrusteesNamePage, TrusteesNinoPage}
import pages.register.trustees.IsThisLeadTrusteePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.NinoView

import scala.concurrent.{ExecutionContext, Future}

class NinoController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        implicit val frontendAppConfig: FrontendAppConfig,
                                        registrationsRepository: RegistrationsRepository,
                                        @LeadTrusteeIndividual navigator: Navigator,
                                        identify: RegistrationIdentifierAction,
                                        getData: DraftIdRetrievalActionProvider,
                                        requireData: RegistrationDataRequiredAction,
                                        validateIndex : IndexActionFilterProvider,
                                        requiredAnswer: RequiredAnswerActionProvider,
                                        formProvider: NinoFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: NinoView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index : Int, draftId: String) =
      identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteesNamePage(index), routes.NameController.onPageLoad(index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val name = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      val form = formProvider("leadTrustee.individual.nino")

      val preparedForm = request.userAnswers.get(TrusteesNinoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, name))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index,draftId).async {
    implicit request =>

      val name = request.userAnswers.get(TrusteesNamePage(index)).get.toString

      val form = formProvider("leadTrustee.individual.nino")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteesNinoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteesNinoPage(index), draftId, updatedAnswers))
        }
      )
  }
}
