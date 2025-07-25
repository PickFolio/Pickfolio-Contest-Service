package com.pickfolio.contest.constant;

/**
 * Represents the lifecycle status of a contest.
 */
public enum ContestStatus {
    /**
     * Accepting new participants; portfolios are editable.
     */
    OPEN,

    /**
     * Contest has started, portfolios are locked for the MVP.
     */
    LIVE,

    /**
     * Contest has finished, and a winner can be determined.
     */
    COMPLETED,

    /**
     * The contest was cancelled before it could go live.
     */
    CANCELLED
}