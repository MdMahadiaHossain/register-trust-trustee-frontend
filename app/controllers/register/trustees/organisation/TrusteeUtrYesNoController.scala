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

import config.annotations.TrusteeOrganisation
import controllers.actions._
import forms.YesNoFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.trustees.organisation.TrusteeUtrYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trustees.organisation.TrusteeUtrYesNoView

import scala.concurrent.{ExecutionContext, Future}

class TrusteeUtrYesNoController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           @TrusteeOrganisation navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           formProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: TrusteeUtrYesNoView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form: Form[Boolean] = formProvider.withPrefix("leadTrusteeUtrYesNo")

      val preparedForm = request.userAnswers.get(TrusteeUtrYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form: Form[Boolean] = formProvider.withPrefix("leadTrusteeUtrYesNo")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TrusteeUtrYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TrusteeUtrYesNoPage(index), draftId, updatedAnswers))
        }
      )
  }
}
