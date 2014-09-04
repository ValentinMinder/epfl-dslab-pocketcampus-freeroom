// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IEmailPrompt

#if DEBUG
namespace PocketCampus.Events.Services.Design
{
    public sealed class DesignEmailPrompt : IEmailPrompt
    {
        public string GetEmail()
        {
            return "john.doe@example.com";
        }
    }
}
#endif