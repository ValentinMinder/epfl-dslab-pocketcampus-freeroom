// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IEmailPrompt

#if DEBUG
using System.Threading.Tasks;

namespace PocketCampus.Events.Services.Design
{
    public sealed class DesignEmailPrompt : IEmailPrompt
    {
        public Task<string> GetEmailAsync()
        {
            return Task.FromResult( "john.doe@example.com" );
        }
    }
}
#endif