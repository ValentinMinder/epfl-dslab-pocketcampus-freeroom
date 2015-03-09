// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.Threading.Tasks;
namespace PocketCampus.Events.Services
{
    /// <summary>
    /// Prompts the user for their e-mail address.
    /// </summary>
    public interface IEmailPrompt
    {
        /// <summary>
        /// Gets the user's e-mail address.
        /// </summary>
        Task<string> GetEmailAsync();
    }
}