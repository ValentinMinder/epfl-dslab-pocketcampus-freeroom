// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using PocketCampus.Common.Resources;
using ThinMvvm;

namespace PocketCampus.Common.Controls
{
    /// <summary>
    /// Displays loading and error messages.
    /// This should be put directly into a view; no properties need to be set.
    /// </summary>
    public partial class LoadingAndError : UserControl
    {
        private bool _isShowingTray;
        private ProgressIndicator _currentIndicator;
        private ProgressIndicator _oldIndicator;
        private bool _oldIsVisible;

        public LoadingAndError()
        {
            InitializeComponent();

            Loaded += ( _, __ ) =>
            {
                if ( DataContext.GetType().GetProperty( "CacheStatus" ) != null )
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
                case CacheStatus.NoData:
                    if ( ( (dynamic) DataContext ).DataStatus == DataStatus.Loading )
                    {
                        Visibility = Visibility.Visible;
                    }
                    else
                    {
                        RemoveLoadingTray();
                    }
                    break;

                case CacheStatus.OptedOut:
                case CacheStatus.Used:
                case CacheStatus.Unused:
                    RemoveLoadingTray();
                    break;

                case CacheStatus.UsedTemporarily:
                    Visibility = Visibility.Collapsed;
                    SetLoadingTray();
                    break;
            }
        }

        private void SetLoadingTray()
        {
            _isShowingTray = true;

            var page = (PhoneApplicationPage) ( (PhoneApplicationFrame) Application.Current.RootVisual ).Content;

            if ( _currentIndicator != null && SystemTray.GetProgressIndicator( page ) == _currentIndicator )
            {
                return;
            }

            _oldIndicator = SystemTray.GetProgressIndicator( page );
            _oldIsVisible = SystemTray.GetIsVisible( page );

            _currentIndicator = new ProgressIndicator
            {
                IsIndeterminate = true,
                IsVisible = true,
                Text = CommonResources.Loading
            };
            SystemTray.SetProgressIndicator( page, _currentIndicator );
            SystemTray.SetIsVisible( page, true );
        }

        private void RemoveLoadingTray()
        {
            if ( !_isShowingTray )
            {
                return;
            }
            _isShowingTray = false;

            var page = (PhoneApplicationPage) ( (PhoneApplicationFrame) Application.Current.RootVisual ).Content;

            SystemTray.SetProgressIndicator( page, _oldIndicator );
            SystemTray.SetIsVisible( page, _oldIsVisible );
        }
    }
}