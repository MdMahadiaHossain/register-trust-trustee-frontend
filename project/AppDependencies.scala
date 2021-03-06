import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "logback-json-logger"            % "4.6.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.52.0-play-26",
    "uk.gov.hmrc"       %% "play-health"                    % "3.14.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                        % "8.8.0-play-26",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.2.0-play-26",
    "uk.gov.hmrc"       %% "domain"                         % "5.9.0-play-26",
    "uk.gov.hmrc"       %% "bootstrap-play-26"              % "1.4.0",
    "uk.gov.hmrc"       %% "play-whitelist-filter"          % "3.1.0-play-26",
    "com.typesafe.play" %% "play-json-joda"                 % "2.7.4"
 )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"            %% "scalatest"             % "3.0.7",
    "org.scalatestplus.play"   %% "scalatestplus-play"    % "3.1.2",
    "org.pegdown"              %  "pegdown"               % "1.6.0",
    "org.jsoup"                %  "jsoup"                 % "1.10.3",
    "com.typesafe.play"        %% "play-test"             % PlayVersion.current,
    "org.mockito"              %  "mockito-all"           % "1.10.19",
    "org.scalacheck"           %% "scalacheck"            % "1.14.0",
    "wolfendale"               %% "scalacheck-gen-regexp" % "0.1.2",
    "com.github.tomakehurst"   % "wiremock-standalone"    % "2.25.1"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.5.23"
  val akkaHttpVersion = "10.0.15"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  )

}
