using Windows.UI.Xaml.Controls;

namespace PocketCampus.Events.Dialogs
{
    public sealed partial class EmailPromptDialog : ContentDialog
    {
        public string Email { get; private set; }


        public EmailPromptDialog()
        {
            InitializeComponent();
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