Pool Balance ( Web Edition )
----------------
>***WIP*** Pool cleaning, measurement and chemical balancing distributed web app using ScalaJs, Scala3 and PostgreSql.

Install
-------
1. brew install node
2. npm install ( in project root directory )
>See **package.json** for installable dependencies.

Build
-----
1. npm install ( only when package.json changes )
2. sbt clean compile fastLinkJS
>See **js/target/public** directory.

Dev
---
1. sbt jvm/run ( new session, curl -v http://localhost:7272/now )
2. sbt ( new session )
3. ~ js/fastLinkJS
4. npx snowpack dev ( new session )
>Edits are reflected in the **fastLinkJS** and **snowpack** sessions.
>See **snowpack.config.json** and [Snowpack Config](https://www.snowpack.dev/reference/configuration) for configurable options.

Package Server
--------------
>See sbt-native-packager ( www.scala-sbt.org/sbt-native-packager/formats/universal.html )
1. sbt clean test fullLinkJS
2. sbt jvm/universal:packageZipTarball | sbt 'show graalvm-native-image:packageBin'
>**Optionally** execute Graalvm image: ./jvm/target/graalvm-native-image/scala.graalvm

Package Client
--------------
1. sbt clean test fullLinkJS
2. npx snowpack build
> See **build** directory.

Features
--------
1. dashboard
2. cleanings
3. measurements
4. chemicals
5. charts
6. database

Use Cases
---------
1. clean pool and components
2. measure pool chemical content
3. add chemicals to pool

Measurements
------------
>Measured in ppm ( parts per million ).

| Measurement                       | Range     | Good        | Ideal |
|-----------------------------------|-----------|-------------|-------|
| total chlorine (tc = fc + cc)     | 0 - 10    | 1 - 5       | 3     |
| free chlorine (fc)                | 0 - 10    | 1 - 5       | 3     |
| combinded chlorine (cc = tc - fc) | 0.0 - 0.5 | 0.0 - 0.2   | 0.0   |
| ph                                | 6.2 - 8.4 | 7.2 - 7.6   | 7.4   |
| calcium hardness                  | 0 - 1000  | 250 - 500   | 375   |
| total alkalinity                  | 0 - 240   | 80 - 120    | 100   |
| cyanuric acid                     | 0 - 300   | 30 - 100    | 50    |
| total bromine                     | 0 - 20    | 2 - 10      | 5     |
| salt                              | 0 - 3600  | 2700 - 3400 | 3200  |
| temperature                       | 50 - 100  | 75 - 85     | 82    |

Chemicals
---------
* Liquids measured in: gallons ( gl ) and liters ( l ).
* Granules measured in: pounds ( lbs ) and kilograms ( kg ).
1. LiquidChlorine ( gl/l )
2. Trichlor ( tablet )
3. Dichlor ( lbs/kg )
4. CalciumHypochlorite ( lbs/kg )
5. Stabilizer ( lbs/kg )
6. Algaecide ( gl/l )
7. MuriaticAcid ( gl/l )
8. Salt ( lbs/kg )

Entity
------
* Pool 1 --> * Cleaning | Measurement | Chemical

Model
-----
* Client 1 --> 1 Context
* Conext 1 --> 1 Model | View
* Model 1 --> 1 Proxy
* View 1 --> 1 Context

Charts
------
* cleanings - line chart ( x = cleaned, y = ? )
* measurements - line chart ( x = measured, y = measurement )
* chemicals - bar chart ( x = added, y = amount/typeof )

Postgresql
----------
1. config:
    1. on osx intel: /usr/local/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
    2. on osx m1: /opt/homebrew/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
2. build.sbt:
    1. IntegrationTest / javaOptions += "-Dquill.binds.log=true"
3. run:
    1. brew services start postgresql
4. logs:
    1. on osx intel: /usr/local/var/log/postgres.log
    2. on m1: /opt/homebrew/var/log/postgres.log

Database
--------
>Example database url: postgresql://localhost:5432/poolbalance?user=mycomputername&password=poolbalance"
1. psql postgres
2. CREATE DATABASE poolbalance OWNER [your computer name];
3. GRANT ALL PRIVILEGES ON DATABASE poolbalance TO [your computer name];
4. \l
5. \q
6. psql poolbalance
7. \i ddl.sql
8. \q

DDL
---
>Alternatively run: psql -d poolbalance -f ddl.sql
1. psql poolbalance
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database poolbalance;
3. \q

Environment
-----------
>The following environment variables must be defined:
* export POOL_BALANCE_HOST="127.0.0.1"
* export POOL_BALANCE_PORT=7272

* export POOL_BALANCE_POSTGRESQL_URL="jdbc:postgresql://localhost:5432/poolbalance"
* export POOL_BALANCE_POSTGRESQL_USER="yourusername"
* export POOL_BALANCE_POSTGRESQL_PASSWORD="poolbalance"
* export POOL_BALANCE_POSTGRESQL_DRIVER="org.postgresql.ds.PGSimpleDataSource"
* export POOL_BALANCE_POSTGRESQL_DB_NAME="poolbalance"
* export POOL_BALANCE_POSTGRESQL_HOST="127.0.0.1"
* export POOL_BALANCE_POSTGRESQL_PORT=5432

* export POOL_BALANCE_EMAIL_HOST="your-email.provider.com"
* export POOL_BALANCE_EMAIL_ADDRESS="your-email@provider.com"
* export POOL_BALANCE_EMAIL_PASSWORD="your-email-password"

License
-------
>Copyright (c) [2023] [Objektwerks]

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    * http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
