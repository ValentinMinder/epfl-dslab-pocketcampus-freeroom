namespace PocketCampus.Transport.Views
{
    public sealed partial class AddStationView
    {
        public AddStationView()
        {
            InitializeComponent();
            //TODO: Find a way to put this in a behavior or something (also, for some reason, Programmatic doesn't work...?)
            Loaded += ( _, __ ) => Box.Focus( Windows.UI.Xaml.FocusState.Pointer );
        }
    }
}