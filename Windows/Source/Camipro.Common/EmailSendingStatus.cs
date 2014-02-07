// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace Epfl.Camipro
{
    /// <summary>
    /// The result of an e-mail request.
    /// </summary>
    public enum EmailSendingStatus
    {
        /// <summary>
        /// Nothing was requested (yet).
        /// </summary>
        NoneRequested,
        /// <summary>
        /// The request successfully completed.
        /// </summary>
        Success,
        /// <summary>
        /// There was an error during the request execution.
        /// </summary>
        Error
    }
}