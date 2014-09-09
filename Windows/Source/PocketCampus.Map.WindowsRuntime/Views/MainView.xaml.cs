using Windows.UI.Xaml.Controls;

namespace PocketCampus.Map.Views
{
    public sealed partial class MainView : Page
    {
        public MainView()
        {
            InitializeComponent();

            MapTokenLoader.SetToken( Map, "ms-appx:///PocketCampus.Map.WindowsRuntime/MapToken.txt" );
        }
    }
}