package edu.ciit.cooperative.Models

data class Member(
    val email: String,
    val id: String?,
    val name: String,
    val password: String,
    val profileImage: String?,
    @field:JvmField val ableToLoan: Boolean,
    val totalLoans: Double,
    val totalShares: Double,
    val totalContributions: Int

) {

    constructor() : this("", "", "", "", "", false, 0.0, 0.0, 0)
}