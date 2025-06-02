package com.example.roots.repository

import com.example.roots.model.Plan
import com.google.firebase.firestore.FirebaseFirestore

class PlanRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("planes")

    fun add(plan: Plan) {
        collection.document(plan.id.toString()).set(plan)
    }

    fun get(id: Int, onResult: (Plan?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Plan::class.java))
        }
    }

    fun update(plan: Plan) {
        collection.document(plan.id.toString()).set(plan)
    }

    fun delete(id: Int) {
        collection.document(id.toString()).delete()
    }
}
