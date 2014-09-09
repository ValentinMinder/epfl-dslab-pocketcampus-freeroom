using PocketCampus.Common.Controls;
using Windows.ApplicationModel;

namespace PocketCampus.Map.Views
{
    public sealed partial class MainView : PageBase
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