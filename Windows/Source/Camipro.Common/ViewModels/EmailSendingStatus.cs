// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Camipro.ViewModels
{
    /// <summary>
    /// Display-friendly version of MailRequestResult and ResponseStatus.
    /// </summary>
    public enum EmailSendingStatus
    {
        /// <summary>
        /// Nothing was requested (yet).
        /// </summary>
        NoneRequested,

        /// <summary>
        /// The request was successfully completed.
        /// </summary>
        Success,

        /// <summary>
        /// An error occurred while executing the request.
        /// </summary>
        Error
    }
}