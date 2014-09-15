﻿using System;
using System.Windows.Input;
using ThinMvvm;
using Windows.ApplicationModel.Resources;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace PocketCampus.Common.Controls
{
    public sealed class DataStatusDisplay : Control
    {
        #region DataStatus
        public DataStatus Data
        {
            get { return (DataStatus) GetValue( DataProperty ); }
            set { SetValue( DataProperty, value ); }
        }

        public static readonly DependencyProperty DataProperty =
            DependencyProperty.Register( "Data", typeof( DataStatus ), typeof( DataStatusDisplay ), new PropertyMetadata( DataStatus.NoData ) );
        #endregion

        #region CacheStatus
        public CacheStatus? Cache
        {
            get { return (CacheStatus?) GetValue( CacheProperty ); }
            set { SetValue( CacheProperty, value ); }
        }

        public static readonly DependencyProperty CacheProperty =
            DependencyProperty.Register( "Cache", typeof( CacheStatus? ), typeof( DataStatusDisplay ), new PropertyMetadata( null, OnCacheChanged ) );

        private static void OnCacheChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (DataStatusDisplay) obj ).Update();
        }
        #endregion

        #region RetryCommand
        public ICommand RetryCommand
        {
            get { return (ICommand) GetValue( RetryCommandProperty ); }
            set { SetValue( RetryCommandProperty, value ); }
        }

        public static readonly DependencyProperty RetryCommandProperty =
            DependencyProperty.Register( "RetryCommand", typeof( ICommand ), typeof( DataStatusDisplay ), new PropertyMetadata( null ) );
        #endregion

        #region TextStyle
        public Style TextStyle
        {
            get { return (Style) GetValue( TextStyleProperty ); }
            set { SetValue( TextStyleProperty, value ); }
        }

        public static readonly DependencyProperty TextStyleProperty =
            DependencyProperty.Register( "TextStyle", typeof( Style ), typeof( DataStatusDisplay ), new PropertyMetadata( null ) );
        #endregion


        private static readonly ResourceLoader _resources = ResourceLoader.GetForViewIndependentUse( "PocketCampus.Common.WindowsRuntime/DataStatusDisplay" );


        private bool _progressIndicatorWasVisible;
        private double? _oldProgressIndicatorValue;
        private string _oldProgressIndicatorText;


        public DataStatusDisplay()
        {
            DefaultStyleKey = typeof( DataStatusDisplay );
        }


        private void Update()
        {
            if ( !Cache.HasValue )
            {
                throw new InvalidOperationException( "Do not manually set StatusDisplay.Cache to null." );
            }

            switch ( Cache.Value )
            {
                case CacheStatus.NoData:
                    Visibility = Visibility.Visible;
                    break;

                case CacheStatus.Used:
                case CacheStatus.UsedTemporarily:
                    Visibility = Visibility.Collapsed;
                    ShowProgressIndicator();
                    break;

                case CacheStatus.OptedOut:
                case CacheStatus.Unused:
                    Visibility = Visibility.Collapsed;
                    HideProgressIndicator();
                    break;
            }
        }

        private async void ShowProgressIndicator()
        {
            var progressIndicator = StatusBar.GetForCurrentView().ProgressIndicator;

            // HACK: There's no IsVisible on ProgressIndicator, but we're always displaying text, so...
            _progressIndicatorWasVisible = progressIndicator.Text != null;
            _oldProgressIndicatorValue = progressIndicator.ProgressValue;
            _oldProgressIndicatorText = progressIndicator.Text;

            if ( Data == DataStatus.Loading )
            {
                progressIndicator.ProgressValue = null;
            }
            else
            {
                progressIndicator.ProgressValue = 0;
            }

            progressIndicator.Text = _resources.GetString( "StatusBar" + Data.ToString() );

            await progressIndicator.ShowAsync();
        }

        private async void HideProgressIndicator()
        {
            var progressIndicator = StatusBar.GetForCurrentView().ProgressIndicator;

            progressIndicator.ProgressValue = _oldProgressIndicatorValue;
            progressIndicator.Text = _oldProgressIndicatorText;

            if ( !_progressIndicatorWasVisible )
            {
                await progressIndicator.HideAsync();
            }
        }
    }
}