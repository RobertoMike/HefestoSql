rootProject.name = "HefestoSql"
include("hibernate")
include("shared")
include("benchmarks")

project(":shared").name = "hefesto-base"
project(":hibernate").name = "hefesto-hibernate"
project(":benchmarks").name = "hefesto-benchmarks"
