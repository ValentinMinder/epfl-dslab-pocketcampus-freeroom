using Windows.ApplicationModel;

namespace PocketCampus.Map.Views
{
    public sealed partial class MainView
    {
        public MainView()
        {
            InitializeComponent();

            if ( !DesignMode.DesignModeEnabled )
            {
                MapTokenLoader.SetToken( Map, "ms-appx:///PocketCampus.Map.WindowsRuntime/MapToken.txt" );
            }
        }
    }
}