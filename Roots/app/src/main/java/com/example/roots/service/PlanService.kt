package com.example.roots.service

import com.example.roots.model.Plan
import com.example.roots.repository.PlanRepository

class PlanService(private val repo: PlanRepository) {
    fun crear(plan: Plan) = repo.add(plan)
    fun obtener(id: String, onResult: (Plan?) -> Unit) = repo.get(id, onResult)
    fun actualizar(plan: Plan) = repo.update(plan)
    fun eliminar(id: String) = repo.delete(id)
}
