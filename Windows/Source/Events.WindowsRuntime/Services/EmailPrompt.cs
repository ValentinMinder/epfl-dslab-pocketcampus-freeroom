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