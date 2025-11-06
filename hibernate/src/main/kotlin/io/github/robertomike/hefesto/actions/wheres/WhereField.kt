package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.Operator

class WhereField : Where {
    var parentField: String? = null

    constructor(field: String, parentField: String) : super(field) {
        this.parentField = parentField
    }

    constructor(field: String, operator: Operator, parentField: String) : super(field) {
        this.operator = operator
        this.parentField = parentField
    }
}
