// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Input;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;
using PocketCampus.IsAcademia.ViewModels;

namespace PocketCampus.IsAcademia.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();
        }

        private void Period_Tap( object sender, GestureEventArgs e )
        {
            var period = (PeriodInfo) ( (FrameworkElement) sender ).DataContext;
            var converter = (EnumToLocalizedStringConverter) Resources["EnumToString"];
            string typeString = (string) converter.Convert( period.PeriodType, null, null, null );
            string text = typeString + Environment.NewLine + period.Rooms;

            MessageBox.Show( text, period.CourseName, MessageBoxButton.OK );
            Messenger.Send( new EventLogRequest( "ViewPeriodProperties" ) );
        }
    }
}