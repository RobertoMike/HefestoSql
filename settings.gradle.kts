rootProject.name = "HefestoSql"
include("hibernate-criteria-builder")
include("hibernate-query-language")
include("hibernate")
include("shared")
include("benchmarks")

project(":shared").name = "hefesto-base"
project(":hibernate-criteria-builder").name = "hefesto-hibernate"
project(":hibernate-query-language").name = "hefesto-hibernate-hql"
project(":hibernate").name = "hefesto-hibernate-base"
project(":benchmarks").name = "hefesto-benchmarks"
