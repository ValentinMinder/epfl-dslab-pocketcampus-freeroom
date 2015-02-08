// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Threading.Tasks;
using PocketCampus.Events.Dialogs;

namespace PocketCampus.Events.Services
{
    public sealed class EmailPrompt : IEmailPrompt
    {
        public async Task<string> GetEmailAsync()
        {
            var dialog = new EmailPromptDialog();
            await dialog.ShowAsync();
            return dialog.Email;
        }
    }
}