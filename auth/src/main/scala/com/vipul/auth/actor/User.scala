package com.vipul.auth.actor

import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import com.typesafe.config.Config
import scala.concurrent.duration._

object Guardian {

  def apply(weatherPorts: Seq[Int]): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { context =>
      val settings = UserSettings(context.system)

      (1 until settings.weatherStations).foreach { n =>
        val wsid = n.toString
        // choose one of the HTTP API nodes to report to
        val weatherPort = weatherPorts(n % weatherPorts.size)

        context.spawn(
          Behaviors.supervise(
            WeatherStation(wsid, settings, weatherPort)
          ).onFailure[RuntimeException](SupervisorStrategy.restartWithBackoff(1.second, 5.seconds, 0.5)),
          s"weather-station-$wsid")
      }
      Behaviors.empty
    }
  }
}
object UserSettings {

  def apply(system: ActorSystem[_]): UserSettings = {
    apply(system.settings.config.getConfig("killrweather.fog"))
  }

  def apply(config: Config): UserSettings = {
    import akka.util.Helpers.Requiring

    val millis = (durationKey: String) =>
      config.getDuration(durationKey).toMillis.millis
        .requiring(_ > Duration.Zero, s"'$durationKey' must be > 0")

    UserSettings(config.getInt("initial-weather-stations"),
      config.getString("weather-station.hostname"),
      millis("weather-station.sample-interval")
    )
  }
}

final case class UserSettings(weatherStations: Int,
                              host: String,
                              sampleInterval: FiniteDuration)
