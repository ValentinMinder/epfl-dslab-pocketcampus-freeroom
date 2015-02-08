// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using Windows.ApplicationModel;
using Windows.Graphics.Display;
using Windows.UI;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

namespace PocketCampus.Common.Controls
{
    [TemplateVisualState( GroupName = "OrientationStates", Name = "Portrait" )]
    [TemplateVisualState( GroupName = "OrientationStates", Name = "Landscape" )]
    public abstract class PageBase : Page
    {
        #region SupportsLandscape
        public bool SupportsLandscape
        {
            get { return (bool) GetValue( SupportsLandscapeProperty ); }
            set { SetValue( SupportsLandscapeProperty, value ); }
        }

        public static readonly DependencyProperty SupportsLandscapeProperty =
            DependencyProperty.Register( "SupportsLandscape", typeof( bool ), typeof( PageBase ), new PropertyMetadata( false, OnSupportsLandscapeChanged ) );

        private static void OnSupportsLandscapeChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            ( (PageBase) obj ).SetSupportedOrientations();
        }
        #endregion

        #region ShowStatusBar
        public bool ShowStatusBar
        {
            get { return (bool) GetValue( ShowStatusBarProperty ); }
            set { SetValue( ShowStatusBarProperty, value ); }
        }

        public static readonly DependencyProperty ShowStatusBarProperty =
            DependencyProperty.Register( "ShowStatusBar", typeof( bool ), typeof( PageBase ), new PropertyMetadata( true, OnStatusBarPropertyChanged ) );
        #endregion

        #region StatusBarText
        public string StatusBarText
        {
            get { return (string) GetValue( StatusBarTextProperty ); }
            set { SetValue( StatusBarTextProperty, value ); }
        }

        public static readonly DependencyProperty StatusBarTextProperty =
            DependencyProperty.Register( "StatusBarText", typeof( string ), typeof( PageBase ), new PropertyMetadata( "", OnStatusBarPropertyChanged ) );
        #endregion

        #region StatusBarShowsText
        public bool StatusBarShowsText
        {
            get { return (bool) GetValue( StatusBarShowsTextProperty ); }
            set { SetValue( StatusBarShowsTextProperty, value ); }
        }

        public static readonly DependencyProperty StatusBarShowsTextProperty =
            DependencyProperty.Register( "StatusBarShowsText", typeof( bool ), typeof( PageBase ), new PropertyMetadata( false, OnStatusBarPropertyChanged ) );
        #endregion

        #region StatusBarShowsProgress
        public bool StatusBarShowsProgress
        {
            get { return (bool) GetValue( StatusBarShowsProgressProperty ); }
            set { SetValue( StatusBarShowsProgressProperty, value ); }
        }

        public static readonly DependencyProperty StatusBarShowsProgressProperty =
            DependencyProperty.Register( "StatusBarShowsProgress", typeof( bool ), typeof( PageBase ), new PropertyMetadata( false, OnStatusBarPropertyChanged ) );
        #endregion

        #region DesignDataContext
        public object DesignDataContext
        {
            get { return GetValue( DesignDataContextProperty ); }
            set { SetValue( DesignDataContextProperty, value ); }
        }

        public static readonly DependencyProperty DesignDataContextProperty =
            DependencyProperty.Register( "DesignDataContext", typeof( object ), typeof( PageBase ), new PropertyMetadata( null, OnDesignDataContextChanged ) );

        private static void OnDesignDataContextChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( DesignMode.DesignModeEnabled )
            {
                ( (PageBase) obj ).DataContext = args.NewValue;
            }
        }
        #endregion


        protected PageBase()
        {
            if ( DesignMode.DesignModeEnabled )
            {
                return; // weird errors happen otherwise
            }

            InitializeStatusBar();
            SizeChanged += ( _, __ ) => UpdateOrientation();
            Background = (Brush) Application.Current.Resources["ApplicationPageBackgroundThemeBrush"];
        }


        protected override void OnNavigatedTo( NavigationEventArgs e )
        {
            SetSupportedOrientations();
            UpdateOrientation();

            base.OnNavigatedTo( e );
        }


        private async void InitializeStatusBar()
        {
            var bar = StatusBar.GetForCurrentView();
            bar.ForegroundColor = (Color) Application.Current.Resources["AppStatusBarForegroundColor"];
            bar.BackgroundColor = (Color) Application.Current.Resources["AppStatusBarBackgroundColor"];
            bar.BackgroundOpacity = 1;
            await bar.ShowAsync();
        }

        private async void UpdateStatusBar()
        {
            var bar = StatusBar.GetForCurrentView();

            if ( ShowStatusBar )
            {
                await bar.ShowAsync();
            }
            else
            {
                await bar.HideAsync();
            }

            if ( StatusBarShowsProgress || StatusBarShowsText )
            {
                await bar.ProgressIndicator.ShowAsync();
                bar.ProgressIndicator.ProgressValue = StatusBarShowsProgress ? (double?) null : 0.0;
                bar.ProgressIndicator.Text = StatusBarShowsText ? StatusBarText : "";
            }
            else
            {
                await bar.ProgressIndicator.HideAsync();
            }
        }

        private void SetSupportedOrientations()
        {
            var orientations = DisplayOrientations.Portrait;

            if ( SupportsLandscape )
            {
                orientations |= DisplayOrientations.Landscape | DisplayOrientations.LandscapeFlipped;
            }

            DisplayInformation.AutoRotationPreferences = orientations;
        }

        private void UpdateOrientation()
        {
            if ( SupportsLandscape )
            {
                var orientation = ApplicationView.GetForCurrentView().Orientation;
                VisualStateManager.GoToState( this, orientation.ToString(), true );
            }
            else
            {
                VisualStateManager.GoToState( this, "Portrait", true );
            }
        }

        private static void OnStatusBarPropertyChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( DesignMode.DesignModeEnabled )
            {
                return; // weird errors happen otherwise
            }

            ( (PageBase) obj ).UpdateStatusBar();
        }
    }
}