using System.Windows;
using System.Windows.Data;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Controls;

namespace PocketCampus.Events.Views
{
    public partial class SettingsView : BasePage
    {
        public SettingsView()
        {
            InitializeComponent();
        }

        private void PeriodPicker_Loaded( object sender, System.Windows.RoutedEventArgs e )
        {
            // HACK: The ListPicker has a bug; setting SelectedItem before SelectedIndex is set crashes
            var binding = new Binding
            {
                Path = new PropertyPath( "Settings.SearchPeriod" ),
                Mode = BindingMode.TwoWay
            };
            ( (ListPicker) sender ).SetBinding( ListPicker.SelectedItemProperty, binding );
        }
    }
}