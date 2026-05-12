package it.umbria.regione.cartellini.model

class DataModel(employees: Iterable<Employee> = emptyList()) {

    private val empList = mutableListOf<Employee>()

    init {
        empList.addAll(employees)
    }

    val employees: List<Employee>
        get() = empList

    val stampings: List<Stamping>
        get() = empList.flatMap { it.stampings }

    fun findEmployeeByName(name: String): Employee {
        val d = empList.find { it.name == name }
        if (d == null) {
            val employee = Employee(name)
            empList.add(employee)
            return employee
        }
        return d
    }

    fun cleanUnfinishedStampings() {
        empList.forEach { it.cleanUnfinishedStampings() }
    }
}
