package edu.ciit.cooperative.Models

data class Member(
    val email: String,
    val id: String?,
    val name: String,
    val password: String,
    val profileImage: String?,
    @field:JvmField val isAbleToLoan: Boolean,
    @field:JvmField val isShareholder: Boolean,
    val totalLoans: Double,
    val totalShares: Double,
    val totalContributions: Int

) {

    constructor() : this("", "", "", "", "", false, false, 0.0, 0.0, 0)
}