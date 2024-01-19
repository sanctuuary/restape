package nl.esciencecenter.restape;

/**
 * Enumeration of various types of software licenses.
 * This enum classifies licenses into categories based on their openness
 * and OSI approval status.
 */
public enum LicenseType {
    /**
     * Represents an unknown license type.
     * This value is used when the license type cannot be determined.
     */
    Unknown,

    /**
     * Represents a closed-source license.
     * This indicates proprietary software where the source code is not publicly
     * available.
     */
    Closed,

    /**
     * Represents an open-source license.
     * This indicates software where the source code is publicly available, but it
     * is not necessarily OSI-approved.
     */
    Open,

    /**
     * Represents a license that is approved by the Open Source Initiative (OSI).
     * This indicates software that adheres to the OSI's definition of open source.
     */
    OSI_Approved;
}