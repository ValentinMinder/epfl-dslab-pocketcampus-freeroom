// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Events.Dialogs
{
    public sealed partial class EmailPromptDialog
    {
        public string Email { get; private set; }


        public EmailPromptDialog()
        {
            InitializeComponent();

            // FRAMEWORK BUG: CustomResource cannot be used on top-level elements like ContentDialog.Title
            var loader = LocalizationHelper.GetLoaderForCurrentAssembly( "EmailPrompt" );
            Title = loader.GetString( "Title" );
            PrimaryButtonText = loader.GetString( "SuccessButton" );
            SecondaryButtonText = loader.GetString( "CancelButton" );

            PrimaryButtonClick += SuccessButtonClick;
            SecondaryButtonClick += CancelButtonClick;
        }


        private void SuccessButtonClick( ContentDialog sender, ContentDialogButtonClickEventArgs e )
        {
            Email = EmailBox.Text;

            if ( string.IsNullOrWhiteSpace( Email ) )
            {
                e.Cancel = true;
            }
        }

        private void CancelButtonClick( ContentDialog sender, ContentDialogButtonClickEventArgs e )
        {
            Email = null;
        }
    }
}