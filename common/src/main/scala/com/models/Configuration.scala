package com.models

case class ApplicationConf(host: String, port: Int)

case class Configurations(app: ApplicationConf,
                          AWSConfig: AWSConfig,
                          jwtScalaCirce: JWTConfig,
                          dbConfig: DBConfig,
                          akka: AkkaConfig)

case class AWSConfig(accessKey: String,
                     secretKey: String,
                     sourceBucketName: String,
                     region: String,
                     serviceEndpoint: String)

case class JWTConfig(key: String, expireDurationHours: Int)

case class DBConfig(profile: String,
                    driver: String,
                    url: String,
                    user: String,
                    password: String,
                    schema: String,
                    threadsPoolCount: Int,
                    queueSize: Int)

case class AkkaConfig(futureAwaitDurationMins: Int, akkaWorkersCount: Int)

case class MailingList(host: String,
                       port: String,
                       userName: String,
                       password: String,
                       protocol: String)
