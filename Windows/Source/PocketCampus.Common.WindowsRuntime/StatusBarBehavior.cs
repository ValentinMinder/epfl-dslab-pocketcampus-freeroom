using System;
using Microsoft.Xaml.Interactivity;
using Windows.ApplicationModel;
using Windows.UI;
using Windows.UI.ViewManagement;
using Windows.UI.Xaml;

namespace PocketCampus.Common
{
    public class StatusBarBehavior : DependencyObject, IBehavior
    {
        // N.B.: Only indeterminate progress is supported
        // N.B. 2: StatusBar.GetForCurrentView throws a REGDB_E_CLASSNOTREG error if ran in the designer

        #region IsVisible
        public bool IsVisible
        {
            get { return (bool) GetValue( IsVisibleProperty ); }
            set { SetValue( IsVisibleProperty, value ); }
        }

        public static readonly DependencyProperty IsVisibleProperty =
            DependencyProperty.Register( "IsVisible", typeof( bool ), typeof( StatusBarBehavior ), new PropertyMetadata( false, OnIsVisibleChanged ) );

        private static async void OnIsVisibleChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( DesignMode.DesignModeEnabled )
            {
                return;
            }

            if ( (bool) args.NewValue )
            {
                await StatusBar.GetForCurrentView().ShowAsync();
            }
            else
            {
                await StatusBar.GetForCurrentView().HideAsync();
            }
        }
        #endregion

        #region BackgroundOpacity
        public double BackgroundOpacity
        {
            get { return (double) GetValue( BackgroundOpacityProperty ); }
            set { SetValue( BackgroundOpacityProperty, value ); }
        }

        public static readonly DependencyProperty BackgroundOpacityProperty =
            DependencyProperty.Register( "BackgroundOpacity", typeof( double ), typeof( StatusBarBehavior ), new PropertyMetadata( 0.0, OnBackgroundOpacityChanged ) );

        private static void OnBackgroundOpacityChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( !DesignMode.DesignModeEnabled )
            {
                StatusBar.GetForCurrentView().BackgroundOpacity = (double) args.NewValue;
            }
        }
        #endregion

        #region ForegroundColor
        public Color ForegroundColor
        {
            get { return (Color) GetValue( ForegroundColorProperty ); }
            set { SetValue( ForegroundColorProperty, value ); }
        }

        public static readonly DependencyProperty ForegroundColorProperty =
            DependencyProperty.Register( "ForegroundColor", typeof( Color ), typeof( StatusBarBehavior ), new PropertyMetadata( null, OnForegroundColorChanged ) );

        private static void OnForegroundColorChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( !DesignMode.DesignModeEnabled )
            {
                StatusBar.GetForCurrentView().ForegroundColor = (Color) args.NewValue;
            }
        }
        #endregion

        #region BackgroundColor
        public Color BackgroundColor
        {
            get { return (Color) GetValue( BackgroundColorProperty ); }
            set { SetValue( BackgroundColorProperty, value ); }
        }

        public static readonly DependencyProperty BackgroundColorProperty =
            DependencyProperty.Register( "BackgroundColor", typeof( Color ), typeof( StatusBarBehavior ), new PropertyMetadata( null, OnBackgroundColorChanged ) );

        private static void OnBackgroundColorChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( !DesignMode.DesignModeEnabled )
            {
                StatusBar.GetForCurrentView().BackgroundColor = (Color) args.NewValue;
            }
        }
        #endregion

        #region ProgressText
        public string Text
        {
            get { return (string) GetValue( TextProperty ); }
            set { SetValue( TextProperty, value ); }
        }

        public static readonly DependencyProperty TextProperty =
            DependencyProperty.Register( "Text", typeof( string ), typeof( StatusBarBehavior ), new PropertyMetadata( null, OnProgressTextChanged ) );


        private static void OnProgressTextChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( !DesignMode.DesignModeEnabled )
            {
                StatusBar.GetForCurrentView().ProgressIndicator.Text = (string) args.NewValue;
            }
        }
        #endregion

        #region ShowProgress
        public bool ShowProgress
        {
            get { return (bool) GetValue( ShowProgressProperty ); }
            set { SetValue( ShowProgressProperty, value ); }
        }

        public static readonly DependencyProperty ShowProgressProperty =
            DependencyProperty.Register( "ShowProgress", typeof( bool ), typeof( StatusBarBehavior ), new PropertyMetadata( false, OnShowProgressChanged ) );

        private static async void OnShowProgressChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            if ( DesignMode.DesignModeEnabled )
            {
                return;
            }

            if ( (bool) args.NewValue )
            {
                await StatusBar.GetForCurrentView().ProgressIndicator.ShowAsync();
            }
            else
            {
                await StatusBar.GetForCurrentView().ProgressIndicator.ShowAsync();
            }
        }
        #endregion

        #region UseDefaultValues
        public bool UseDefaultValues
        {
            get { return (bool) GetValue( UseDefaultValuesProperty ); }
            set { SetValue( UseDefaultValuesProperty, value ); }
        }

        public static readonly DependencyProperty UseDefaultValuesProperty =
            DependencyProperty.Register( "UseDefaultValues", typeof( bool ), typeof( StatusBarBehavior ), new PropertyMetadata( false, OnUseDefaultValuesChanged ) );

        private static async void OnUseDefaultValuesChanged( DependencyObject obj, DependencyPropertyChangedEventArgs args )
        {
            // N.B.: False is not supported, just don't set it if you don't want the default values.
            if ( DesignMode.DesignModeEnabled )
            {
                return;
            }

            var bar = StatusBar.GetForCurrentView();

            bar.ForegroundColor = (Color) Application.Current.Resources["AppStatusBarForegroundColor"];
            bar.BackgroundColor = (Color) Application.Current.Resources["AppStatusBarBackgroundColor"];
            bar.BackgroundOpacity = 1;
            await bar.ShowAsync();
        }
        #endregion

        public void Attach( DependencyObject associatedObject ) { }
        public void Detach() { }
        public DependencyObject AssociatedObject { get; private set; }
    }
}