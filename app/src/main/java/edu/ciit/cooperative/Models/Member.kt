package edu.ciit.cooperative.Models

data class Member(
    val email: String,
    val id: String,
    val name: String,
    val password: String,
    val profileImage: String?
) {
    constructor() : this("", "", "", "", "")
}