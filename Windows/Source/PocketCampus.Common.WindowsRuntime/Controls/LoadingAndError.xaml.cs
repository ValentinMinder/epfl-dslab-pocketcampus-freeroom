// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using ThinMvvm;
using Windows.ApplicationModel.Resources;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// Displays loading and error messages.
    /// This should be put directly into a view; no properties need to be set.
    /// </summary>
    public partial class LoadingAndError : UserControl
    {
        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Common.WindowsRuntime/LoadingAndError" );

        private static readonly string StatusBarLoadingText = _resources.GetString( "StatusBarLoading" );

#warning HACK... figure out how to do this properly

        public string LoadingText
        {
            get { return _resources.GetString( "Loading/Text" ); }
        }

        public string ErrorText
        {
            get { return _resources.GetString( "Error/Text" ); }
        }

        public string NetworkErrorText
        {
            get { return _resources.GetString( "NetworkError/Text" ); }
        }


        public LoadingAndError()
        {
            InitializeComponent();

            Loaded += ( _, __ ) =>
            {
                if ( DataContext.GetType().GetRuntimeProperties().Any( p => p.Name == "CacheStatus" ) )
                {
                    ( (INotifyPropertyChanged) DataContext ).PropertyChanged += DataContext_PropertyChanged;
                    SwitchState();
                }
            };
        }


        private void DataContext_PropertyChanged( object sender, PropertyChangedEventArgs e )
        {
            if ( e.PropertyName == "CacheStatus" )
            {
                SwitchState();
            }
        }

        private void SwitchState()
        {
            switch ( (CacheStatus) ( (dynamic) DataContext ).CacheStatus )
            {
                case CacheStatus.NoCache:
                case CacheStatus.OptedOut:
                case CacheStatus.Used:
                case CacheStatus.Unused:
                    RemoveLoadingTray();
                    break;

                case CacheStatus.Loading:
                    Visibility = Visibility.Visible;
                    break;

                case CacheStatus.UsedTemporarily:
                    Visibility = Visibility.Collapsed;
                    SetLoadingTray();
                    break;
            }
        }

        private async void SetLoadingTray()
        {
            var bar = StatusBar.GetForCurrentView();
            bar.ProgressIndicator.Text = StatusBarLoadingText;
            await bar.ProgressIndicator.ShowAsync();
        }

        private async void RemoveLoadingTray()
        {
            var bar = StatusBar.GetForCurrentView();
            bar.ProgressIndicator.Text = string.Empty;
            await bar.ProgressIndicator.HideAsync();
        }
    }
}