// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using PocketCampus.IsAcademia.Models;
using ThinMvvm;
using ThinMvvm.Logging;
using SysWinInput = System.Windows.Input;

namespace PocketCampus.IsAcademia.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        private void Period_Tap( object sender, SysWinInput.GestureEventArgs e )
        {
            var period = (Period) ( (FrameworkElement) sender ).DataContext;
            var converter = (EnumToLocalizedStringConverter) Resources["EnumToString"];
            string typeString = (string) converter.Convert( period.PeriodType, null, null, null );
            string text = typeString + Environment.NewLine + string.Join( ", ", period.Rooms );

            MessageBox.Show( text, period.CourseName, MessageBoxButton.OK );
            Messenger.Send( new EventLogRequest( "ViewPeriodProperties", period.CourseName ) );
        }

        // Hide the application bar (it can't be minimized) and system tray in landscape mode
        // (can't be done from visual states for some reason)
        private void Page_OrientationChanged( object sender, OrientationChangedEventArgs e )
        {
            bool isPortrait = Orientation.HasFlag( PageOrientation.Portrait );
            ApplicationBar.IsVisible = isPortrait;
            SystemTray.SetIsVisible( this, isPortrait );
        }
    }
}