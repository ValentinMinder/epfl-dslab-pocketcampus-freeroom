// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Windows;
using System.Windows.Input;
using PocketCampus.Common;
using PocketCampus.Common.Controls;
using PocketCampus.IsAcademia.Models;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

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
            var period = (Period) ( (FrameworkElement) sender ).DataContext;
            var converter = (EnumToLocalizedStringConverter) Resources["EnumToString"];
            string typeString = (string) converter.Convert( period.PeriodType, null, null, null );
            string text = typeString + Environment.NewLine + string.Join( ", ", period.Rooms );

            MessageBox.Show( text, period.CourseName, MessageBoxButton.OK );
            Messenger.Send( new EventLogRequest( "ViewPeriodProperties", period.CourseName ) );
        }
    }
}