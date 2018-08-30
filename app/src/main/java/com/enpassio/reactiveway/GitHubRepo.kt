package com.enpassio.reactiveway

/**
 * Created by Greta Grigutė on 2018-08-30.
 */
data class GitHubRepo(val id: Int,
                      val name: String,
                      val htmlUrl: String,
                      val description: String,
                      val language: String,
                      val stargazersCount: Int)