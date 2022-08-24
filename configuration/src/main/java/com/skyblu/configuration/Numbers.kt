package com.skyblu.configuration

/**
 * Number of millis for a task to timeout
 */
const val TIMEOUT_MILLIS = 10000

/**
 * Number of jumps to be loaded at a time
 */
const val JUMP_PAGE_SIZE = 5

/**
 * Number of users to be loaded at a time
 */
const val USER_PAGE_SIZE = 15

/**
 * Range for valid jump numbers
 */
val JUMP_NUMBER_RANGE = 1..10000

/**
 * The size of a 1MB file
 */
const val ONE_MEGABYTE: Long = 1024 * 1024